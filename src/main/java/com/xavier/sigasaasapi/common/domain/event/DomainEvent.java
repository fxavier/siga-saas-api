package com.xavier.sigasaasapi.common.domain.event;
/**
 * Interface representing a domain event.
 * Domain events are used to capture significant occurrences within the domain.
 * They typically include a timestamp and a topic for categorization.
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */

import java.time.Instant;

public interface DomainEvent {
    Instant ooccurredOn();
    String topic();
}
