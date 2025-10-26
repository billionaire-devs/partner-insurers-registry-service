package com.bamboo.assur.partnerinsurersservice.registry.domain.repositories

import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Suppress("LongParameterList")
interface PartnerInsurerRepository {
    suspend fun findById(id: UUID): PartnerInsurer?
    suspend fun findByPartnerCode(partnerCode: String): PartnerInsurer?
    suspend fun save(partnerInsurer: PartnerInsurer): Boolean
    suspend fun update(partnerInsurer: PartnerInsurer): Boolean
    suspend fun delete(partnerInsurer: PartnerInsurer)
    suspend fun existsByPartnerCode(partnerCode: String): Boolean
    suspend fun streamAll(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
        sortBy: String?,
        sortDirection: SortDirection
    ): Flow<PartnerInsurerSummary>
}
