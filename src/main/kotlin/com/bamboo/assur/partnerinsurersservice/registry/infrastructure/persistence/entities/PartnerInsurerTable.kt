@file:OptIn(ExperimentalUuidApi::class)

package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities

import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.BrokerPartnerInsurerAgreement
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurersservice.registry.domain.valueObjects.TaxIdentificationNumber
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.InsertOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid


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
    val address: JsonElement,
    val status: String,
    @InsertOnlyProperty
    val createdAt: Instant,
    val updatedAt: Instant,
    @Column("deleted_at")
    val deletedAt: Instant? = null,
) {
    fun toDomain(contacts: Set<Contact>, agreements: Set<BrokerPartnerInsurerAgreement>) = PartnerInsurer.reconstitute(
        id = DomainEntityId(id.toKotlinUuid()),
        partnerInsurerCode = partnerInsurerCode,
        legalName = legalName,
        taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
        logoUrl = logoUrl?.let { Url(it) },
        contacts = contacts,
        address = json.decodeFromJsonElement<Address>(address),
        status = PartnerInsurerStatus.valueOf(status),
        brokerPartnerInsurerAgreements = agreements,
    )
    companion object {
        /**
         * Creates a new [PartnerInsurerTable] instance from a [PartnerInsurer] domain object.
         * Serves for creating new records in the database.
         *
         * @param domain The [PartnerInsurer] domain object to be converted.
         * @return A new [PartnerInsurerTable] instance with the same values as the given [PartnerInsurer].
         */
        fun PartnerInsurer.fromDomain() = PartnerInsurerTable(
            id = id.value.toJavaUuid(),
            partnerInsurerCode = partnerInsurerCode,
            legalName = legalName,
            taxIdentificationNumber = taxIdentificationNumber.value,
            logoUrl = logoUrl?.value,
            address = json.encodeToJsonElement(address),
            status = status.name,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}