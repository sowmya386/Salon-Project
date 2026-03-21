package com.salon.exception;

/**
 * Thrown when a request conflicts with current state (e.g. duplicate resource).
 * Maps to HTTP 409 CONFLICT.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
