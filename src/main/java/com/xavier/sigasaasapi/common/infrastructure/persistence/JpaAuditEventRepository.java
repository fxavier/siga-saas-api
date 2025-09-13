package com.xavier.sigasaasapi.common.infrastructure.persistence;

import com.xavier.sigasaasapi.common.infrastructure.persistence.entity.AuditEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA repository for AuditEventEntity.
 * Provides database operations for audit event entities.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Repository
public interface JpaAuditEventRepository extends JpaRepository<AuditEventEntity, Long> {

    /**
     * Find audit events by username.
     * @param username the username
     * @return list of audit events
     */
    List<AuditEventEntity> findByUsername(String username);

    /**
     * Find audit events by entity type and ID.
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return list of audit events
     */
    List<AuditEventEntity> findByEntityTypeAndEntityId(String entityType, String entityId);

    /**
     * Find audit events by event type.
     * @param eventType the event type
     * @return list of audit events
     */
    List<AuditEventEntity> findByEventType(String eventType);

    /**
     * Find audit events within a time range.
     * @param startTime the start time
     * @param endTime the end time
     * @return list of audit events
     */
    List<AuditEventEntity> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find audit events by correlation ID.
     * @param correlationId the correlation ID
     * @return list of audit events
     */
    List<AuditEventEntity> findByCorrelationId(String correlationId);

    /**
     * Find audit events by tenant ID.
     * @param tenantId the tenant ID
     * @return list of audit events
     */
    List<AuditEventEntity> findByTenantId(String tenantId);

    /**
     * Find failed audit events.
     * @return list of failed audit events
     */
    List<AuditEventEntity> findBySuccessFalse();

    /**
     * Find audit events by username with pagination.
     * @param username the username
     * @param pageable the pagination information
     * @return page of audit events
     */
    Page<AuditEventEntity> findByUsernameWithPagination(String username, Pageable pageable);

    /**
     * Delete audit events older than a specific date.
     * @param date the cutoff date
     * @return number of deleted events
     */
    @Modifying
    @Query("DELETE FROM AuditEventEntity e WHERE e.timestamp < :date")
    int deleteOlderThan(@Param("date") LocalDateTime date);

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

    /**
     * Find audit events by entity type with pagination.
     * @param entityType the entity type
     * @param pageable the pagination information
     * @return page of audit events
     */
    Page<AuditEventEntity> findByEntityType(String entityType, Pageable pageable);

    /**
     * Find audit events by event type and success status.
     * @param eventType the event type
     * @param success the success status
     * @return list of audit events
     */
    List<AuditEventEntity> findByEventTypeAndSuccess(String eventType, boolean success);

    /**
     * Find audit events by username and event type.
     * @param username the username
     * @param eventType the event type
     * @param pageable the pagination information
     * @return page of audit events
     */
    Page<AuditEventEntity> findByUsernameAndEventType(String username, String eventType, Pageable pageable);

    /**
     * Find audit events by tenant ID and time range.
     * @param tenantId the tenant ID
     * @param startTime the start time
     * @param endTime the end time
     * @param pageable the pagination information
     * @return page of audit events
     */
    Page<AuditEventEntity> findByTenantIdAndTimestampBetween(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable);

    /**
     * Count failed login attempts for a username in a time period.
     * @param username the username
     * @param eventType the event type (LOGIN_FAILED)
     * @param since the start time
     * @return count of failed attempts
     */
    @Query("SELECT COUNT(e) FROM AuditEventEntity e WHERE e.username = :username " +
            "AND e.eventType = :eventType AND e.timestamp >= :since AND e.success = false")
    long countFailedLoginAttempts(
            @Param("username") String username,
            @Param("eventType") String eventType,
            @Param("since") LocalDateTime since);

    /**
     * Find recent audit events by entity.
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param limit the maximum number of results
     * @return list of recent audit events
     */
    @Query(value = "SELECT * FROM audit_events WHERE entity_type = :entityType " +
            "AND entity_id = :entityId ORDER BY timestamp DESC LIMIT :limit",
            nativeQuery = true)
    List<AuditEventEntity> findRecentByEntity(
            @Param("entityType") String entityType,
            @Param("entityId") String entityId,
            @Param("limit") int limit);

    /**
     * Get audit statistics for a time period.
     * @param startTime the start time
     * @param endTime the end time
     * @return list of statistics
     */
    @Query("SELECT e.eventType, COUNT(e), " +
            "SUM(CASE WHEN e.success = true THEN 1 ELSE 0 END) as successCount, " +
            "SUM(CASE WHEN e.success = false THEN 1 ELSE 0 END) as failureCount " +
            "FROM AuditEventEntity e WHERE e.timestamp BETWEEN :startTime AND :endTime " +
            "GROUP BY e.eventType")
    List<Object[]> getAuditStatistics(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}