package com.xavier.sigasaasapi.common.domain.repository;
/**
 * Specification interface for creating complex queries.
 * Implements the Specification pattern for combining criteria.
 * @param <T> the entity type
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public interface Specification<T> {

    /**
     * Check if the entity satisfies the specification.
     * @param entity the entity to check
     * @return true if satisfies, false otherwise
     */
    boolean isSatisfiedBy(T entity);

    /**
     * Combine this specification with another using AND logic.
     * @param other the other specification
     * @return a new combined specification
     */
    default Specification<T> and(Specification<T> other) {
        return entity -> this.isSatisfiedBy(entity) && other.isSatisfiedBy(entity);
    }

    /**
     * Combine this specification with another using OR logic.
     * @param other the other specification
     * @return a new combined specification
     */
    default Specification<T> or(Specification<T> other) {
        return entity -> this.isSatisfiedBy(entity) || other.isSatisfiedBy(entity);
    }

    /**
     * Negate this specification.
     * @return a new negated specification
     */
    default Specification<T> not() {
        return entity -> !this.isSatisfiedBy(entity);
    }

    /**
     * Create a specification that always returns true.
     * @param <T> the entity type
     * @return a specification that accepts all entities
     */
    static <T> Specification<T> alwaysTrue() {
        return entity -> true;
    }

    /**
     * Create a specification that always returns false.
     * @param <T> the entity type
     * @return a specification that rejects all entities
     */
    static <T> Specification<T> alwaysFalse() {
        return entity -> false;
    }
}