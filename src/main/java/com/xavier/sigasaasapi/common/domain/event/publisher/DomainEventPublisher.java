package com.xavier.sigasaasapi.common.domain.event.publisher;
/**
 * Interface for publishing domain events.
 * @param <T> the type of domain event
 * @version 1.0
 * @since 2025-09-11
 * @author Xavier Nhagumbe
 */

import com.xavier.sigasaasapi.common.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T event);
}
