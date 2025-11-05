package com.bamboo.assur.partnerinsurers.registry.application.queries

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Query
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

/**
 * Query object for retrieving a single Partner Insurer by ID.
 *
 * This represents a read-only operation (CQS pattern) that allows
 * fetching complete details of a specific partner insurer.
 *
 * @property id The unique identifier of the partner insurer to retrieve.
 */
@OptIn(ExperimentalUuidApi::class)
data class GetPartnerInsurerByIdQuery(
    val id: UUID
) : Query
