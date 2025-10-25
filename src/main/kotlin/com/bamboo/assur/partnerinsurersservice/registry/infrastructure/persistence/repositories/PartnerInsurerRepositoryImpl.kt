package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurersservice.core.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurersservice.core.domain.FailedToSaveEntityException
import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerContactTable
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerContactTable.Companion.toEntityTable
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerTable
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerTable.Companion.toEntityTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Repository
@Transactional
class PartnerInsurerRepositoryImpl(
    private val partnerInsurerR2dbcRepository: PartnerInsurerR2dbcRepository,
    private val partnerInsurerContactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val transactionalOperator: TransactionalOperator,
) : PartnerInsurerRepository {
    val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun save(partnerInsurer: PartnerInsurer): Boolean {
        logger.info("Starting to save Partner insurer: {}", partnerInsurer)
        val partnerInsurerEntity = partnerInsurer.toEntityTable()

        logger.info("Persisting Partner insurer entity: {}", partnerInsurerEntity)

        return try {
            // Check if entity exists
            logger.info("Checking if entity already exists")

            if (partnerInsurerR2dbcRepository.existsById(partnerInsurerEntity.id)) {
                logger.info("Entity exists with same ID: {}", true)
                throw EntityAlreadyExistsException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    partnerInsurerEntity.id,
                )
            }

            if (
                partnerInsurerR2dbcRepository.existsByPartnerInsurerCode(
                    partnerInsurerEntity.partnerInsurerCode
                )
            ) {
                logger.info("Entity exists with same partner insurer code: {}", true)
                throw EntityAlreadyExistsException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    partnerInsurerEntity.id,
                    entityIdentifierName = "Partner insurer code"
                )
            }

            if (
                partnerInsurerR2dbcRepository.existsByTaxIdentificationNumber(
                    partnerInsurerEntity.taxIdentificationNumber
                )
            ) {
                logger.info("Entity exists with same tax identification number: {}", true)
                throw EntityAlreadyExistsException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    partnerInsurerEntity.id,
                    entityIdentifierName = "Tax identification number"
                )
            }


            // Save partner insurer and contacts in a single transaction
            transactionalOperator.executeAndAwait {
                // Insert partner insurer and capture the saved entity (so we get the DB-generated id if any)
                val savedPartner = r2dbcEntityTemplate.insert(partnerInsurerEntity).awaitSingleOrNull()
                    ?: throw FailedToSaveEntityException(
                        PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                        partnerInsurerEntity.id
                    ).also { logger.error("Failed to save partner insurer: {}", partnerInsurerEntity) }

                logger.debug("Partner insurer saved: {}", savedPartner)

                // Save contacts using the saved partner insurer id
                partnerInsurer.contacts.forEach { contact ->
                    logger.debug("Saving contact: {}", contact)
                    val contactEntity = contact.toEntityTable(savedPartner.id)

                    r2dbcEntityTemplate.insert(contactEntity).awaitSingleOrNull() ?: throw FailedToSaveEntityException(
                        PartnerInsurerContactTable::class.simpleName ?: "PartnerInsurerContactTable",
                        contact.id
                    )
                }
            }

            logger.info("Partner insurer saved successfully with contacts")
            true
        } catch (e: Exception) {
            logger.error("Failed to save partner insurer with contacts", e)
            throw e
        }
    }

    override suspend fun findById(id: kotlin.uuid.Uuid): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findById(id.toJavaUuid()) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerId(entity.id)
            .map { it.toDomain() }
            .toSet()

        return entity.toDomain(contacts = contacts, agreements = emptySet())
    }

    override suspend fun findByPartnerCode(partnerCode: String): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findByPartnerInsurerCode(partnerCode) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerId(entity.id)
            .map { it.toDomain() }
            .toSet()
            .firstOrNull() ?: return null

        return entity.toDomain(contacts = setOf(contacts), agreements = emptySet())
    }

    override suspend fun delete(partnerInsurer: PartnerInsurer) {
        partnerInsurerR2dbcRepository.deleteById(partnerInsurer.id.value)
    }

    override suspend fun existsByPartnerCode(partnerCode: String): Boolean =
        partnerInsurerR2dbcRepository.existsByPartnerInsurerCode(partnerCode)

    override suspend fun streamAll(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: SortDirection,
    ): Flow<PartnerInsurerSummary> {
        val offset = page * size
        return partnerInsurerR2dbcRepository.findAll(
            status = status,
            search = search,
            offset = offset,
            size = size,
            sortBy = sortBy,
            sortDirection = sortDirection.name
        )
    }
}