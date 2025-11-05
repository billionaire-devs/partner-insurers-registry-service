package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import java.util.UUID

/**
 * Response DTO for status change operation on Partner Insurer.
 */
data class ChangePartnerInsurerStatusResponseDto(
    val id: UUID,
    val oldStatus: String,
    val newStatus: String,
    val message: String
)
