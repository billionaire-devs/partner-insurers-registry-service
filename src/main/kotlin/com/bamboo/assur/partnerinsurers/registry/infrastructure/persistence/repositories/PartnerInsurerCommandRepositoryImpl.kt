package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerContactTable
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerContactTable.Companion.toEntityTable
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerTable
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerTable.Companion.toEntityTable
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToSaveEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Repository
@Transactional
class PartnerInsurerCommandRepositoryImpl(
    private val partnerInsurerR2dbcRepository: PartnerInsurerR2dbcRepository,
    private val partnerInsurerContactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
    private val tableTemplate: R2dbcEntityTemplate,
    private val transactionalOperator: TransactionalOperator,
) : PartnerInsurerCommandRepository {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override suspend fun save(partnerInsurer: PartnerInsurer): Boolean {
        val partnerInsurerEntity = partnerInsurer.toEntityTable()

        logger.info("Saving partner insurer {}", partnerInsurerEntity.id)

        return transactionalOperator.executeAndAwait {
            // Save partner insurer and contacts in a single transaction
            tableTemplate.insert(partnerInsurerEntity).awaitSingleOrNull()
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

                tableTemplate.insert(contactEntity).awaitSingleOrNull()
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

        val updatedPartnerInsurer = tableTemplate.update(entity).awaitSingleOrNull()

        return if (updatedPartnerInsurer != null) {
            logger.info("Partner insurer updated: {}", getAggregateTypeOrEmpty<PartnerInsurerTable>())
            true
        } else {
            false
        }
    }
}
