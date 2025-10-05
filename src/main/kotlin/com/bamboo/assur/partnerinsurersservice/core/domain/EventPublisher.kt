package com.bamboo.assur.partnerinsurersservice.core.domain

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent

/**
 * Interface for publishing domain events.
 *
 * Event publishers are responsible for dispatching domain events to interested
 * parties, such as event handlers, message queues, or external systems.
 * This interface abstracts the underlying event publishing mechanism.
 */
interface EventPublisher {

    /**
     * Publishes a single domain event.
     *
     * @param event The domain event to publish
     */
    suspend fun publish(event: DomainEvent)

    /**
     * Publishes multiple domain events.
     *
     * @param events The list of domain events to publish
     */
    suspend fun publishAll(events: List<DomainEvent>)
}