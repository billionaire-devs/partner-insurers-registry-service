package com.bamboo.assur.partnerinsurers.registry.domain.repositories

import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
interface ContactRepository {
    suspend fun save(partnerInsurerId: UUID, contact: Contact): Boolean
    suspend fun findById(id: DomainEntityId): Contact?
    suspend fun findByPartnerInsurerId(partnerInsurerId: DomainEntityId): List<Contact>
    suspend fun update(contact: Contact, partnerInsurerId: UUID): Boolean
    suspend fun delete(contact: Contact, deletedAt: Instant, deleteBy: UUID)
}