package com.bamboo.assur.partnerinsurers.registry.core.domain

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class DomainEventTest {

    private class SampleCreatedEvent : DomainEvent(
        aggregateId = DomainEntityId(UUID.randomUUID()),
        aggregateType = "SampleAggregate",
        eventType = "SampleCreatedEvent"
    )

    private class Something : DomainEvent(
        aggregateId = DomainEntityId(UUID.randomUUID()),
        aggregateType = "SomethingAggregate",
        eventType = "Something"
    )

    private class DoubleEventEvent : DomainEvent(
        aggregateId = DomainEntityId(UUID.randomUUID()),
        aggregateType = "DoubleAggregate",
        eventType = "DoubleEventEvent"
    )

    @Test
    fun removesEventSuffixFromConcreteEventClass() {
        val result = DomainEvent.getEventTypeNameOrDefault<SampleCreatedEvent>()
        assertEquals("SampleCreated", result)
    }

    @Test
    fun returnsClassNameWhenNoEventSuffixPresent() {
        val result = DomainEvent.getEventTypeNameOrDefault<Something>()
        assertEquals("Something", result)
    }

    @Test
    fun removesOnlyOneTrailingEventSuffixWhenPresentMultipleTimes() {
        val result = DomainEvent.getEventTypeNameOrDefault<DoubleEventEvent>()
        assertEquals("DoubleEvent", result)
    }
}
