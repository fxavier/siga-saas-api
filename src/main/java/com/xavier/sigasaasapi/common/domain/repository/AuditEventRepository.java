package com.xavier.sigasaasapi.common.domain.repository;
import com.xavier.sigasaasapi.common.domain.audit.AuditEvent;
import com.xavier.sigasaasapi.common.domain.pagination.Page;
import com.xavier.sigasaasapi.common.domain.pagination.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AuditEvent entities.
 * Provides methods for querying and persisting audit events.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public interface AuditEventRepository {

    /**
     * Save an audit event.
     * @param event the audit event to save
     * @return the saved audit event
     */
    AuditEvent save(AuditEvent event);

    /**
     * Find an audit event by ID.
     * @param id the audit event ID
     * @return Optional containing the audit event if found
     */
    Optional<AuditEvent> findById(Long id);

    /**
     * Find audit events by username.
     * @param username the username
     * @return list of audit events
     */
    List<AuditEvent> findByUsername(String username);

    /**
     * Find audit events by entity type and ID.
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return list of audit events
     */
    List<AuditEvent> findByEntityTypeAndEntityId(String entityType, String entityId);

    /**
     * Find audit events by event type.
     * @param eventType the event type
     * @return list of audit events
     */
    List<AuditEvent> findByEventType(String eventType);

    /**
     * Find audit events within a time range.
     * @param startTime the start time
     * @param endTime the end time
     * @return list of audit events
     */
    List<AuditEvent> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find audit events by correlation ID.
     * @param correlationId the correlation ID
     * @return list of audit events
     */
    List<AuditEvent> findByCorrelationId(String correlationId);

    /**
     * Find audit events by tenant ID.
     * @param tenantId the tenant ID
     * @return list of audit events
     */
    List<AuditEvent> findByTenantId(String tenantId);

    /**
     * Find failed audit events.
     * @return list of failed audit events
     */
    List<AuditEvent> findBySuccessFalse();

    /**
     * Find audit events with pagination.
     * @param pageRequest the pagination request
     * @return page of audit events
     */
    Page<AuditEvent> findAll(PageRequest pageRequest);

    /**
     * Find audit events by username with pagination.
     * @param username the username
     * @param pageRequest the pagination request
     * @return page of audit events
     */
    Page<AuditEvent> findByUsername(String username, PageRequest pageRequest);

    /**
     * Delete audit events older than a specific date.
     * @param date the cutoff date
     * @return number of deleted events
     */
    int deleteByTimestampBefore(LocalDateTime date);

    /**
     * Count audit events by event type.
     * @param eventType the event type
     * @return count of events
     */
    long countByEventType(String eventType);

    /**
     * Count audit events by username.
     * @param username the username
     * @return count of events
     */
    long countByUsername(String username);
}
