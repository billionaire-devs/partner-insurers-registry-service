package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import kotlin.time.ExperimentalTime

data class DeletePartnerInsurerResponseDto(
    val id: String,
    val partnerInsurerCode: String,
    val deletedAt: String,
) {
    @OptIn(ExperimentalTime::class)
    companion object {
        fun PartnerInsurer.toResponseDto() = DeletePartnerInsurerResponseDto(
            id = this.id.value.toString(),
            partnerInsurerCode = this.partnerInsurerCode,
            deletedAt = this.deletedAt.toString()
        )
    }
}