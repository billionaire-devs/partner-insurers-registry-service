package com.bamboo.assur.partnerinsurers.registry.application.queries.handlers

import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurerQuery
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Service
class GetPartnerInsurerQueryHandler(
    private val queryRepository: PartnerInsurerQueryRepository,
) : QueryHandler<GetPartnerInsurerQuery, PartnerInsurer> {

    override suspend fun invoke(
        query: GetPartnerInsurerQuery,
    ): PartnerInsurer {
        return when (query.identifier) {
            is GetPartnerInsurerQuery.Identifier.ById -> {
                queryRepository.findById(query.identifier.id) ?: throw EntityNotFoundException(
                    getAggregateTypeOrEmpty<PartnerInsurer>(),
                    query.identifier.id
                )
            }

            is GetPartnerInsurerQuery.Identifier.ByPartnerCode -> {
                queryRepository.findByPartnerCode(query.identifier.partnerInsurerCode)
                    ?: throw EntityNotFoundException(
                        getAggregateTypeOrEmpty<PartnerInsurer>(),
                        query.identifier.partnerInsurerCode
                    )
            }

            is GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber -> {
                queryRepository.findByTaxIdentificationNumber(query.identifier.taxIdentificationNumber)
                    ?: throw EntityNotFoundException(
                        getAggregateTypeOrEmpty<PartnerInsurer>(),
                        query.identifier.taxIdentificationNumber
                    )
            }
        }
    }
}
