package com.salon.filter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Rate limits auth endpoints to prevent brute-force attacks.
 */
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final List<String> RATE_LIMITED_PATHS = List.of(
            "/api/auth/admin/login",
            "/api/auth/customers/login",
            "/api/auth/super-admin/login",
            "/api/auth/supabase/exchange",
            "/api/auth/forgot-password"
    );

    private final Map<String, Bucket> buckets;

    public RateLimitFilter(Map<String, Bucket> authRateLimitBuckets) {
        this.buckets = authRateLimitBuckets;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!shouldRateLimit(path) || !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = getClientKey(request);
        Bucket bucket = buckets.computeIfAbsent(clientKey, k -> com.salon.config.RateLimitConfig.createAuthBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            response.setContentType("application/json");
        }
    }

    private boolean shouldRateLimit(String path) {
        return RATE_LIMITED_PATHS.stream().anyMatch(path::equals);
    }

    private String getClientKey(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
