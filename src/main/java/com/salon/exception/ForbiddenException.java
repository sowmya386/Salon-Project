package com.salon.exception;

/**
 * Thrown when the user is authenticated but lacks permission.
 * Maps to HTTP 403 FORBIDDEN.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
