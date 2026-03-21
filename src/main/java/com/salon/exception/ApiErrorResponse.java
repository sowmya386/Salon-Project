package com.salon.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Standardized API error response for enterprise consistency.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final int status;
    private final String code;
    private final String message;
    private final String path;
    private final String traceId;
    private final Instant timestamp;
    private final List<FieldError> errors;

    public ApiErrorResponse(int status, String code, String message, String path, String traceId, List<FieldError> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
        this.traceId = traceId;
        this.timestamp = Instant.now();
        this.errors = errors;
    }

    /** Simplified constructor for backward compatibility */
    public ApiErrorResponse(int status, String message) {
        this(status, "ERROR", message, null, null, null);
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getTraceId() {
        return traceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public record FieldError(String field, String message) {}
}
