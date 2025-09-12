package com.xavier.sigasaasapi.common.domain.exception;

/**
 * Exception thrown when a validation fails.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public class ValidationException extends DomainException {
    private final String field;
    private final Object value;

    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    public ValidationException(String field, Object value, String message) {
        super(String.format("Validation failed for field '%s' with value '%s': %s",
                field, value, message));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
