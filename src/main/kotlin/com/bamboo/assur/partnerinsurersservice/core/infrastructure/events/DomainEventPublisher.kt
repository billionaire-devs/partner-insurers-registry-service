package com.bamboo.assur.partnerinsurersservice.core.infrastructure.events

import com.bamboo.assur.partnerinsurersservice.core.application.ports.output.OutboxRepository
import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.OutboxMessagesTable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.ExperimentalUuidApi

/**
 * Publishes domain events using the Transactional Outbox pattern.
 *
 * Events are serialized with Kotlinx.serialization and stored in the `outbox` table
 * within the same transaction as the domain changes. A separate scheduled processor
 * publishes them to RabbitMQ.
 */
@OptIn(ExperimentalUuidApi::class)
@Component
class DomainEventPublisher(
    private val outboxRepository: OutboxRepository,
    private val json: Json
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Publishes multiple events to the outbox.
     */
    @Transactional
    suspend fun publish(events: List<DomainEvent>) {
        events.forEach { event -> publish(event) }
    }

    /**
     * Publishes a single event to the outbox.
     *
     * @throws EventPublishingException when serialization or persistence fails.
     */
    @Transactional
    suspend fun publish(event: DomainEvent) {
        try {
            val outboxMessagesTable = OutboxMessagesTable.create(
                aggregateId = event.aggregateId.value,
                aggregateType = event.aggregateType,
                eventType = event.eventType,
                payload = event,
                json = json
            )

            outboxRepository.save(outboxMessagesTable)
            logger.debug("Saved event to outbox: {} - {}", event.eventType, event.aggregateId)
        } catch (e: Exception) {
            logger.error("Failed to save event to outbox: {}", event.eventType, e)
            throw EventPublishingException("Failed to publish event: ${event.eventType}", e)
        }
    }
}

/**
 * Exception thrown when event publishing fails.
 */
class EventPublishingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
