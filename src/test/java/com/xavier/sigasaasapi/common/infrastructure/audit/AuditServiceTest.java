package com.xavier.sigasaasapi.common.infrastructure.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavier.sigasaasapi.common.domain.audit.AuditEvent;
import com.xavier.sigasaasapi.common.domain.repository.AuditEventRepository;
import com.xavier.sigasaasapi.common.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditService.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEventRepository auditEventRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private ObjectMapper objectMapper;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditEventRepository, securityContext, objectMapper);
    }

    @Test
    void recordCreate_shouldSaveAuditEventWithCorrectData() throws Exception {
        // Given
        String entityType = "User";
        String entityId = "123";
        TestEntity entity = new TestEntity("test");
        String jsonEntity = "{\"name\":\"test\"}";

        when(objectMapper.writeValueAsString(entity)).thenReturn(jsonEntity);
        when(securityContext.getCurrentUsername()).thenReturn(Optional.of("testuser"));
        when(securityContext.getCurrentTenantId()).thenReturn(Optional.of("tenant1"));

        // When
        auditService.recordCreate(entityType, entityId, entity);

        // Then
        Thread.sleep(100); // Wait for async execution

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.CREATE);
        assertThat(savedEvent.getEntityType()).isEqualTo(entityType);
        assertThat(savedEvent.getEntityId()).isEqualTo(entityId);
        assertThat(savedEvent.getNewValue()).isEqualTo(jsonEntity);
        assertThat(savedEvent.getUsername()).isEqualTo("testuser");
        assertThat(savedEvent.getTenantId()).isEqualTo("tenant1");
    }

    @Test
    void recordUpdate_shouldSaveAuditEventWithOldAndNewValues() throws Exception {
        // Given
        String entityType = "User";
        String entityId = "123";
        TestEntity oldEntity = new TestEntity("old");
        TestEntity newEntity = new TestEntity("new");
        String oldJson = "{\"name\":\"old\"}";
        String newJson = "{\"name\":\"new\"}";

        when(objectMapper.writeValueAsString(oldEntity)).thenReturn(oldJson);
        when(objectMapper.writeValueAsString(newEntity)).thenReturn(newJson);
        when(securityContext.getCurrentUsername()).thenReturn(Optional.of("testuser"));

        // When
        auditService.recordUpdate(entityType, entityId, oldEntity, newEntity);

        // Then
        Thread.sleep(100);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.UPDATE);
        assertThat(savedEvent.getOldValue()).isEqualTo(oldJson);
        assertThat(savedEvent.getNewValue()).isEqualTo(newJson);
    }

    @Test
    void recordDelete_shouldSaveAuditEventWithOldValue() throws Exception {
        // Given
        String entityType = "User";
        String entityId = "123";
        TestEntity entity = new TestEntity("test");
        String jsonEntity = "{\"name\":\"test\"}";

        when(objectMapper.writeValueAsString(entity)).thenReturn(jsonEntity);
        when(securityContext.getCurrentUsername()).thenReturn(Optional.of("testuser"));

        // When
        auditService.recordDelete(entityType, entityId, entity);

        // Then
        Thread.sleep(100);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.DELETE);
        assertThat(savedEvent.getOldValue()).isEqualTo(jsonEntity);
        assertThat(savedEvent.getNewValue()).isNull();
    }

    @Test
    void recordLogin_success_shouldSaveSuccessfulLoginEvent() throws Exception {
        // Given
        String username = "testuser";

        // When
        auditService.recordLogin(username, true, null);

        // Then
        Thread.sleep(100);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.LOGIN);
        assertThat(savedEvent.getUsername()).isEqualTo(username);
        assertThat(savedEvent.isSuccess()).isTrue();
        assertThat(savedEvent.getErrorMessage()).isNull();
    }

    @Test
    void recordLogin_failure_shouldSaveFailedLoginEvent() throws Exception {
        // Given
        String username = "testuser";
        String errorMessage = "Invalid credentials";

        // When
        auditService.recordLogin(username, false, errorMessage);

        // Then
        Thread.sleep(100);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.LOGIN_FAILED);
        assertThat(savedEvent.getUsername()).isEqualTo(username);
        assertThat(savedEvent.isSuccess()).isFalse();
        assertThat(savedEvent.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void recordAccessDenied_shouldSaveAccessDeniedEvent() throws Exception {
        // Given
        String resource = "/api/admin";
        String reason = "Insufficient privileges";

        when(securityContext.getCurrentUsername()).thenReturn(Optional.of("testuser"));

        // When
        auditService.recordAccessDenied(resource, reason);

        // Then
        Thread.sleep(100);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.ACCESS_DENIED);
        assertThat(savedEvent.isSuccess()).isFalse();
        assertThat(savedEvent.getMetadata()).containsEntry("resource", resource);
        assertThat(savedEvent.getMetadata()).containsEntry("reason", reason);
    }

    @Test
    void auditEvent_shouldUseCorrelationIdFromMDC() {
        // Given
        String correlationId = "test-correlation-id";
        MDC.put("correlationId", correlationId);

        // Note: recordLogout doesn't call getCurrentUsername() internally
        // so we don't need to stub it

        try {
            // When
            auditService.recordLogout("testuser");

            // Then - In unit tests, @Async runs synchronously, so MDC is preserved
            ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
            verify(auditEventRepository).save(captor.capture());

            AuditEvent savedEvent = captor.getValue();
            // In synchronous execution (unit test), MDC is preserved
            assertThat(savedEvent.getCorrelationId()).isEqualTo(correlationId);
        } finally {
            MDC.clear();
        }
    }

    @Test
    void auditEvent_shouldGenerateCorrelationIdWhenNotInMDC() {
        // Given
        MDC.clear(); // Ensure no correlation ID in MDC
        // REMOVA ESTA LINHA: when(securityContext.getCurrentUsername()).thenReturn(Optional.of("testuser"));

        // When
        auditService.recordLogout("testuser");

        // Then
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getCorrelationId()).isNotNull();
        assertThat(savedEvent.getCorrelationId()).isNotEmpty();
        // Should be a valid UUID
        assertThat(savedEvent.getCorrelationId()).matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
        );
    }

    @Test
    void auditEvent_shouldUseAnonymousWhenNoUserAuthenticated() throws Exception {
        // Given
        when(securityContext.getCurrentUsername()).thenReturn(Optional.empty());

        // When
        auditService.recordLogout(null);

        // Then
        Thread.sleep(100);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, timeout(1000)).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getUsername()).isEqualTo("anonymous");
    }

    @Test
    void recordCreate_whenExceptionOccurs_shouldStillSaveWithToString() {
        // This test verifies that even when JSON serialization fails,
        // the audit event is still saved using toString() fallback

        // Given
        String entityType = "User";
        String entityId = "123";
        TestEntity entity = new TestEntity("test");

        // Mock objectMapper to throw exception during serialization
        try {
            when(objectMapper.writeValueAsString(any()))
                    .thenThrow(new RuntimeException("Serialization failed"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        when(securityContext.getCurrentUsername()).thenReturn(Optional.of("testuser"));

        // When
        assertThatCode(() ->
                auditService.recordCreate(entityType, entityId, entity)
        ).doesNotThrowAnyException();

        // Then - verify save WAS called (with toString value instead of JSON)
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(captor.capture());

        AuditEvent savedEvent = captor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(AuditEvent.EventType.CREATE);
        assertThat(savedEvent.getEntityType()).isEqualTo(entityType);
        assertThat(savedEvent.getEntityId()).isEqualTo(entityId);
        // The newValue will be toString() instead of JSON due to serialization error
        assertThat(savedEvent.getNewValue()).contains("TestEntity");
    }

    // Test entity class
    private static class TestEntity {
        private final String name;

        TestEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "TestEntity{name='" + name + "'}";
        }
    }
}