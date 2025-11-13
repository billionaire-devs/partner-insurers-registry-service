package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import kotlin.time.ExperimentalTime

data class CreatePartnerInsurerResponseDto(
    val id: String,
    val partnerInsurerCode: String,
    val legalName: String,
    val status: String,
    val createdAt: String,
) {
    companion object {

        /**
         * Factory method to create a [CreatePartnerInsurerResponseDto]
         * from a domain [PartnerInsurer] entity
         *
         * @return A [CreatePartnerInsurerResponseDto] containing the partner
         *         insurer details.
         */
        @OptIn(ExperimentalTime::class)
        fun PartnerInsurer.toResponseDto(): CreatePartnerInsurerResponseDto {
            return CreatePartnerInsurerResponseDto(
                id = id.value.toString(),
                partnerInsurerCode = this.partnerInsurerCode,
                legalName = this.legalName,
                status = this.status.name,
                createdAt = this.createdAt.toString()
            )
        }
    }
}
