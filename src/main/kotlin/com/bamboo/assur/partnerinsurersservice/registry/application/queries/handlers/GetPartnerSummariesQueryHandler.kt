package com.bamboo.assur.partnerinsurersservice.registry.application.queries.handlers

import com.bamboo.assur.partnerinsurersservice.core.application.QueryHandler
import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.GetPartnerSummariesQuery
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

/**
 * Handles the GetPartnerSummariesQuery.
 *
 * Uses a read-only repository to fetch lightweight PartnerInsurer summaries.
 * Returns a Result wrapper to explicitly represent success or failure.
 */
@Service
class GetPartnerSummariesQueryHandler(
    private val readRepository: PartnerInsurerRepository,
) : QueryHandler<GetPartnerSummariesQuery, Result<List<PartnerInsurerSummary>>> {

    override suspend fun handle(query: GetPartnerSummariesQuery): Result<List<PartnerInsurerSummary>> = Result.of {
        readRepository.streamAll(
            status = query.status,
            search = query.search,
            page = query.page,
            size = query.size,
            sortBy = query.sortBy,
            sortDirection = query.sortDirection
        ).toList().also {
            if (it.isEmpty()) {
                throw NoSuchElementException("No partner insurer summaries found for given criteria.")
            }
        }
    }
}