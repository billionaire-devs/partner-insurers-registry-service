package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ContactResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.collections.emptySet
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Suppress("TooManyFunctions")
@Repository
@Transactional
class PartnerInsurerQueryRepositoryImpl(
    private val partnerInsurerR2dbcRepository: PartnerInsurerR2dbcRepository,
    private val partnerInsurerContactR2dbcRepository: PartnerInsurerContactR2dbcRepository,
): PartnerInsurerQueryRepository {
    override suspend fun findByIdSummary(id: UUID): PartnerInsurerProjection.SummaryProjection =
        partnerInsurerR2dbcRepository.findByIdSummary(id)

    @OptIn(ExperimentalTime::class)
    override suspend fun findByIdDetailed(id: UUID): PartnerInsurerProjection.FullProjection? {
        val partner = partnerInsurerR2dbcRepository.findByIdDetailed(id) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(partner.id)
            .map { it.toDomain().toResponseDto() } //TODO: Simplify the conversion
            .toSet()

        // TODO: Add agreements retrieval

        return partner.copy(contacts = contacts)
    }

    @Transactional
    override suspend fun findById(id: UUID): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findById(id) ?: throw EntityNotFoundException(
            getAggregateTypeOrEmpty<PartnerInsurer>(),
            id
        )
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(entity.id)
            .map { it.toDomain() }
            .toSet()

        //

        return entity.toDomain(contacts = contacts, agreements = emptySet())
    }

    override suspend fun findByPartnerCode(partnerCode: String): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findByPartnerInsurerCode(partnerCode) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(entity.id)
            .map { it.toDomain() }
            .toSet()

        return entity.toDomain(contacts = contacts, agreements = emptySet())
    }

    override suspend fun findByPartnerCodeSummary(partnerCode: String) : PartnerInsurerProjection.SummaryProjection? =
        partnerInsurerR2dbcRepository.findByPartnerCodeSummary(partnerCode)

    override suspend fun findByPartnerCodeDetailed(partnerCode: String): PartnerInsurerProjection.FullProjection? {
        val partner = partnerInsurerR2dbcRepository.findByPartnerCodeDetailed(partnerCode) ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(partner.id)
            .map { it.toDomain().toResponseDto() }
            .toSet()
        
        return partner.copy(contacts = contacts)
    }

    /**
     * Used for partial update (PATCH) - returns the PartnerInsurerTable entity (without contacts)
     */
    override suspend fun findByIdForUpdate(id: UUID): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findById(id) ?: return null
        // For updates, we don't need contacts, so return with empty contacts set
        return entity.toDomain(contacts = emptySet(), agreements = emptySet())
    }

    override suspend fun findByTaxIdentificationNumberSummary(
        taxIdentificationNumber: String
    ): PartnerInsurerProjection.SummaryProjection? = partnerInsurerR2dbcRepository
        .findByTaxIdentificationNumberSummary(taxIdentificationNumber)

    override suspend fun findByTaxIdentificationNumberDetailed(
        taxIdentificationNumber: String
    ): PartnerInsurerProjection.FullProjection? {
        val partner = partnerInsurerR2dbcRepository.findByTaxIdentificationNumberDetailed(taxIdentificationNumber)
            ?: return null
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(partner.id)
            .map { it.toDomain().toResponseDto() }
            .toSet()
        
        return partner.copy(contacts = contacts)
    }

    override suspend fun findByTaxIdentificationNumber(taxIdentificationNumber: String): PartnerInsurer? {
        val entity = partnerInsurerR2dbcRepository.findByTaxIdentificationNumberAndDeletedAtIsNull(taxIdentificationNumber)
        val contacts = partnerInsurerContactR2dbcRepository.findByPartnerInsurerIdAndDeletedAtIsNull(entity.id)
            .map { it.toDomain() }
            .toSet()
        
        return entity.toDomain(contacts = contacts, agreements = emptySet())
    }


    override suspend fun existsByPartnerCode(partnerCode: String): Boolean =
        partnerInsurerR2dbcRepository.existsByPartnerInsurerCodeAndDeletedAtIsNull(partnerCode)

    override suspend fun existsByTaxIdentificationNumber(taxIdentificationNumber: String): Boolean =
        partnerInsurerR2dbcRepository.existsByTaxIdentificationNumberAndDeletedAtIsNull(taxIdentificationNumber)

    override suspend fun existById(id: UUID): Boolean = partnerInsurerR2dbcRepository.existsById(id)
    override suspend fun existsByIdAndNotDeleted(id: UUID): Boolean = partnerInsurerR2dbcRepository.existsByIdAndDeletedAtIsNull(id)
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

    override suspend fun findAllSummary(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: String,
        createdBefore: Instant?,
        createdAfter: Instant?
    ): Flow<PartnerInsurerProjection.SummaryProjection> = partnerInsurerR2dbcRepository.findAllSummary(
            status = status,
            search = search,
            page = page,
            size = size,
            sortBy = sortBy,
            sortDirection = sortDirection,
            createdBefore = createdBefore,
            createdAfter = createdAfter
        )


    override suspend fun findAllDetailed(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: String,
        createdBefore: Instant?,
        createdAfter: Instant?,
    ): Flow<PartnerInsurer> = partnerInsurerR2dbcRepository.findAllDetailed(
        status = status,
        search = search,
        page = page,
        size = size,
        sortBy = sortBy,
        sortDirection = sortDirection,
        createdBefore = createdBefore,
        createdAfter = createdAfter
    ).map { partnerTable ->
        partnerTable.toDomain(
            contacts = partnerInsurerContactR2dbcRepository
                .findByPartnerInsurerIdAndDeletedAtIsNull(partnerTable.id)
                .map { contact -> contact.toDomain() }
                .toSet(),
            agreements = emptySet()
        )
    }

}