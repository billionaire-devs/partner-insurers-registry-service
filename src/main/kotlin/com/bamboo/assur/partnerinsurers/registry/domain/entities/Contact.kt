package com.bamboo.assur.partnerinsurers.registry.domain.entities

import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerContactAddedEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.Model
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Email
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Suppress("LongParameterList")
class Contact(
    id: DomainEntityId,
    val fullName: String,
    val email: Email,
    val phone: Phone,
    val contactRole: String,
    createdAt: Instant,
    updatedAt: Instant,
) : Model(id = id, createdAt = createdAt, updatedAt = updatedAt) {
    init {
        require(fullName.isNotBlank()) { "Full name cannot be blank." }
        require(contactRole.isNotBlank()) { "Contact role cannot be blank." }
    }

    fun update(
        fullName: String?,
        email: Email?,
        phone: Phone?,
        contactRole: String?
    ): Pair<Contact, Map<String, String?>> {
        val updatedFields = mutableMapOf<String, String?>()

        val newFullName = fullName?.takeIf { it != this.fullName }
            ?.also { updatedFields["fullName"] = it }?: this.fullName
        val newEmail = email?.takeIf { it != this.email }
            ?.also { updatedFields["email"] = it.value } ?: this.email
        val newPhone = phone?.takeIf { it != this.phone }
            ?.also { updatedFields["phone"] = it.value } ?: this.phone
        val newContactRole = contactRole?.takeIf { it != this.contactRole }
            ?.also { updatedFields["contactRole"] = it } ?: this.contactRole

        val updatedContact = if (updatedFields.isNotEmpty()) {
            Contact(
                id = this.id,
                fullName = newFullName,
                email = newEmail,
                phone = newPhone,
                contactRole = newContactRole,
                createdAt = this.createdAt,
                updatedAt = Clock.System.now()
            )
        } else {
            this
        }

        return updatedContact to updatedFields
    }

    companion object {
        fun create(
            fullName: String,
            email: Email,
            phone: Phone,
            contactRole: String,
        ): Contact {

            val now = Clock.System.now()

            return Contact(
                id = DomainEntityId.random(),
                fullName = fullName,
                email = email,
                phone = phone,
                contactRole = contactRole,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}
