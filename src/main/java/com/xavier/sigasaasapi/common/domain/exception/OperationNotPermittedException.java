package com.xavier.sigasaasapi.common.domain.exception;
/**
 * Exception thrown when an operation is not permitted.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

public class OperationNotPermittedException extends DomainException {
    private final String operation;

    public OperationNotPermittedException(String message) {
        super(message);
        this.operation = null;
    }

    public OperationNotPermittedException(String operation, String reason) {
        super(String.format("Operation '%s' not permitted: %s", operation, reason));
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}