package com.salon.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting for auth endpoints to prevent brute-force and abuse.
 * In-memory implementation; for multi-instance use Redis-backed Bucket4j.
 */
@Configuration
public class RateLimitConfig {

    /** 10 requests per minute per IP for auth endpoints */
    private static final int CAPACITY = 10;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);

    @Bean
    public Map<String, Bucket> authRateLimitBuckets() {
        return new ConcurrentHashMap<>();
    }

    public static Bucket createAuthBucket() {
        Bandwidth limit = Bandwidth.classic(CAPACITY, Refill.greedy(CAPACITY, REFILL_DURATION));
        return Bucket.builder().addLimit(limit).build();
    }
}
