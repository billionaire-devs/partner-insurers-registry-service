package com.bamboo.assur.partnerinsurers.registry.application.queries

import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerSortTerm
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.sharedkernel.application.Query
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryView
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection
import kotlinx.datetime.LocalDate
import java.time.Instant


/**
 * A query for retrieving a list of partner insurers.
 *
 * @property status The status of the partner insurers to retrieve. Defaults to [PartnerInsurerStatus.ACTIVE].
 * @property search A search term to filter the partner insurers by. Defaults to null.
 * @property page The page number of the results. Defaults to 0.
 * @property size The maximum number of partner insurers to retrieve. Defaults to 20.
 * @property sort The field to sort the partner insurers by. Defaults to null.
 * @property sortDirection The direction to sort the partner insurers in. Defaults to [SortDirection.ASC].
 * @property createdBefore The upper bound of the date range to filter the partner insurers by.
 * Defaults to [Instant.now].
 * @property createdAfter The lower bound of the date range to filter the partner insurers by.
 * Defaults to [Instant.now].
 * @property view The view of the partner insurers to retrieve. Defaults to [QueryView.SUMMARY].
 */
data class GetPartnerInsurersQuery(
    val status: PartnerInsurerStatus? = null,
    val search: String? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sort: PartnerInsurerSortTerm? = null,
    val sortDirection: SortDirection = SortDirection.ASC,
    val createdBefore: String?,
    val createdAfter: String?,
    val view: QueryView = QueryView.SUMMARY,
) : Query