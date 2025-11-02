package com.bamboo.assur.partnerinsurersservice.core.infrastructure.events

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.infrastructure.EventPublishingException
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.OutboxMessagesTable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.ExperimentalUuidApi
import java.util.UUID

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
    private val json: Json,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
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
        val eventType = event.eventType
        val aggregateId = event.aggregateId.value
        var outboxId: UUID? = null

        try {
            val outboxMessagesTable = OutboxMessagesTable.create(
                aggregateId = aggregateId,
                aggregateType = event.aggregateType,
                eventType = eventType,
                payload = event,
                json = json
            )

            outboxId = outboxMessagesTable.id

            // Log creation details (do not log full payload in prod if sensitive)
            val payloadPreview = try {
                val s = outboxMessagesTable.payload.toString()
                if (s.length > 512) s.substring(0, 512) + "..." else s
            } catch (_: Exception) {
                "<unserializable-payload>"
            }

            logger.debug(
                "Creating outbox message: id={}, aggregateId={}, aggregateType={}, eventType={}, payloadPreview={}",
                outboxId, aggregateId, outboxMessagesTable.aggregateType, eventType, payloadPreview
            )

            // Use R2dbcEntityTemplate.insert to force an INSERT of a new outbox row
            r2dbcEntityTemplate.insert(outboxMessagesTable).awaitSingle()

            logger.debug("Saved event to outbox: {} - {} (outboxId={})", eventType, aggregateId, outboxId)
        } catch (e: Exception) {
            logger.error(
                "Failed to save event to outbox: {} (aggregateId={}, outboxId={})",
                eventType, aggregateId, outboxId,
                e
            )
            throw EventPublishingException("Failed to publish event: $eventType", e)
        }
    }
}
