package com.bamboo.assur.partnerinsurers.registry.registry.domain.entities

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
