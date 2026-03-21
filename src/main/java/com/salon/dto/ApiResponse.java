package com.salon.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standardized API response envelope for consistency across all endpoints.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ApiMeta meta,
        String message,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> ok(T data, ApiMeta meta) {
        return new ApiResponse<>(true, data, meta, null, Instant.now());
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, null, message, Instant.now());
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(true, null, null, message, Instant.now());
    }

    /** Pagination metadata for list endpoints */
    public record ApiMeta(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
    ) {
        public static ApiMeta from(int page, int size, long totalElements, int totalPages) {
            return new ApiMeta(
                    page,
                    size,
                    totalElements,
                    totalPages,
                    page < totalPages - 1,
                    page > 0
            );
        }
    }
}
