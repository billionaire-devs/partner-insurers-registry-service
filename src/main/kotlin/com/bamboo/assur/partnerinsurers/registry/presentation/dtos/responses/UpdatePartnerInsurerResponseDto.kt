package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto.Companion.toResponseDTO
import kotlin.time.ExperimentalTime

data class UpdatePartnerInsurerResponseDto(
    val id: String,
    val partnerInsurerCode: String,
    val legalName: String,
    val logoUrl: String?,
    val status: String,
    val address: AddressDto,
    val createdAt: String,
    val updatedAt: String,
) {
    @OptIn(ExperimentalTime::class)
    companion object {
        fun PartnerInsurer.toResponseDto() = UpdatePartnerInsurerResponseDto(
            id = this.id.value.toString(),
            partnerInsurerCode = this.partnerInsurerCode,
            legalName = this.legalName,
            logoUrl = this.logoUrl?.value,
            status = this.status.name,
            address = this.address.toResponseDTO(),
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString()
        )
    }
}