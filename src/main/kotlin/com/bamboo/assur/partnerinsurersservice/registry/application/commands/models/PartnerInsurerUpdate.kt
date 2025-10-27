package com.bamboo.assur.partnerinsurersservice.registry.application.commands.models

import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url

/**
 * Represents a partial update model for PartnerInsurer entity.
 * Contains only the fields that can be updated, allowing for efficient partial updates.
 */
data class PartnerInsurerUpdate(
    val legalName: String?,
    val logoUrl: Url?,
    val address: Address?,
) {
    /**
     * Checks if this update contains any actual changes.
     */
    fun hasChanges(): Boolean = legalName != null || logoUrl != null || address != null
}