package com.xavier.sigasaasapi.common.infrastructure.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavier.sigasaasapi.common.domain.audit.AuditEvent;
import com.xavier.sigasaasapi.common.domain.repository.AuditEventRepository;
import com.xavier.sigasaasapi.common.security.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Service for recording audit events.
 * Provides methods to log various types of audit events asynchronously.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditEventRepository auditEventRepository;
    private final SecurityContext securityContext;
    private final ObjectMapper objectMapper;

    public AuditService(AuditEventRepository auditEventRepository,
                        SecurityContext securityContext,
                        ObjectMapper objectMapper) {
        this.auditEventRepository = auditEventRepository;
        this.securityContext = securityContext;
        this.objectMapper = objectMapper;
    }

    /**
     * Record a create event.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordCreate(String entityType, String entityId, Object entity) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType(AuditEvent.EventType.CREATE)
                    .entityType(entityType)
                    .entityId(entityId)
                    .action("Created " + entityType)
                    .newValue(objectToJson(entity))
                    .build();

            enrichAndSave(event);
        } catch (Exception e) {
            logger.error("Failed to record create audit event", e);
        }
    }

    /**
     * Record an update event.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordUpdate(String entityType, String entityId, Object oldEntity, Object newEntity) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType(AuditEvent.EventType.UPDATE)
                    .entityType(entityType)
                    .entityId(entityId)
                    .action("Updated " + entityType)
                    .oldValue(objectToJson(oldEntity))
                    .newValue(objectToJson(newEntity))
                    .build();

            enrichAndSave(event);
        } catch (Exception e) {
            logger.error("Failed to record update audit event", e);
        }
    }

    /**
     * Record a delete event.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordDelete(String entityType, String entityId, Object entity) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType(AuditEvent.EventType.DELETE)
                    .entityType(entityType)
                    .entityId(entityId)
                    .action("Deleted " + entityType)
                    .oldValue(objectToJson(entity))
                    .build();

            enrichAndSave(event);
        } catch (Exception e) {
            logger.error("Failed to record delete audit event", e);
        }
    }

    /**
     * Record a login event.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLogin(String username, boolean success, String errorMessage) {
        try {
            AuditEvent.Builder builder = AuditEvent.builder()
                    .eventType(success ? AuditEvent.EventType.LOGIN : AuditEvent.EventType.LOGIN_FAILED)
                    .action(success ? "User login successful" : "User login failed")
                    .username(username)
                    .success(success);

            if (!success && errorMessage != null) {
                builder.errorMessage(errorMessage);
            }

            enrichAndSave(builder.build());
        } catch (Exception e) {
            logger.error("Failed to record login audit event", e);
        }
    }

    /**
     * Record a logout event.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLogout(String username) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType(AuditEvent.EventType.LOGOUT)
                    .action("User logout")
                    .username(username)
                    .build();

            enrichAndSave(event);
        } catch (Exception e) {
            logger.error("Failed to record logout audit event", e);
        }
    }

    /**
     * Record an access denied event.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAccessDenied(String resource, String reason) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType(AuditEvent.EventType.ACCESS_DENIED)
                    .action("Access denied to resource")
                    .addMetadata("resource", resource)
                    .addMetadata("reason", reason)
                    .success(false)
                    .build();

            enrichAndSave(event);
        } catch (Exception e) {
            logger.error("Failed to record access denied audit event", e);
        }
    }

    /**
     * Record a custom audit event.
     */
    @Async("auditExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordCustomEvent(String eventType, String action, Map<String, Object> metadata) {
        try {
            AuditEvent event = AuditEvent.builder()
                    .eventType(eventType)
                    .action(action)
                    .metadata(metadata)
                    .build();

            enrichAndSave(event);
        } catch (Exception e) {
            logger.error("Failed to record custom audit event", e);
        }
    }

    /**
     * Enrich audit event with context information and save.
     */
    private void enrichAndSave(AuditEvent event) {
        // Add user information
        if (event.getUsername() == null) {
            event.setUsername(securityContext.getCurrentUsername().orElse("anonymous"));
        }

        // Add tenant information
        securityContext.getCurrentTenantId().ifPresent(event::setTenantId);

        // Add correlation ID
        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        event.setCorrelationId(correlationId);

        // Add request information
        addRequestInfo(event);

        // Set timestamp if not already set
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }

        // Save the event
        auditEventRepository.save(event);

        logger.debug("Audit event recorded: {} - {}", event.getEventType(), event.getAction());
    }

    /**
     * Add HTTP request information to audit event.
     */
    private void addRequestInfo(AuditEvent event) {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Get client IP
                event.setUserIp(getClientIpAddress(request));

                // Get user agent
                event.setUserAgent(request.getHeader("User-Agent"));

                // Add request metadata
                event.getMetadata().put("requestMethod", request.getMethod());
                event.getMetadata().put("requestUri", request.getRequestURI());
                event.getMetadata().put("sessionId", request.getSession(false) != null ?
                        request.getSession().getId() : null);
            }
        } catch (Exception e) {
            logger.debug("Could not add request info to audit event", e);
        }
    }

    /**
     * Get client IP address from request.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs
                if (ip.contains(",")) {
                    return ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Convert object to JSON string.
     */
    private String objectToJson(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.debug("Failed to convert object to JSON", e);
            return object.toString();
        }
    }
}