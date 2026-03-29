package com.salon.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Verifies Supabase-issued JWTs (e.g. from Google OAuth sign-in)
 * Uses the Supabase /auth/v1/user endpoint to securely bypass ES256 algorithm limitations.
 */
@Service
public class SupabaseJwtVerifier {

    private final String supabaseUrl;
    private final String anonKey;
    private final RestTemplate restTemplate;

    public SupabaseJwtVerifier(
            @Value("${supabase.url:}") String supabaseUrl,
            @Value("${supabase.anon-key:}") String anonKey) {
        this.supabaseUrl = supabaseUrl;
        this.anonKey = anonKey;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Verifies and extracts user info using Supabase API.
     */
    public SupabaseUserInfo verifyAndExtract(String accessToken) {
        if (supabaseUrl == null || supabaseUrl.isBlank() || anonKey == null || anonKey.isBlank()) {
            throw new IllegalStateException("Supabase URL or Anon Key not configured. Set supabase.url and supabase.anon-key in application.properties");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("apikey", anonKey);

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<SupabaseUserResponse> response = restTemplate.exchange(
                    supabaseUrl + "/auth/v1/user",
                    HttpMethod.GET,
                    request,
                    SupabaseUserResponse.class
            );

            SupabaseUserResponse user = response.getBody();
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("Invalid Supabase token");
            }

            String email = user.getEmail();
            if (email == null || email.isBlank()) {
                email = user.getPhone();
            }

            String fullName = null;
            if (user.getUserMetadata() != null && user.getUserMetadata().containsKey("full_name")) {
                fullName = String.valueOf(user.getUserMetadata().get("full_name"));
            }
            if (fullName == null || fullName.isBlank()) {
                fullName = email != null ? email.split("@")[0] : "User";
            }

            return new SupabaseUserInfo(user.getId(), email, fullName);

        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Invalid or expired Supabase token", e);
        }
    }

    public record SupabaseUserInfo(String providerId, String email, String fullName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SupabaseUserResponse {
        private String id;
        private String email;
        private String phone;
        @JsonProperty("user_metadata")
        private Map<String, Object> userMetadata;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Map<String, Object> getUserMetadata() { return userMetadata; }
        public void setUserMetadata(Map<String, Object> userMetadata) { this.userMetadata = userMetadata; }
    }
}
