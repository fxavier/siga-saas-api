package com.xavier.sigasaasapi.common.domain.exception;
/**
 * Exception thrown when a concurrency conflict occurs.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

public class ConcurrencyException extends DomainException {
    private final String entityName;
    private final Object id;

    public ConcurrencyException(String message) {
        super(message);
        this.entityName = null;
        this.id = null;
    }

    public ConcurrencyException(String entityName, Object id) {
        super(String.format("Concurrency conflict for %s with id: %s", entityName, id));
        this.entityName = entityName;
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public Object getId() {
        return id;
    }
}
