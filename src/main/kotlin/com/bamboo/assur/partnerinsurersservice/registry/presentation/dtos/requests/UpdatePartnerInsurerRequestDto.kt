package com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurersservice.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.AddressDto
import jakarta.validation.Valid
import org.hibernate.validator.constraints.URL
import java.util.*

data class UpdatePartnerInsurerRequestDto(
    val legalName: String? = null,

    @field:URL
    val logoUrl: String? = null,

    @field:Valid
    val address: AddressDto? = null,
) {
    fun toCommand(id: UUID): UpdatePartnerInsurerCommand {
        return UpdatePartnerInsurerCommand(
            id = id,
            legalName = legalName,
            logoUrl = logoUrl,
            address = address?.toDomain(),
        )
    }
}
