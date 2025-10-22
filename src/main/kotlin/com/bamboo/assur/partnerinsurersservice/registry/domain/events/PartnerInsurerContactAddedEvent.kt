package com.bamboo.assur.partnerinsurersservice.registry.domain.events

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
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
    aggregateType = PartnerInsurer::class.simpleName.orEmpty(),
    eventType = PartnerInsurerContactAddedEvent::class.simpleName.orEmpty()
) 