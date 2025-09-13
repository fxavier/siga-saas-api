package com.xavier.sigasaasapi.common.infrastructure.persistence.mapper;

import com.xavier.sigasaasapi.common.domain.audit.AuditEvent;
import com.xavier.sigasaasapi.common.domain.pagination.Page;
import com.xavier.sigasaasapi.common.domain.pagination.PageRequest;
import com.xavier.sigasaasapi.common.domain.pagination.Sort;
import com.xavier.sigasaasapi.common.infrastructure.persistence.entity.AuditEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between AuditEvent domain objects and entities.
 * Handles bidirectional mapping and pagination conversion.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Component
public class AuditEventMapper {

    /**
     * Convert domain AuditEvent to JPA entity.
     * @param domain the domain object
     * @return the JPA entity
     */
    public AuditEventEntity toEntity(AuditEvent domain) {
        if (domain == null) {
            return null;
        }

        AuditEventEntity entity = new AuditEventEntity();
        entity.setId(domain.getId());
        entity.setEventType(domain.getEventType());
        entity.setEntityType(domain.getEntityType());
        entity.setEntityId(domain.getEntityId());
        entity.setAction(domain.getAction());
        entity.setUsername(domain.getUsername());
        entity.setUserIp(domain.getUserIp());
        entity.setUserAgent(domain.getUserAgent());
        entity.setTimestamp(domain.getTimestamp());
        entity.setOldValue(domain.getOldValue());
        entity.setNewValue(domain.getNewValue());
        entity.setMetadata(domain.getMetadata());
        entity.setSuccess(domain.isSuccess());
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setCorrelationId(domain.getCorrelationId());
        entity.setTenantId(domain.getTenantId());

        return entity;
    }

    /**
     * Convert JPA entity to domain AuditEvent.
     * @param entity the JPA entity
     * @return the domain object
     */
    public AuditEvent toDomain(AuditEventEntity entity) {
        if (entity == null) {
            return null;
        }

        AuditEvent domain = new AuditEvent();
        domain.setId(entity.getId());
        domain.setEventType(entity.getEventType());
        domain.setEntityType(entity.getEntityType());
        domain.setEntityId(entity.getEntityId());
        domain.setAction(entity.getAction());
        domain.setUsername(entity.getUsername());
        domain.setUserIp(entity.getUserIp());
        domain.setUserAgent(entity.getUserAgent());
        domain.setTimestamp(entity.getTimestamp());
        domain.setOldValue(entity.getOldValue());
        domain.setNewValue(entity.getNewValue());
        domain.setMetadata(entity.getMetadata());
        domain.setSuccess(entity.isSuccess());
        domain.setErrorMessage(entity.getErrorMessage());
        domain.setCorrelationId(entity.getCorrelationId());
        domain.setTenantId(entity.getTenantId());

        return domain;
    }

    /**
     * Convert list of JPA entities to domain objects.
     * @param entities the list of JPA entities
     * @return list of domain objects
     */
    public List<AuditEvent> toDomainList(List<AuditEventEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of domain objects to JPA entities.
     * @param domains the list of domain objects
     * @return list of JPA entities
     */
    public List<AuditEventEntity> toEntityList(List<AuditEvent> domains) {
        if (domains == null) {
            return new ArrayList<>();
        }

        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Convert domain PageRequest to Spring Data Pageable.
     * @param pageRequest the domain page request
     * @return Spring Data Pageable
     */
    public Pageable toPageable(PageRequest pageRequest) {
        if (pageRequest == null) {
            return Pageable.unpaged();
        }

        if (pageRequest.getSort() != null) {
            List<org.springframework.data.domain.Sort.Order> orders = new ArrayList<>();

            for (Sort.Order order : pageRequest.getSort().getOrders()) {
                org.springframework.data.domain.Sort.Direction direction =
                        order.getDirection() == Sort.Direction.ASC
                                ? org.springframework.data.domain.Sort.Direction.ASC
                                : org.springframework.data.domain.Sort.Direction.DESC;

                orders.add(new org.springframework.data.domain.Sort.Order(
                        direction, order.getProperty()));
            }

            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPageNumber(),
                    pageRequest.getPageSize(),
                    org.springframework.data.domain.Sort.by(orders)
            );
        }

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    /**
     * Convert Spring Data Page to domain Page.
     * @param springPage the Spring Data page
     * @return domain page
     */
    public Page<AuditEvent> toPage(org.springframework.data.domain.Page<AuditEventEntity> springPage) {
        if (springPage == null) {
            return new Page<>(new ArrayList<>(), 0, 0, 0);
        }

        List<AuditEvent> content = springPage.getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new Page<>(
                content,
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements()
        );
    }
}