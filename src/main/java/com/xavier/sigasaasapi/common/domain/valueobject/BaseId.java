package com.xavier.sigasaasapi.common.domain.valueobject;
/**
 * Base ID class with generic type.
 * Provides common functionality for ID value objects.
 * Includes equals and hashCode methods based on the ID value.
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */

public abstract class BaseId<T> {
    private final T value;

    protected BaseId(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseId<?> baseId = (BaseId<?>) obj;
        return value.equals(baseId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
