package com.bamboo.assur.partnerinsurersservice.registry.domain.events

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class PartnerInsurerStatusChangedEvent(
    @Contextual
    val aggregateIdValue: DomainEntityId,
    val oldStatus: String,
    val newStatus: String,
    val reason: String?
) : DomainEvent(
    aggregateId = aggregateIdValue,
    aggregateType = PartnerInsurer::class.simpleName.orEmpty(),
    eventType = createEventTypeName(this::class)
)