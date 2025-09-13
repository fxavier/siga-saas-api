package com.xavier.sigasaasapi.common.domain.audit;
import com.xavier.sigasaasapi.common.domain.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Audit event entity for tracking system activities.
 * Stores detailed information about user actions and system events.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public class AuditEvent extends BaseEntity<Long> {

    private String eventType;
    private String entityType;
    private String entityId;
    private String action;
    private String username;
    private String userIp;
    private String userAgent;
    private LocalDateTime timestamp;
    private String oldValue;
    private String newValue;
    private Map<String, Object> metadata;
    private boolean success;
    private String errorMessage;
    private String correlationId;
    private String tenantId;

    public AuditEvent() {
        this.metadata = new HashMap<>();
        this.timestamp = LocalDateTime.now();
        this.success = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Builder class for AuditEvent.
     */
    public static class Builder {
        private final AuditEvent auditEvent;

        public Builder() {
            this.auditEvent = new AuditEvent();
        }

        public Builder eventType(String eventType) {
            auditEvent.setEventType(eventType);
            return this;
        }

        public Builder entityType(String entityType) {
            auditEvent.setEntityType(entityType);
            return this;
        }

        public Builder entityId(String entityId) {
            auditEvent.setEntityId(entityId);
            return this;
        }

        public Builder action(String action) {
            auditEvent.setAction(action);
            return this;
        }

        public Builder username(String username) {
            auditEvent.setUsername(username);
            return this;
        }

        public Builder userIp(String userIp) {
            auditEvent.setUserIp(userIp);
            return this;
        }

        public Builder userAgent(String userAgent) {
            auditEvent.setUserAgent(userAgent);
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            auditEvent.setTimestamp(timestamp);
            return this;
        }

        public Builder oldValue(String oldValue) {
            auditEvent.setOldValue(oldValue);
            return this;
        }

        public Builder newValue(String newValue) {
            auditEvent.setNewValue(newValue);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            auditEvent.setMetadata(metadata);
            return this;
        }

        public Builder addMetadata(String key, Object value) {
            auditEvent.getMetadata().put(key, value);
            return this;
        }

        public Builder success(boolean success) {
            auditEvent.setSuccess(success);
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            auditEvent.setErrorMessage(errorMessage);
            return this;
        }

        public Builder correlationId(String correlationId) {
            auditEvent.setCorrelationId(correlationId);
            return this;
        }

        public Builder tenantId(String tenantId) {
            auditEvent.setTenantId(tenantId);
            return this;
        }

        public AuditEvent build() {
            return auditEvent;
        }
    }

    /**
     * Common audit event types.
     */
    public static class EventType {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String LOGIN_FAILED = "LOGIN_FAILED";
        public static final String ACCESS_DENIED = "ACCESS_DENIED";
        public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
        public static final String PERMISSION_CHANGE = "PERMISSION_CHANGE";
        public static final String DATA_EXPORT = "DATA_EXPORT";
        public static final String DATA_IMPORT = "DATA_IMPORT";
        public static final String CONFIGURATION_CHANGE = "CONFIGURATION_CHANGE";
    }
}
