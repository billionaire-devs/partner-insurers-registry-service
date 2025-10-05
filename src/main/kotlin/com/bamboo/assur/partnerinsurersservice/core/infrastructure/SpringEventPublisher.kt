package com.bamboo.assur.partnerinsurersservice.core.infrastructure

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent
import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEventHandler
import com.bamboo.assur.partnerinsurersservice.core.domain.EventPublisher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class SpringEventPublisher(
    private val eventHandlers: List<DomainEventHandler<DomainEvent>>
) : EventPublisher {

    override suspend fun publish(event: DomainEvent) {
        val handlers = eventHandlers.filter { it.eventType() == event.eventType }

        coroutineScope {
            handlers.map { handler ->
                async {
                    try {
                        handler.handle(event)
                    } catch (e: Exception) {
                        // Log error but don't fail the entire operation
                        println("Error handling event ${event.eventType}: ${e.message}")
                    }
                }
            }.awaitAll()
        }
    }

    override suspend fun publishAll(events: List<DomainEvent>) {
        coroutineScope {
            events.map { event ->
                async { publish(event) }
            }.awaitAll()
        }
    }
}