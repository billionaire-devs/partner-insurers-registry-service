package com.bamboo.assur.partnerinsurers.registry.application.queries.handlers

import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurerQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Service
class GetPartnerInsurerDetailedProjectionQueryHandler(
    private val repository: PartnerInsurerQueryRepository,
) : QueryHandler<GetPartnerInsurerQuery, PartnerInsurerProjection.DetailedProjection?> {

    override suspend fun invoke(
        query: GetPartnerInsurerQuery,
    ): PartnerInsurerProjection.DetailedProjection? {
        return when (query.identifier) {
            is GetPartnerInsurerQuery.Identifier.ById -> {
                repository.findByIdDetailed(query.identifier.id) ?: throw EntityNotFoundException(
                    getAggregateTypeOrEmpty<PartnerInsurer>(),
                    query.identifier.id
                )
            }

            is GetPartnerInsurerQuery.Identifier.ByPartnerCode -> {
                repository.findByPartnerCodeDetailed(query.identifier.partnerInsurerCode)
                    ?: throw EntityNotFoundException(
                        getAggregateTypeOrEmpty<PartnerInsurer>(),
                        query.identifier.partnerInsurerCode
                    )
            }

            is GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber -> {
                repository.findByTaxIdentificationNumberDetailed(query.identifier.taxIdentificationNumber)
                    ?: throw EntityNotFoundException(
                        getAggregateTypeOrEmpty<PartnerInsurer>(),
                        query.identifier.taxIdentificationNumber
                    )
            }
        }
    }
}
