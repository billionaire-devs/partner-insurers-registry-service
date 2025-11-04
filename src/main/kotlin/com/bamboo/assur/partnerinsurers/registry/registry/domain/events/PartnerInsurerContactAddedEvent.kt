package com.bamboo.assur.partnerinsurers.registry.registry.domain.events

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.registry.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.registry.domain.entities.PartnerInsurer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class PartnerInsurerContactAddedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    val partnerCode: String,
    @Contextual
    val contact: Contact,
): DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = getAggregateTypeOrEmpty<PartnerInsurer>(),
    eventType = getEventTypeNameOrDefault<PartnerInsurerContactAddedEvent>()
)