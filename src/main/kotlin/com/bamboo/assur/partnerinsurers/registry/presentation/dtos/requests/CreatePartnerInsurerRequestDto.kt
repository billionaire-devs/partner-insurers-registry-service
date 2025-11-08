package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurers.registry.application.commands.CreatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.ContactDto
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.URL

data class CreatePartnerInsurerRequestDto(
    @field:NotBlank(message = "Partner code cannot be blank")
    val partnerInsurerCode: String,

    @field:NotBlank(message = "Legal name cannot be blank")
    val legalName: String,

    @field:NotBlank(message = "Tax identification number cannot be blank")
    val taxIdentificationNumber: String,

    @field:Valid
    @field:NotEmpty("Contacts cannot be empty. At least one is required")
    val contacts: Set<ContactDto>,

    @field:NotNull(message = "Address cannot be null")
    @field:Valid
    val address: AddressDto,

    @field:NotBlank(message = "Status cannot be blank")
    @field:NotNull
    val status: String,

    @field:URL
    val logoUrl: String? = null,
) {
    fun toCommand(): CreatePartnerInsurerCommand {
        val validStatus = try {
            PartnerInsurerStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid status: $status. Valid statuses are ${PartnerInsurerStatus.entries.joinToString()}"
            )
        }


        return CreatePartnerInsurerCommand(
            partnerInsurerCode = partnerInsurerCode,
            legalName = legalName,
            taxIdentificationNumber = taxIdentificationNumber,
            logoUrl = logoUrl,
            contacts = contacts.mapTo(mutableSetOf(), ContactDto::toDomain),
            address = address.toDomain(),
            status = validStatus
        )
    }
}
