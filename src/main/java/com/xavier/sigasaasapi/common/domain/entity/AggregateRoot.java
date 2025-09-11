package com.xavier.sigasaasapi.common.domain.entity;
/**
 * Aggregate root base class with generic ID type.
 * Inherits from BaseEntity to provide common functionality for aggregate roots.
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */

public abstract class AggregateRoot<ID> extends BaseEntity<ID> {
}
