package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
data class AddPartnerInsurerContactResponseDto(
    @Contextual
    val contactId: UUID,
    @Contextual
    val partnerInsurerId: UUID,
    val fullName: String,
    val email: String,
    val phone: String,
    val contactRole: String,
    @Contextual
    val createdAt: Instant,
)