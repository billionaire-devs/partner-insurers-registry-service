package com.bamboo.assur.partnerinsurersservice.registry.domain.events

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Serializable
data class PartnerInsurerCreatedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    val partnerInsurerCode: String,
    val legalName: String,
    val status: PartnerInsurerStatus,
    @Contextual
    val createdAt: Instant
): DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = getAggregateTypeOrEmpty<PartnerInsurer>(),
    eventType = getEventTypeNameOrDefault<PartnerInsurerCreatedEvent>()
)