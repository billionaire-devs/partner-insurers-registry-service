package com.bamboo.assur.partnerinsurersservice.registry.application.queries

import com.bamboo.assur.partnerinsurersservice.core.application.Query
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
