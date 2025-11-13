@file:OptIn(ExperimentalTime::class)

package com.bamboo.assur.partnerinsurers.registry.application.queries.models

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ContactResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ContactResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import com.fasterxml.jackson.annotation.JsonFormat
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
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

        @field:JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.XX",
            timezone = "Africa/Libreville"
        )
        val createdAt: OffsetDateTime,
    ) : PartnerInsurerProjection

    data class FullProjection(
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

        @field:JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssXX",
            timezone = "Africa/Libreville"
        )
        val createdAt: OffsetDateTime,

        @field:JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.ssZ",
            timezone = "Africa/Libreville"
        )
        val updatedAt: OffsetDateTime,
    ) : PartnerInsurerProjection {
        companion object {
            fun PartnerInsurer.toProjection(
                agreementsSummary: Set<Nothing> = emptySet(),
            ): FullProjection {
                return FullProjection(
                    id = this.id.value,
                    partnerInsurerCode = this.partnerInsurerCode,
                    legalName = this.legalName,
                    taxIdentificationNumber = this.taxIdentificationNumber,
                    status = this.status.name,
                    logoUrl = this.logoUrl,
                    address = json.encodeToJsonElement(  this.address),
                    contacts = this.contacts.map { it.toResponseDto() }.toSet(),
//                    agreementsSummary = , TODO: Implement when adding features about agreements
                    createdAt = this.createdAt.toJavaInstant().atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                    updatedAt = this.updatedAt.toJavaInstant().atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            }
        }
    }
}
