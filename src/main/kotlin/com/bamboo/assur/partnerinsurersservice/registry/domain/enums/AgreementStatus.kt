package com.bamboo.assur.partnerinsurersservice.registry.domain.enums

/**
 * Represents the possible statuses of a contract in the system.
 *
 * @property DRAFT The contract is being drafted and is not yet validated.
 * @property PENDING The contract is validated but the starting date has not arrived yet.
 * @property ACTIVE The contract has been validated and the starting date has arrived.
 * @property SUSPENDED The contract is temporarily suspended.
 * @property EXPIRED The contract has reached its end date and is no longer active.
 * @property TERMINATED The contract has been terminated before its expiration.
 */
enum class AgreementStatus {
    /** The contract is being drafted and is not yet validated. */
    DRAFT,

    /** The contract is validated but the starting date has not arrived yet. */
    PENDING,

    /** The contract has been validated and the starting date has arrived. */
    ACTIVE,

    /** The contract is temporarily suspended. */
    SUSPENDED,

    /** The contract has reached its end date and is no longer active. */
    EXPIRED,

    /** The contract has been terminated before its expiration. */
    TERMINATED
}