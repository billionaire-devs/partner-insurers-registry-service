package com.bamboo.assur.partnerinsurersservice.registry.application.queries

import com.bamboo.assur.partnerinsurersservice.core.application.Query
import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection

/**
 * Query object for retrieving summaries of Partner Insurers.
 *
 * This represents a read-only operation (CQS pattern) that allows
 * fetching lightweight projections of partner insurers for
 * display, listing, or dashboard use.
 *
 * Filtering, sorting, and pagination can be extended later.
 *
 * @property status Optional filter by partner insurer status.
 * @property search Optional free-text search on name or code.
 * @property page Zero-based page index (default 0).
 * @property size Page size (default 50).
 * @property sortBy Optional field to sort by (e.g. "legalName", "status").
 * @property sortDirection Sort direction: ASC or DESC.
 */
data class GetPartnerSummariesQuery(
    val status: String? = null,
    val search: String? = null,
    val page: Int = 0,
    val size: Int = 50,
    val sortBy: String? = null,
    val sortDirection: SortDirection = SortDirection.ASC
): Query
