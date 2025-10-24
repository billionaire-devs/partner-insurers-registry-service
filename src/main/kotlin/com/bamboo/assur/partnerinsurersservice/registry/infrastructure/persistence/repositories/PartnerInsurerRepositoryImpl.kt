package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurersservice.core.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
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
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import java.util.UUID

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Repository
@Transactional
class PartnerInsurerRepositoryImpl(
    private val partnerInsurerR2dbcRepository: PartnerInsurerR2dbcRepository,
    private val partnerInsurerContactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : PartnerInsurerRepository {
    val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun save(partnerInsurer: PartnerInsurer): Boolean {
        logger.debug("Starting to save Partner insurer: {}", partnerInsurer)
        val partnerInsurerEntity = partnerInsurer.toEntityTable()

        logger.debug("Persisting Partner insurer entity: {}", partnerInsurerEntity)
        // If the id already exists in DB -> perform update via repository.save
        // Else perform an INSERT using R2dbcEntityTemplate to avoid save() attempting an UPDATE
        val id: UUID = partnerInsurerEntity.id
        try {
            val entityExists = partnerInsurerR2dbcRepository.existsById(id)
            logger.info("Entity exists: $entityExists")

            if (entityExists) {
                throw EntityAlreadyExistsException(
                    PartnerInsurerTable::class.simpleName ?: "PartnerInsurerTable",
                    partnerInsurerEntity.id
                )
            }

            r2dbcEntityTemplate.insert(partnerInsurerEntity).awaitSingle()
        } catch (e: Exception) {
            logger.error("Failed to persist partner insurer entity", e)
            throw e
        }
        logger.debug("Partner insurer saved")

//        logger.debug("Saving partner insurer contacts")
//        partnerInsurer.contacts.map { contact ->
//            val contactEntity = contact.fromDomain(partnerInsurer.id.value)
//            partnerInsurerContactR2dbcRepository.save(contactEntity)
//        }
//        logger.debug("All partner insurer contacts saved")

        logger.debug("Partner insurer saved successfully")
        return true
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