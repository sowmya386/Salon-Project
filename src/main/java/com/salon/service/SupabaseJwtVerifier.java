package com.salon.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Verifies Supabase-issued JWTs (e.g. from Google OAuth sign-in).
 * Uses the JWT secret from Supabase project settings (Settings → API → JWT Secret).
 */
@Service
public class SupabaseJwtVerifier {

    private final String jwtSecret;

    public SupabaseJwtVerifier(@Value("${supabase.jwt-secret:}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    /**
     * Verifies and parses a Supabase access token.
     *
     * @param accessToken the JWT from Supabase Auth (e.g. session.access_token)
     * @return claims with sub (user id), email, user_metadata, etc.
     * @throws IllegalArgumentException if token is invalid or verification fails
     */
    public SupabaseUserInfo verifyAndExtract(String accessToken) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("Supabase JWT secret is not configured. Set supabase.jwt-secret in application.properties");
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (Exception e) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        String sub = claims.getSubject();
        String email = claims.get("email", String.class);
        if (email == null || email.isBlank()) {
            email = claims.get("phone", String.class);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> userMetadata = claims.get("user_metadata", Map.class);
        String fullName = null;
        if (userMetadata != null && userMetadata.containsKey("full_name")) {
            fullName = String.valueOf(userMetadata.get("full_name"));
        }
        if (fullName == null || fullName.isBlank()) {
            fullName = email != null ? email.split("@")[0] : "User";
        }

        return new SupabaseUserInfo(sub, email, fullName);
    }

    public record SupabaseUserInfo(String providerId, String email, String fullName) {}
}
