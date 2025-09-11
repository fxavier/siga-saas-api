package com.xavier.sigasaasapi.common.domain.exception;
/**
 * Custom exception class for domain-related errors.
 * Extends RuntimeException to represent unchecked exceptions in the domain layer.
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
