package com.bamboo.assur.partnerinsurers.registry.registry.presentation.dtos

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import com.bamboo.assur.partnerinsurers.registry.registry.domain.entities.Contact
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

typealias DomainEmail = com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Email

data class ContactDto(
    @field:NotBlank(message = "Full name cannot be blank")
    @field:NotNull
    val fullName: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email
    @field:NotNull
    val email: String,

    @field:NotBlank(message = "Phone cannot be blank")
    @field:Pattern(
        regexp = Phone.PHONE_REGEX,
        message = "Phone number must be between 10 and 15 digits and may start with +"
    )
    @field:NotNull
    val phone: String,

    @field:NotBlank(message = "Role cannot be blank")
    @field:NotNull
    val role: String
) {
    fun toDomain() = Contact.create(
        fullName = fullName,
        email = DomainEmail(email),
        phone = Phone(phone),
        contactRole = role
    )

    companion object {
        fun Contact.toResponseDTO() = ContactDto(
            fullName = fullName,
            email = email.value,
            phone = phone.value,
            role = contactRole
        )
    }
}