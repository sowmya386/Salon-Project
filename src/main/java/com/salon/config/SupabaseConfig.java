package com.salon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Supabase integration (Auth, Google OAuth).
 * Configure in application.properties or via environment variables.
 */
@Configuration
public class SupabaseConfig {

    @Value("${supabase.url:}")
    private String url;

    @Value("${supabase.jwt-secret:}")
    private String jwtSecret;

    public String getUrl() {
        return url;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public boolean isConfigured() {
        return url != null && !url.isBlank() && jwtSecret != null && !jwtSecret.isBlank();
    }
}
