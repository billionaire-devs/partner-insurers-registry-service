package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurers.registry.application.commands.models.PartnerInsurerUpdate
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerContactTable
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerContactTable.Companion.toEntityTable
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerTable
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerTable.Companion.toEntityTable
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToSaveEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Repository
@Transactional
class PartnerInsurerCommandRepositoryImpl(
    private val partnerInsurerR2dbcRepository: PartnerInsurerR2dbcRepository,
    private val partnerInsurerContactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val transactionalOperator: TransactionalOperator,
) : PartnerInsurerCommandRepository {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override suspend fun save(partnerInsurer: PartnerInsurer): Boolean {
        val partnerInsurerEntity = partnerInsurer.toEntityTable()

        logger.info("Saving partner insurer {}", partnerInsurerEntity.id)

        return transactionalOperator.executeAndAwait {
            // Save partner insurer and contacts in a single transaction
            r2dbcEntityTemplate.insert(partnerInsurerEntity).awaitSingleOrNull()
                ?: throw FailedToSaveEntityException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    partnerInsurerEntity.id
                )

            partnerInsurer.contacts.forEach { contact ->
                val contactEntity = contact.toEntityTable(partnerInsurer.id.value)

                logger.debug(
                    "Saving partner insurer contact {} for partner {}",
                    contactEntity.id,
                    partnerInsurerEntity.id
                )

                r2dbcEntityTemplate.insert(contactEntity).awaitSingleOrNull()
                    ?: throw FailedToSaveEntityException(
                        getAggregateTypeOrEmpty<PartnerInsurerContactTable>(),
                        contact.id
                    )
            }
            logger.info("Partner insurer saved successfully {}", partnerInsurerEntity.id)
            true
        }
    }

    override suspend fun delete(partnerInsurer: PartnerInsurer) {
        partnerInsurerR2dbcRepository.deleteById(partnerInsurer.id.value)
    }

    override suspend fun update(partnerInsurer: PartnerInsurer): Boolean {
        logger.info("Updating Partner insurer: {}", partnerInsurer.id)
        val entity = partnerInsurer.toEntityTable()

        return try {
            val updatedPartnerInsurer = r2dbcEntityTemplate.update(entity).awaitSingleOrNull()
                ?: throw FailedToUpdateEntityException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    entity.id
                )

            logger.info("Partner insurer updated: {}", updatedPartnerInsurer.javaClass)
            true
        } catch (e: Exception) {
            logger.error("Failed during update of partner insurer {}", entity.id, e)
            throw e
        }
    }

    /**
     * Partial update for partner insurer entity.
     * Only updates the fields that are provided in the update object.
     * This is more efficient than full updates as it doesn't fetch contacts and only updates changed fields.
     *
     * @param id UUID id of the partner insurer
     * @param update PartnerInsurerUpdate containing only fields to update
     * @return true if update successful
     */
    override suspend fun partialUpdate(id: UUID, update: PartnerInsurerUpdate): Boolean {
        logger.info("Partial update PartnerInsurer id: {}, fields: {}", id, update)

        if (!update.hasChanges()) {
            logger.info("No changes detected for partner insurer {}", id)
            return true
        }

        val entity = partnerInsurerR2dbcRepository.findById(id) ?: throw EntityNotFoundException(
            PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
            id
        )

        // Create updated entity with only the changed fields
        val updatedEntity = entity.copy(
            legalName = update.legalName ?: entity.legalName,
            logoUrl = update.logoUrl?.value ?: entity.logoUrl,
            address = update.address?.let { Json.encodeToJsonElement(it) } ?: entity.address,
            updatedAt = java.time.Instant.now()
        )

        return try {
            val result = r2dbcEntityTemplate.update(updatedEntity).awaitSingleOrNull()
                ?: throw FailedToUpdateEntityException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    id
                )
            logger.info("Partner insurer partial update applied: {}", result.id)
            true
        } catch (e: Exception) {
            logger.error("Failed during partial update of partner insurer {}", id, e)
            throw e
        }
    }
}
