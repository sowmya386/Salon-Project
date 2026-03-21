package com.salon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.salon.security.JwtAuthFilter;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ⬅️ ADD THIS
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    "/swagger-ui.html",
            			    "/swagger-ui/**",
            			    "/swagger-ui/index.html",
            			    "/api-docs",
            			    "/api-docs/**",
            			    "/v3/api-docs",
            			    "/v3/api-docs/**"
            			).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/admin/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/admin/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/customers/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/customers/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/salons").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/salons").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/bot/chat").permitAll()
                // Public catalog for customer browsing
                .requestMatchers(HttpMethod.GET, "/api/customers/services").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/customers/products").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/super-admin/login").permitAll()
                .requestMatchers("/api/super-admin/**").hasRole("SUPER_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/auth/supabase/exchange").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/payments/plans").permitAll()
                .anyRequest().authenticated()
                
                
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ⬅️ ADD THIS METHOD
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // your React URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}