@file:OptIn(ExperimentalUuidApi::class)

package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurers.registry.domain.entities.BrokerPartnerInsurerAgreement
import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi

private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Table("partner_insurers")
data class PartnerInsurerTable(
    @Id
    @Column("id")
    val id: UUID,
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val logoUrl: String?,
    // store address as JSON element so R2DBC custom converters bind it as jsonb
    val address: JsonElement,
    val status: String,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant,
    val deletedAt: java.time.Instant? = null,
    val deletedBy: UUID? = null,
) {
    fun toDomain(contacts: Set<Contact>, agreements: Set<BrokerPartnerInsurerAgreement>) = PartnerInsurer.reconstitute(
        id = DomainEntityId(id),
        partnerInsurerCode = partnerInsurerCode,
        legalName = legalName,
        taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
        logoUrl = logoUrl?.let { Url(it) },
        contacts = contacts,
        // decode Address from JsonElement
        address = json.decodeFromJsonElement(address),
        status = PartnerInsurerStatus.valueOf(status),
        brokerPartnerInsurerAgreements = agreements
    )

    companion object {
        /**
         * Create a [PartnerInsurerTable] from a [PartnerInsurer] domain object (extension function).
         * Returns a new [PartnerInsurerTable] with values copied from the given domain object.
         */
        fun PartnerInsurer.toEntityTable() = PartnerInsurerTable(
            id = id.value,
            partnerInsurerCode = partnerInsurerCode,
            legalName = legalName,
            taxIdentificationNumber = taxIdentificationNumber.value,
            logoUrl = logoUrl?.value,
            // store Address as JsonElement (let R2DBC converters handle jsonb binding)
            address = json.encodeToJsonElement(address),
            status = status.name,
            createdAt = createdAt.toJavaInstant(),
            updatedAt = updatedAt.toJavaInstant(),
            deletedAt = deletedAt?.toJavaInstant(),
            // TODO: Add support for deletedBy when user context is available
            deletedBy = null
        )
    }
}