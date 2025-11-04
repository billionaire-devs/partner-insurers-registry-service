package com.bamboo.assur.partnerinsurers.registry.registry.infrastructure.persistence.entities

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Email
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import com.bamboo.assur.partnerinsurers.registry.registry.domain.entities.Contact
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@OptIn(ExperimentalTime::class)
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
        createdAt = createdAt.toKotlinInstant(),
        updatedAt = updatedAt.toKotlinInstant()
    )

    companion object {
        fun Contact.toEntityTable(partnerInsurerId: UUID) = PartnerInsurerContactTable(
            id = UUID.randomUUID(),
            partnerInsurerId = partnerInsurerId,
            fullName = fullName,
            email = email.value,
            phone = phone.value,
            contactRole = contactRole,
            createdAt = createdAt.toJavaInstant(),
            updatedAt = updatedAt.toJavaInstant(),
            deletedAt = null,
        )
    }
}
