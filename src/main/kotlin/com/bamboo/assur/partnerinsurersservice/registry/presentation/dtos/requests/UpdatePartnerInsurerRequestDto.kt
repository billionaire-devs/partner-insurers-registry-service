package com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.ContactDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.URL
import java.util.UUID

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
