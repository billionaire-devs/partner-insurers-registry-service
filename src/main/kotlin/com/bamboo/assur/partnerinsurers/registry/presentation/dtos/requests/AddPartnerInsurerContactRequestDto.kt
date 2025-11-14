package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurers.registry.application.commands.AddPartnerInsurerContactCommand
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.util.*

data class AddPartnerInsurerContactRequestDto(
    @field:NotBlank(message = "Full name cannot be blank")
    @field:NotNull
    @field:NotEmpty(message = "Full name cannot be empty")
    val fullName: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email must be valid")
    @field:NotNull
    val email: String,

    @field:NotBlank(message = "Phone cannot be blank")
    @field:NotEmpty(message = "Phone must be between 10 and 15 digits and may start with +")
    @field:Pattern(
        regexp = Phone.PHONE_REGEX,
        message = "Phone number must be between 10 and 15 digits and may start with +"
    )
    @field:NotNull
    val phone: String,

    @field:NotBlank(message = "Contact role cannot be blank")
    @field:NotEmpty
    @field:NotNull
    val contactRole: String,
) {
    fun toCommand(partnerInsurerId: UUID) = AddPartnerInsurerContactCommand(
        partnerInsurerId = DomainEntityId(partnerInsurerId),
        fullName = fullName,
        email = email,
        phone = phone,
        contactRole = contactRole
    )
}