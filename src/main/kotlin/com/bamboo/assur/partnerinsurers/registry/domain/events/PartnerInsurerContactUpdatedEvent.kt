package com.bamboo.assur.partnerinsurers.registry.domain.events

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class PartnerInsurerContactUpdatedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    @Contextual
    val contactId: DomainEntityId,
    val updatedFields: Map<String, String?>,
) : DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = getAggregateTypeOrEmpty<PartnerInsurer>(),
    eventType = "partner.contact.updated"
)
