package com.xavier.sigasaasapi.common.domain.repository;
/**
 * Base repository interface with generic types for entity and ID.
 * Provides common CRUD operations for all repositories.
 * @param <T> the entity type
 * @param <ID> the ID type
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {

    /**
     * Save an entity to the repository.
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);

    /**
     * Save multiple entities to the repository.
     * @param entities the entities to save
     * @return the saved entities
     */
    List<T> saveAll(List<T> entities);

    /**
     * Find an entity by its ID.
     * @param id the ID of the entity
     * @return an Optional containing the entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities in the repository.
     * @return a list of all entities
     */
    List<T> findAll();

    /**
     * Find multiple entities by their IDs.
     * @param ids the IDs of the entities
     * @return a list of found entities
     */
    List<T> findAllById(List<ID> ids);

    /**
     * Check if an entity exists by its ID.
     * @param id the ID to check
     * @return true if exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Update an existing entity.
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);

    /**
     * Delete an entity by its ID.
     * @param id the ID of the entity to delete
     */
    void deleteById(ID id);

    /**
     * Delete an entity.
     * @param entity the entity to delete
     */
    void delete(T entity);

    /**
     * Delete multiple entities.
     * @param entities the entities to delete
     */
    void deleteAll(List<T> entities);

    /**
     * Delete all entities in the repository.
     */
    void deleteAll();

    /**
     * Count the total number of entities.
     * @return the count of entities
     */
    long count();
}
