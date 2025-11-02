package com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AddressDto(
    val street: String,

    @field:NotBlank
    @field:NotNull
    val city: String,

    val zipCode: String?,

    @field:NotBlank(message = "Country cannot be blank")
    @field:NotNull
    val country: String
) {
    fun toDomain() = Address(
        street = street,
        city = city,
        country = country,
        zipCode = zipCode
    )

    companion object {
        fun Address.toResponseDTO() = AddressDto(
            street = street,
            city = city,
            country = country,
            zipCode = zipCode
        )
    }
}