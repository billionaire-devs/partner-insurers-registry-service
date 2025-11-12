@file:OptIn(ExperimentalTime::class)

package com.bamboo.assur.partnerinsurers.registry.application.queries.models

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ContactResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ContactResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import kotlinx.serialization.Contextual
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

private val json = Json { ignoreUnknownKeys = true }

sealed interface PartnerInsurerProjection {
    data class SummaryProjection(
        @Contextual val id: UUID,
        val partnerInsurerCode: String,
        val taxIdentificationNumber: TaxIdentificationNumber,
        val legalName: String,
        val status: PartnerInsurerStatus,
        val createdAt: Instant,
    ) : PartnerInsurerProjection

    data class DetailedProjection(
        @Contextual
        val id: UUID,
        val partnerInsurerCode: String,
        val legalName: String,
        val taxIdentificationNumber: TaxIdentificationNumber,
        val status: String,
        val logoUrl: Url?,
        @Contextual
        val address: JsonElement,
        val contacts: Set<ContactResponseDto> = emptySet(), // TODO: Implement when adding features about contacts
        val agreementsSummary: Set<Nothing> = emptySet(), // TODO: Implement when adding features about agreements
        val createdAt: Instant,
        val updatedAt: Instant,
    ) : PartnerInsurerProjection {
        companion object {
            fun PartnerInsurer.toProjection(
                agreementsSummary: Set<Nothing> = emptySet(),
            ): DetailedProjection {
                return DetailedProjection(
                    id = this.id.value,
                    partnerInsurerCode = this.partnerInsurerCode,
                    legalName = this.legalName,
                    taxIdentificationNumber = this.taxIdentificationNumber,
                    status = this.status.name,
                    logoUrl = this.logoUrl,
                    address = json.encodeToJsonElement(  this.address),
                    contacts = this.contacts.map { it.toResponseDto() }.toSet(),
//                    agreementsSummary = , TODO: Implement when adding features about agreements
                    createdAt = this.createdAt.toJavaInstant(),
                    updatedAt = this.updatedAt.toJavaInstant(),
                )
            }
        }
    }
}
