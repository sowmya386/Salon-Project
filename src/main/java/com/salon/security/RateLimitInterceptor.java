package com.salon.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise-grade simple sliding window rate limiter.
 * Limits users/IPs to 120 requests per minute to prevent DDoS/Spam.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RequestData> requestCounts = new ConcurrentHashMap<>();
    private static final long MAX_REQUESTS_PER_MINUTE = 120;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        long currentTime = Instant.now().toEpochMilli();

        requestCounts.putIfAbsent(clientIp, new RequestData(1L, currentTime));
        RequestData requestData = requestCounts.get(clientIp);

        // Reset memory if a minute has passed
        if (currentTime - requestData.startTime > 60000) {
            requestData.startTime = currentTime;
            requestData.count = 1L;
        } else {
            requestData.count++;
        }

        if (requestData.count > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{ \"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Try again in a minute.\" }");
            return false; // Blocks the request gracefully
        }

        return true;
    }

    private static class RequestData {
        long count;
        long startTime;

        RequestData(long count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }
}
