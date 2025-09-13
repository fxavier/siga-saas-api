package com.xavier.sigasaasapi.common.domain.entity;
import java.time.LocalDateTime;

/**
 * Base entity class with audit fields.
 * Provides common audit functionality for entities.
 * Tracks creation and modification information.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public abstract class AuditableEntity<ID> extends BaseEntity<ID> {
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Long version;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Set audit fields for creation.
     * @param username the username of the creator
     */
    public void onCreate(String username) {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.createdBy = username;
        this.updatedAt = now;
        this.updatedBy = username;
        this.version = 0L;
    }

    /**
     * Update audit fields for modification.
     * @param username the username of the modifier
     */
    public void onUpdate(String username) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = username;
    }
}