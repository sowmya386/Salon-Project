package com.salon.util;

import com.salon.dto.ApiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Utility to wrap Spring Page into standardized ApiResponse with pagination meta.
 */
public final class PageResponseUtil {

    private PageResponseUtil() {
    }

    /** Wrap page content with pagination metadata */
    public static <T> ApiResponse<List<T>> wrap(Page<T> page) {
        ApiResponse.ApiMeta meta = ApiResponse.ApiMeta.from(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ApiResponse.ok(page.getContent(), meta);
    }

    /** Use when content is transformed (e.g. DTO mapping); meta from source page */
    public static <T, R> ApiResponse<R> wrap(R content, Page<T> page) {
        ApiResponse.ApiMeta meta = ApiResponse.ApiMeta.from(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return ApiResponse.ok(content, meta);
    }
}
