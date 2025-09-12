package com.xavier.sigasaasapi.common.domain.exception;
/**
 * Exception thrown when a business rule is violated.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

public class BusinessRuleException extends DomainException {
    private final String ruleName;

    public BusinessRuleException(String message) {
        super(message);
        this.ruleName = null;
    }

    public BusinessRuleException(String ruleName, String message) {
        super(String.format("Business rule '%s' violated: %s", ruleName, message));
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }
}