package com.bamboo.assur.partnerinsurersservice.registry.domain.events

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class PartnerInsurerUpdatedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    val legalName: String?,
    @Contextual
    val logoUrl: Url?,
    @Contextual
    val address: Address?,
) : DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = getAggregateTypeOrEmpty<PartnerInsurer>(),
    eventType = getEventTypeNameOrDefault< PartnerInsurerCreatedEvent>()
)