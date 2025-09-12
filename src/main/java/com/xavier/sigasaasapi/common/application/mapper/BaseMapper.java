package com.xavier.sigasaasapi.common.application.mapper;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base mapper interface for converting between entities and DTOs.
 * @param <E> the entity type
 * @param <D> the DTO type
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public interface BaseMapper<E, D> {

    /**
     * Convert an entity to a DTO.
     * @param entity the entity to convert
     * @return the corresponding DTO
     */
    D toDto(E entity);

    /**
     * Convert a DTO to an entity.
     * @param dto the DTO to convert
     * @return the corresponding entity
     */
    E toEntity(D dto);

    /**
     * Convert a list of entities to a list of DTOs.
     * @param entities the entities to convert
     * @return the list of DTOs
     */
    default List<D> toDtoList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of DTOs to a list of entities.
     * @param dtos the DTOs to convert
     * @return the list of entities
     */
    default List<E> toEntityList(List<D> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing entity with data from a DTO.
     * @param dto the DTO with update data
     * @param entity the entity to update
     * @return the updated entity
     */
    E updateEntity(D dto, E entity);
}