package com.bamboo.assur.partnerinsurersservice.core.domain

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent

/**
 * Interface for handling domain events.
 *
 * Event handlers process domain events and typically perform side effects
 * like updating read models, sending notifications, or triggering workflows.
 *
 * @param T The type of domain event this handler processes
 */
interface DomainEventHandler<in T : DomainEvent> {

    /**
     * Handles the given domain event.
     *
     * @param event The domain event to handle
     */
    suspend fun handle(event: T)

    /**
     * Returns the type of event this handler can process.
     * Used for event routing and handler registration.
     */
    fun eventType(): String
}