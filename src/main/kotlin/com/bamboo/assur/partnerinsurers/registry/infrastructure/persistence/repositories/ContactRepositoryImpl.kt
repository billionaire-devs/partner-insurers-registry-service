package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.ContactRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerContactTable.Companion.toEntityTable
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Repository
@Transactional
class ContactRepositoryImpl(
    private val contactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
    private val tableTemplate: R2dbcEntityTemplate,
) : ContactRepository {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    override suspend fun save(partnerInsurerId: UUID, contact: Contact): Boolean {
        logger.info("Saving contact {} for partner {}", contact.id, partnerInsurerId)

        val contactEntity = contact.toEntityTable(partnerInsurerId)
        val saved = tableTemplate.insert(contactEntity)

        return saved.awaitSingleOrNull() == null
    }

    override suspend fun findById(id: DomainEntityId): Contact? {
        logger.debug("Finding contact by id {}", id)

        return contactR2dbcRepository.findById(id.value)?.toDomain()
    }

    override suspend fun findByPartnerInsurerId(partnerInsurerId: DomainEntityId): List<Contact> {
        logger.debug("Finding contacts by partner insurer id {}", partnerInsurerId)

        return contactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(partnerInsurerId.value)
            .toList()
            .map { it.toDomain() }
    }

    override suspend fun update(contact: Contact, partnerInsurerId: UUID): Boolean {
        logger.info("Updating contact {} for partner {}", contact.id, partnerInsurerId)

        val contactEntity = contact.toEntityTable(partnerInsurerId)
        val result = tableTemplate.update(contactEntity).awaitSingleOrNull()

        return if (result != null) {
            logger.info("Contact updated successfully {}", contact.id)
            true
        } else {
            false
        }
    }

    override suspend fun delete(contact: Contact, deletedAt: Instant, deletedBy: UUID) {
        logger.info("Soft deleting contact {}", contact.id)
        
        // First get the existing contact (including deleted ones) to check if it exists
        val existingContact = contactR2dbcRepository.findByIdAndDeletedAtIsNotNull(contact.id.value)
        if (existingContact == null) {
            logger.warn("Contact {} not found for soft deletion", contact.id)
            return
        }
        
        // Check if already soft deleted (idempotent operation)
        if (existingContact.deletedAt != null) {
            logger.info("Contact {} already soft deleted - idempotent operation", contact.id)
            return
        }

        // Create updated contact entity with soft deletion fields
        val softDeletedContact = existingContact.copy(
            deletedAt = deletedAt.toJavaInstant(),
            deletedBy = deletedBy,
            updatedAt = deletedAt.toJavaInstant(),
        )
        
        val result = tableTemplate.update(softDeletedContact).awaitSingleOrNull()
        
        if (result != null) {
            logger.info("Contact soft deleted successfully {}", contact.id)
        } else {
            logger.warn("Failed to soft delete contact {}", contact.id)
        }
    }
}