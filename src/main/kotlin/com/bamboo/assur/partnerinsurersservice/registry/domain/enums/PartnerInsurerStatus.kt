package com.bamboo.assur.partnerinsurersservice.registry.domain.enums

enum class  PartnerInsurerStatus {
    ONBOARDING, ACTIVE, SUSPENDED, MAINTENANCE, DEACTIVATED;

    fun isActive() = this == ACTIVE
    fun isSuspended() = this == SUSPENDED
    fun isInMaintenance() = this == MAINTENANCE
    fun isDeactivated() = this == DEACTIVATED
    fun isOnboarding() = this == ONBOARDING
}