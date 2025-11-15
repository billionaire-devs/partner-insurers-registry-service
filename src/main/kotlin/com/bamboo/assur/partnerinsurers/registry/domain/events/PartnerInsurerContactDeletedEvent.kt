package com.bamboo.assur.partnerinsurers.registry.domain.events

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class PartnerInsurerContactDeletedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    @Contextual
    val contactId: DomainEntityId,
    @Contextual
    val deletedAt: Instant,
) : DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = getAggregateTypeOrEmpty<PartnerInsurer>(),
    eventType = DomainEvent.getEventTypeNameOrDefault<PartnerInsurerContactDeletedEvent>()
)