package com.bamboo.assur.partnerinsurersservice.core.domain

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
class DomainEvent(
    val eventId: Uuid = Uuid.Companion.random(),
    val aggregateId: Any,
    val aggregateType: String,
    val eventType: String,
    val occurredOn: Instant = Clock.System.now(),
)