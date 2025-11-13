package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurers.registry.application.commands.DeletePartnerInsurerCommand
import java.util.*

data class DeletePartnerInsurerRequestDto(
    val reason: String?,
) {
    fun toCommand(id: UUID) = DeletePartnerInsurerCommand(
        id = id,
        reason = reason
    )
}