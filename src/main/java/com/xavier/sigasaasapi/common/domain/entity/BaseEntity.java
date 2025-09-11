package com.xavier.sigasaasapi.common.domain.entity;

/**
 * Base entity class with generic ID type.
 * Provides common functionality for entities.
 * Includes equals and hashCode methods based on ID.
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */

import java.util.Objects;

public abstract class BaseEntity<ID> {
    private ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
