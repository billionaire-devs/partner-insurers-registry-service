package com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection

data class GetPartnerInsurerSummariesRequestDto(
    val status: String? = null,
    val search: String? = null,
    val page: Int = 0,
    val size: Int = 10,
    val sortBy: String? = null,
    val sortDirection: SortDirection = SortDirection.ASC
)