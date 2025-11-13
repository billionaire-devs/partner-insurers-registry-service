package com.bamboo.assur.partnerinsurers.registry.domain.valueObjects

import com.bamboo.assur.partnerinsurers.registry.domain.enums.InsuranceFamily

/**
 * Represents a specific branch of insurance, such as "Auto Insurance" or "Home Insurance".
 * Each branch belongs to a broader [com.bamboo.assur.partnerinsurers.registry.domain.enums.InsuranceFamily].
 *
 * @property code A unique identifier for this insurance branch (e.g., "AUTO", "HOME").
 * @property label The human-readable name of the insurance branch (e.g., "Auto Insurance").
 * @property description A detailed explanation of what this insurance branch covers.
 * @property family The overarching category this insurance branch falls under (e.g., "Property & Casualty").
 */
data class InsuranceBranch(
    val code: String,
    val label: String,
    val description: String,
    val family: InsuranceFamily
)