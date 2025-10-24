@file:OptIn(ExperimentalUuidApi::class)

package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities

import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Email
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Phone
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.Contact
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Table("partner_insurer_contacts")
data class PartnerInsurerContactTable(
    @Id
    val id: UUID,
    val partnerInsurerId: UUID,
    val fullName: String,
    val email: String,
    val phone: String,
    val contactRole: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?,
) {
    fun toDomain() = Contact(
        id = DomainEntityId(id),
        fullName = fullName,
        email = Email(email),
        phone = Phone(phone),
        contactRole = contactRole,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun Contact.fromDomain(partnerInsurerId: Uuid) = PartnerInsurerContactTable(
            id = Uuid.random().toJavaUuid(),
            partnerInsurerId = partnerInsurerId.toJavaUuid(),
            fullName = fullName,
            email = email.value,
            phone = phone.value,
            contactRole = contactRole,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = null,
        )
    }
}
