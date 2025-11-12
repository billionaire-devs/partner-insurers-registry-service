@file:OptIn(ExperimentalTime::class)

package com.bamboo.assur.partnerinsurers.registry.application.queries.handlers

import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurersQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.domain.utils.formatISODateToInstant
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.GetPartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.GetPartnerInsurerResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.springframework.stereotype.Service
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

/**
 * Handles the GetPartnerSummariesQuery.
 *
 * Uses a read-only repository to fetch lightweight PartnerInsurer summaries.
 * Returns a Result wrapper to explicitly represent success or failure.
 */
@Service
class GetPartnerInsurersQueryHandler(
    private val repository: PartnerInsurerQueryRepository,
) : QueryHandler<GetPartnerInsurersQuery, Flow<GetPartnerInsurerResponseDto>> {

    override suspend fun invoke(query: GetPartnerInsurersQuery): Flow<GetPartnerInsurerResponseDto> = repository
        .findAllDetailed(
            search = query.search,
            status = query.status?.name,
            page = query.page,
            size = query.size,
            sortBy = query.sort?.name,
            sortDirection = query.sortDirection.name,
            createdBefore = query.createdBefore?.formatISODateToInstant()?.toJavaInstant(),
            createdAfter = query.createdAfter?.formatISODateToInstant()?.toJavaInstant(),
        ).map { it.toResponseDto() }
}

/**
 * Handles the GetPartnerSummariesQuery.
 *
 * Uses a read-only repository to fetch lightweight PartnerInsurer summaries.
 * Returns a Result wrapper to explicitly represent success or failure.
 */
@Service
class GetPartnerInsurersSummariesQueryHandler(
    private val readRepository: PartnerInsurerQueryRepository,
) : QueryHandler<GetPartnerInsurersQuery, Flow<PartnerInsurerProjection.SummaryProjection>> {

    override suspend fun invoke(query: GetPartnerInsurersQuery): Flow<PartnerInsurerProjection.SummaryProjection> {
        return readRepository
            .findAllSummary(
                search = query.search,
                status = query.status?.name,
                page = query.page,
                size = query.size,
                sortBy = query.sort?.name,
                sortDirection = query.sortDirection.name,
                createdBefore = query.createdBefore?.formatISODateToInstant()?.toJavaInstant(),
                createdAfter = query.createdAfter?.formatISODateToInstant()?.toJavaInstant(),
            )
    }
}
