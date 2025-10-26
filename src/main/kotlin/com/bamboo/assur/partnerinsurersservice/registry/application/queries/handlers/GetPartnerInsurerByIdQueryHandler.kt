package com.bamboo.assur.partnerinsurersservice.registry.application.queries.handlers

import com.bamboo.assur.partnerinsurersservice.core.application.QueryHandler
import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.GetPartnerInsurerByIdQuery
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.responses.PartnerInsurerDetailResponseDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.responses.PartnerInsurerDetailResponseDto.Companion.toResponseDTO
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi

/**
 * Handles the GetPartnerInsurerByIdQuery.
 *
 * Uses the repository to fetch a single PartnerInsurer by its ID
 * and converts it to a detailed DTO for the presentation layer.
 * Returns a Result wrapper to explicitly represent success or failure.
 */
@OptIn(ExperimentalUuidApi::class)
@Service
class GetPartnerInsurerByIdQueryHandler(
    private val repository: PartnerInsurerRepository,
) : QueryHandler<GetPartnerInsurerByIdQuery, Result<PartnerInsurer>> {

    override suspend fun invoke(
        query: GetPartnerInsurerByIdQuery,
    ): Result<PartnerInsurer> = Result.of {
        repository.findById(query.id) ?: throw NoSuchElementException("Partner insurer with id ${query.id} not found")
    }
}
