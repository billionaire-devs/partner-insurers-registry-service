package com.bamboo.assur.partnerinsurersservice.registry.application.queries

import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class PartnerInsurerSummary(
    val id: UUID,
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val status: String,
    val logoUrl: String?,
    val address: String
)