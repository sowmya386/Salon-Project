package com.salon.exception;

/**
 * Thrown when authentication is required or has failed.
 * Maps to HTTP 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
