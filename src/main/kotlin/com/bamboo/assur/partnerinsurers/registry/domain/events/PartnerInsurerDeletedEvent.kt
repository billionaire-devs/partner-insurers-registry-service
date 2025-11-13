package com.bamboo.assur.partnerinsurers.registry.domain.events

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class PartnerInsurerDeletedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    val partnerInsurerCode: String,
    val status: String,
    val reason: String?,
) : DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = getAggregateTypeOrEmpty<PartnerInsurer>(),
    eventType = getEventTypeNameOrDefault<PartnerInsurerDeletedEvent>()
)