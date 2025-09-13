package com.xavier.sigasaasapi.common.infrastructure.audit;
import com.xavier.sigasaasapi.common.domain.entity.AuditableEntity;
import com.xavier.sigasaasapi.common.security.SecurityContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * JPA Entity Listener for automatic audit fields population.
 * Automatically sets created/updated timestamps and user information.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Component
public class AuditEntityListener {

    private final SecurityContext securityContext;

    public AuditEntityListener(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @PrePersist
    public void onPrePersist(AuditableEntity<?> entity) {
        LocalDateTime now = LocalDateTime.now();
        String username = getCurrentUsername();

        entity.setCreatedAt(now);
        entity.setCreatedBy(username);
        entity.setUpdatedAt(now);
        entity.setUpdatedBy(username);

        if (entity.getVersion() == null) {
            entity.setVersion(0L);
        }
    }

    @PreUpdate
    public void onPreUpdate(AuditableEntity<?> entity) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(getCurrentUsername());
    }

    private String getCurrentUsername() {
        return securityContext.getCurrentUsername()
                .orElse("system");
    }
}