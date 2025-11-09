package com.bamboo.assur.partnerinsurers.registry.domain.repositories

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection
import com.bamboo.assur.partnerinsurers.registry.application.commands.models.PartnerInsurerUpdate
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import kotlinx.coroutines.flow.Flow
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Suppress("LongParameterList")
interface PartnerInsurerRepository {
    suspend fun findById(id: UUID): PartnerInsurer?
    suspend fun findByIdForUpdate(id: UUID): PartnerInsurer?
    suspend fun findByPartnerCode(partnerCode: String): PartnerInsurer?
    suspend fun save(partnerInsurer: PartnerInsurer): Boolean
    suspend fun update(partnerInsurer: PartnerInsurer): Boolean
    suspend fun partialUpdate(id: UUID, update: PartnerInsurerUpdate): Boolean
    suspend fun delete(partnerInsurer: PartnerInsurer)
    suspend fun existById(id: UUID): Boolean
    suspend fun existsByPartnerCode(partnerCode: String): Boolean
    suspend fun existsByTaxIdentificationNumber(taxIdentificationNumber: String): Boolean
    suspend fun streamAll(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: SortDirection
    ): Flow<PartnerInsurerSummary>
}
