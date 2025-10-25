package com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.AddressDto.Companion.toResponseDTO
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.ContactDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.ContactDto.Companion.toResponseDTO
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

/**
 * Data Transfer Object for detailed Partner Insurer information.
 *
 * This DTO is used to transfer complete partner insurer details
 * including contacts and full address information.
 */
@OptIn(ExperimentalUuidApi::class)
data class PartnerInsurerDetailResponseDto(
    val id: UUID,
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val status: String,
    val logoUrl: String?,
    val address: AddressDto,
    val contacts: List<ContactDto>
) {
    companion object {
        /**
         * Factory method to create a [PartnerInsurerDetailResponseDto] from a domain [PartnerInsurer] entity.
         */
        fun PartnerInsurer.toResponseDTO(): PartnerInsurerDetailResponseDto {
            return PartnerInsurerDetailResponseDto(
                id = id.value,
                partnerInsurerCode = partnerInsurerCode,
                legalName = legalName,
                taxIdentificationNumber = taxIdentificationNumber.value,
                status = status.name,
                logoUrl = logoUrl?.value,
                address = address.toResponseDTO(),
                contacts = contacts.map { it.toResponseDTO() }
            )
        }
    }
}
