package com.bamboo.assur.partnerinsurers.registry.domain.repositories

import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Suppress("TooManyFunctions")
interface PartnerInsurerQueryRepository {

    suspend fun findByIdSummary(id: UUID): PartnerInsurerProjection.SummaryProjection?
    suspend fun findByIdDetailed(id: UUID): PartnerInsurerProjection.DetailedProjection?
    suspend fun findById(id: UUID): PartnerInsurer?

    suspend fun findByPartnerCodeSummary(
        partnerCode: String
    ): PartnerInsurerProjection.SummaryProjection?
    suspend fun findByPartnerCodeDetailed(
        partnerCode: String
    ): PartnerInsurerProjection.DetailedProjection?
    suspend fun findByPartnerCode(partnerCode: String): PartnerInsurer?

    suspend fun findByTaxIdentificationNumberSummary(
        taxIdentificationNumber: String
    ): PartnerInsurerProjection.SummaryProjection?
    suspend fun findByTaxIdentificationNumberDetailed(
        taxIdentificationNumber: String
    ): PartnerInsurerProjection.DetailedProjection?
    suspend fun findByTaxIdentificationNumber(taxIdentificationNumber: String): PartnerInsurer?

    suspend fun findByIdForUpdate(id: UUID): PartnerInsurer?
    suspend fun existById(id: UUID): Boolean
    suspend fun existsByPartnerCode(partnerCode: String): Boolean
    suspend fun existsByTaxIdentificationNumber(taxIdentificationNumber: String): Boolean

    @Suppress("LongParameterList")
    suspend fun streamAll(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: SortDirection,
    ): Flow<PartnerInsurerSummary>
}
