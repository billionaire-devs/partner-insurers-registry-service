package com.bamboo.assur.partnerinsurersservice.core.domain

import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

/**
 * Represents a domain event in a domain-driven design context.
 *
 * A domain event encapsulates a significant occurrence or change in the state
 * of a domain model. It typically includes metadata to describe the event
 * and support tracking, such as a unique identifier and timestamp.
 *
 * @param eventId A unique identifier for the domain event, defaulting to a randomly generated UUID.
 * @param aggregateId The identifier of the aggregate root instance associated with the event.
 * @param aggregateType The type of aggregate root that the event is linked to.
 * @param eventType A descriptive type or name for the event; used to categorize and identify the event.
 * @param occurredOn The timestamp indicating when the event occurred, defaulting to the current system time.
 */
@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Serializable
abstract class DomainEvent(
    @Contextual
    val eventId: DomainEntityId = DomainEntityId(UUID.randomUUID()),
    @Contextual
    val aggregateId: DomainEntityId,
    val aggregateType: String,
    val eventType: String,
    @Contextual
    val occurredOn: Instant = Clock.System.now(),
) {
    companion object {
        /**
         * Returns a descriptive event type name for the given domain event class.
         *
         * This method takes a domain event class as input and returns a string
         * that can be used to identify or categorize the event. The returned
         * string is the simple name of the class, minus the "Event" suffix,
         * if present.
         *
         * @param domainEventClass The class of the domain event.
         * @return A descriptive event type name.
         */
        fun <T: DomainEvent> createEventTypeName(domainEventClass: KClass<in T>): String {
            val simpleName = domainEventClass.simpleName.orEmpty()
            val eventNameSuffix = "Event"
            return if (simpleName.endsWith(eventNameSuffix)) {
                simpleName.removeSuffix(eventNameSuffix)
            } else {
                simpleName
            }
        }
    }
}