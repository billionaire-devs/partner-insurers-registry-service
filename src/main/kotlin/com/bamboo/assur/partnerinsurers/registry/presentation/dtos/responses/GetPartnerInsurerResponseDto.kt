@file:OptIn(ExperimentalTime::class)

package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto.Companion.toResponseDTO
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.ContactDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.ContactDto.Companion.toResponseDTO
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi

/**
 * Data Transfer Object for detailed Partner Insurer information.
 *
 * This DTO is used to transfer complete partner insurer details
 * including contacts and full address information.
 */
@OptIn(ExperimentalUuidApi::class)
data class GetPartnerInsurerResponseDto(
    val id: UUID,
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val status: String,
    val logoUrl: String?,
    val address: AddressDto,
    val contacts: List<ContactDto>,

    @field:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ssXX",
        timezone = "Africa/Libreville"
    )
    val createdAt: OffsetDateTime,
) {
    companion object {
        /**
         * Factory method to create a [GetPartnerInsurerResponseDto] from a domain [PartnerInsurer] entity.
         */
        fun PartnerInsurer.toResponseDto(): GetPartnerInsurerResponseDto {
            return GetPartnerInsurerResponseDto(
                id = id.value,
                partnerInsurerCode = partnerInsurerCode,
                legalName = legalName,
                taxIdentificationNumber = taxIdentificationNumber.value,
                status = status.name,
                logoUrl = logoUrl?.value,
                address = address.toResponseDTO(),
                contacts = contacts.map { it.toResponseDTO() },
                createdAt = createdAt.toJavaInstant().atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime()
            )
        }
    }
}
