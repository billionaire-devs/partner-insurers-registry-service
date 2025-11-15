package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

data class UpdatePartnerInsurerContactResponseDto(
    val contactId: UUID,
    val partnerInsurerId: UUID,
    val fullName: String,
    val email: String,
    val phone: String,
    val contactRole: String,

    @field:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ssXX",
        timezone = "Africa/Libreville"
    )
    val createdAt: OffsetDateTime,

    @field:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ssXX",
        timezone = "Africa/Libreville"
    )
    val updatedAt: OffsetDateTime
)
