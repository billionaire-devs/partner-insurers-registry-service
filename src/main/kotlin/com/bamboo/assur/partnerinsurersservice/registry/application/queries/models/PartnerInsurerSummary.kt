package com.bamboo.assur.partnerinsurersservice.registry.application.queries.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class PartnerInsurerSummary(
    @Contextual
    val id: UUID,
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val status: String,
    val logoUrl: String?,
    @Contextual
    val address: JsonElement
)