package com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

/**
 * Represents a message persisted in the transactional outbox.
 *
 * The payload is stored as a JSON string. Use [create] to build instances from
 * arbitrary serializable payloads using Kotlinx.serialization.
 *
 * @property id Unique identifier of the outbox message.
 * @property aggregateId ID of the aggregate that emitted the event.
 * @property aggregateType Type/name of the aggregate.
 * @property eventType Domain event type name.
 * @property payload JSON payload as a string (serialized event).
 * @property createdAt Timestamp when the message was created.
 * @property processed Whether the message has been successfully processed.
 * @property processedAt Timestamp when processing completed.
 * @property error Optional error message in case processing failed.
 */
@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Table("outbox")
data class OutboxMessagesTable(
    @Id
    val id: UUID,

    @Column("aggregate_id")
    val aggregateId: UUID,

    @Column("aggregate_type")
    val aggregateType: String,

    @Column("event_type")
    val eventType: String,

    @Column("payload")
    val payload: String,

    @Column("created_at")
    val createdAt: Instant = Clock.System.now(),

    @Column("processed")
    var processed: Boolean = false,

    @Column("processed_at")
    var processedAt: Instant? = null,

    @Column("error")
    var error: String? = null
) {
    companion object {
        /**
         * Factory to create an [OutboxMessagesTable] with a serialized payload using Kotlinx.serialization.
         *
         * @param T The payload/event type which must be serializable.
         * @param aggregateId Aggregate identifier related to the event.
         * @param aggregateType Aggregate type name.
         * @param eventType Event type name.
         * @param payload Event instance to serialize and store.
         * @param json Configured Kotlinx [Json] instance (defaults to permissive one).
         */
        inline fun <reified T> create(
            aggregateId: Uuid,
            aggregateType: String,
            eventType: String,
            payload: T,
            json: Json = Json
        ): OutboxMessagesTable {
            return OutboxMessagesTable(
                id = Uuid.random().toJavaUuid(),
                aggregateId = aggregateId.toJavaUuid(),
                aggregateType = aggregateType,
                eventType = eventType,
                payload = json.encodeToString(payload)
            )
        }
    }
}
