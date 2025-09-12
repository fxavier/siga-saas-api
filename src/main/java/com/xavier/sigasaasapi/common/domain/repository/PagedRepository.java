package com.xavier.sigasaasapi.common.domain.repository;
/**
 * Repository interface for paginated queries.
 * Extends BaseRepository to add pagination support.
 * @param <T> the entity type
 * @param <ID> the ID type
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface PagedRepository<T, ID> extends BaseRepository<T, ID>  {
    /**
     * Find all entities with pagination.
     * @param pageRequest the pagination request
     * @return a page of entities
     */
    Page<T> findAll(PageRequest pageRequest);

    /**
     * Find entities matching a specification with pagination.
     * @param specification the specification to match
     * @param pageRequest the pagination request
     * @return a page of matching entities
     */
    Page<T> findAll(Specification<T> specification, PageRequest pageRequest);
}
