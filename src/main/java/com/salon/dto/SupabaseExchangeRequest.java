package com.salon.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for exchanging a Supabase access token for a salon JWT.
 * Used when user signs in with Google via Supabase Auth.
 */
public class SupabaseExchangeRequest {

    @NotBlank(message = "Access token is required")
    private String accessToken;

    /**
     * Salon name - required for new customers to associate with a salon.
     * Optional if user already exists (matched by providerId or email).
     */
    private String salonName;

    /**
     * Role for new users: "ROLE_CUSTOMER" (default) or "ROLE_ADMIN".
     * Admins require salon approval; customers do not.
     */
    private String role = "ROLE_CUSTOMER";

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
