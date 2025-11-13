package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime
import java.util.*

/**
 * Response DTO for status change operation on Partner Insurer.
 * Based on PIS-REG-105 specification.
 */
data class ChangePartnerInsurerStatusResponseDto(
    val id: UUID,
    val partnerInsurerCode: String,
    val oldStatus: String,
    val newStatus: String,
    val reason: String?,

    @field:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ssXX",
        timezone = "Africa/Libreville"
    )
    val updatedAt: OffsetDateTime,
)
