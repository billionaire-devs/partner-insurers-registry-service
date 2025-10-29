package com.bamboo.assur.partnerinsurersservice.registry.domain.events

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainEvent
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurersservice.registry.domain.valueObjects.TaxIdentificationNumber
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
    aggregateType = PartnerInsurer::class.simpleName.orEmpty(),
    eventType = createEventTypeName(this::class)
)