package com.bamboo.assur.partnerinsurers.registry.domain.repositories

import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
interface ContactRepository {
    suspend fun save(partnerInsurerId: UUID, contact: Contact): Boolean
    suspend fun findById(id: DomainEntityId): Contact?
    suspend fun findByPartnerInsurerId(partnerInsurerId: DomainEntityId): List<Contact>
    suspend fun update(contact: Contact): Boolean
    suspend fun delete(contact: Contact)
}