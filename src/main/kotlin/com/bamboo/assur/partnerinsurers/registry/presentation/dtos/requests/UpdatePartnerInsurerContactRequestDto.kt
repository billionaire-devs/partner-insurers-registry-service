package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import com.bamboo.assur.partnerinsurers.registry.application.commands.UpdatePartnerInsurerContactCommand
import java.util.UUID

data class UpdatePartnerInsurerContactRequestDto(
    @field:Size(min = 2, max = 255)
    val fullName: String?,

    @field:Email(regexp = ".+@.+\\..+")
    val email: String?,

    @field:Size(min = 9, max = 50)
    val phone: String?,

    @field:Size(min = 2, max = 100)
    val contactRole: String?
) {
    fun toCommand(partnerInsurerId: UUID, contactId: UUID): UpdatePartnerInsurerContactCommand {
        return UpdatePartnerInsurerContactCommand(
            partnerInsurerId = partnerInsurerId,
            contactId = contactId,
            fullName = fullName,
            email = email,
            phone = phone,
            contactRole = contactRole
        )
    }
}
