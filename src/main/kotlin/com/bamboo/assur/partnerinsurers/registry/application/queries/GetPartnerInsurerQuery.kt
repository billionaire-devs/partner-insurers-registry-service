package com.bamboo.assur.partnerinsurers.registry.application.queries

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Query
import java.util.UUID

/**
 * Query object for retrieving a partner insurer with flexible lookup strategies and
 * configurable response views.
 */
data class GetPartnerInsurerQuery(
    val identifier: Identifier,
) : Query {

    sealed interface Identifier {
        data class ById(val id: UUID) : Identifier
        data class ByPartnerCode(val partnerInsurerCode: String) : Identifier
        data class ByTaxIdentificationNumber(val taxIdentificationNumber: String) : Identifier
    }
}
