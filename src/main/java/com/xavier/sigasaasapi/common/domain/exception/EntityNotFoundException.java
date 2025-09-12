package com.xavier.sigasaasapi.common.domain.exception;
/**
 * Exception thrown when an entity is not found.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public class EntityNotFoundException extends DomainException {
    private final String entityName;
    private final Object id;

    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s not found with id: %s", entityName, id));
        this.entityName = entityName;
        this.id = id;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityName = null;
        this.id = null;
    }

    public String getEntityName() {
        return entityName;
    }

    public Object getId() {
        return id;
    }
}