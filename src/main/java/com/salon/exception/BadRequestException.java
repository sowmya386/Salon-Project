package com.salon.exception;

/**
 * Thrown when the request is malformed or violates business rules.
 * Maps to HTTP 400 BAD_REQUEST.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
