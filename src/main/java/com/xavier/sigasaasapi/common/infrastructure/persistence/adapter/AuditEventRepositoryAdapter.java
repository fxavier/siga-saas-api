package com.xavier.sigasaasapi.common.infrastructure.persistence.adapter;

import com.xavier.sigasaasapi.common.domain.audit.AuditEvent;
import com.xavier.sigasaasapi.common.domain.pagination.Page;
import com.xavier.sigasaasapi.common.domain.pagination.PageRequest;
import com.xavier.sigasaasapi.common.domain.repository.AuditEventRepository;
import com.xavier.sigasaasapi.common.infrastructure.persistence.JpaAuditEventRepository;
import com.xavier.sigasaasapi.common.infrastructure.persistence.entity.AuditEventEntity;
import com.xavier.sigasaasapi.common.infrastructure.persistence.mapper.AuditEventMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation for AuditEventRepository.
 * Bridges the domain repository interface with JPA implementation.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Repository("auditEventRepository")
@Transactional
public class AuditEventRepositoryAdapter implements AuditEventRepository {

    private final JpaAuditEventRepository jpaRepository;
    private final AuditEventMapper mapper;

    public AuditEventRepositoryAdapter(JpaAuditEventRepository jpaRepository,
                                       AuditEventMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AuditEvent save(AuditEvent event) {
        AuditEventEntity entity = mapper.toEntity(event);
        AuditEventEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AuditEvent> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<AuditEvent> findByUsername(String username) {
        return jpaRepository.findByUsername(username).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByEntityTypeAndEntityId(String entityType, String entityId) {
        return jpaRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByEventType(String eventType) {
        return jpaRepository.findByEventType(eventType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return jpaRepository.findByTimestampBetween(startTime, endTime).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByCorrelationId(String correlationId) {
        return jpaRepository.findByCorrelationId(correlationId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> findBySuccessFalse() {
        return jpaRepository.findBySuccessFalse().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AuditEvent> findAll(PageRequest pageRequest) {
        Pageable pageable = mapper.toPageable(pageRequest);
        org.springframework.data.domain.Page<AuditEventEntity> page = jpaRepository.findAll(pageable);
        return mapper.toPage(page);
    }

    @Override
    public Page<AuditEvent> findByUsername(String username, PageRequest pageRequest) {
        Pageable pageable = mapper.toPageable(pageRequest);
        org.springframework.data.domain.Page<AuditEventEntity> page =
                jpaRepository.findByUsernameWithPagination(username, pageable);
        return mapper.toPage(page);
    }

    @Override
    public int deleteByTimestampBefore(LocalDateTime date) {
        return jpaRepository.deleteOlderThan(date);
    }

    @Override
    public long countByEventType(String eventType) {
        return jpaRepository.countByEventType(eventType);
    }

    @Override
    public long countByUsername(String username) {
        return jpaRepository.countByUsername(username);
    }
}