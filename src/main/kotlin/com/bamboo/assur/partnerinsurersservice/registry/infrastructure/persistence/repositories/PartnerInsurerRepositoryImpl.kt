package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerContactTable.Companion.fromDomain
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerTable.Companion.fromDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Repository
@Transactional
class PartnerInsurerRepositoryImpl(
    private val partnerInsurerR2dbcRepository: PartnerInsurerR2dbcRepository,
    private val partnerInsurerContactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
    private val json: Json,
) : PartnerInsurerRepository {

    override suspend fun save(partnerInsurer: PartnerInsurer): Boolean {
        val partnerInsurerEntity = partnerInsurer.fromDomain()
        partnerInsurerR2dbcRepository.save(partnerInsurerEntity)

        partnerInsurer.contacts.map { contact ->
            val contactEntity = contact.fromDomain(partnerInsurer.id.value)
            partnerInsurerContactR2dbcRepository.save(contactEntity)
        }

        return true
    }

    override suspend fun findById(id: Uuid): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findById(id) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerId(entity.id.toKotlinUuid())
            .map { it.toDomain() }
            .toSet()

        return entity.toDomain(contacts = contacts, agreements = emptySet())
    }

    override suspend fun findByPartnerCode(partnerCode: String): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findByPartnerInsurerCode(partnerCode) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerId(entity.id.toKotlinUuid())
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
        sortDirection: SortDirection
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