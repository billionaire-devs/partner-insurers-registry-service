package com.bamboo.assur.partnerinsurersservice.registry.application.queries

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class PartnerInsurerSummary(
    val id: Uuid,
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val status: String,
    val logoUrl: String?,
    val address: String
)