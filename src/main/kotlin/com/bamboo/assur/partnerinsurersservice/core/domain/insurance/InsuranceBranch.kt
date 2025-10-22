package com.bamboo.assur.partnerinsurersservice.core.domain.insurance

/**
 * Represents a specific branch of insurance, such as "Auto Insurance" or "Home Insurance".
 * Each branch belongs to a broader [InsuranceFamily].
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