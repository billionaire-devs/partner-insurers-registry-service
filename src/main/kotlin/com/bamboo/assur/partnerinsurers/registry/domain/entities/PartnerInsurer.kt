@file:OptIn(ExperimentalUuidApi::class)

package com.bamboo.assur.partnerinsurers.registry.domain.entities

import com.bamboo.assur.partnerinsurers.registry.application.commands.models.PartnerInsurerUpdate
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerContactAddedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerCreatedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerStatusChangedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerUpdatedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.AggregateRoot
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.InvalidOperationException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

/**
 * Represents a Partner Insurer aggregate root in the domain.
 *
 * A Partner Insurer is a key entity that encapsulates all the information and behavior
 * related to an insurance partner. It manages its own lifecycle, including status changes,
 * and ensures data consistency through its internal invariants.
 *
 * @property id The unique identifier of the partner insurer.
 * @property partnerInsurerCode A unique code identifying the partner insurer.
 * @property legalName The legal name of the partner insurer.
 * @property taxIdentificationNumber The tax identification number of the partner insurer.
 * @property logoUrl The URL to the partner insurer's logo.
 * @property contacts A mutable list of contact persons for the partner insurer.
 * @property brokerPartnerInsurerAgreements A mutable list of contracts associated with the partner insurer.
 * @property status The current status of the partner insurer, defaulting to [PartnerInsurerStatus.ONBOARDING].
 * @property address The physical address of the partner insurer.
 */
@Suppress("LongParameterList")
class PartnerInsurer private constructor(
    id: DomainEntityId,
    val partnerInsurerCode: String, // TODO(Define business validation rules for code creation)
    var legalName: String,
    val taxIdentificationNumber: TaxIdentificationNumber,
    var logoUrl: Url?,
    val contacts: MutableSet<Contact>,
    val brokerPartnerInsurerAgreements: MutableSet<BrokerPartnerInsurerAgreement>,
    var status: PartnerInsurerStatus,
    var address: Address,
) : AggregateRoot(id) {
    init {
        require(partnerInsurerCode.isNotBlank()) { "Partner code cannot be blank" }
        require(legalName.isNotBlank()) { "Partner name cannot be blank" }
    }

    @OptIn(ExperimentalTime::class)
    companion object {

        /**
         * Factory method to create a new [PartnerInsurer] instance.
         *
         * This method is used when a new partner insurer is being onboarded. It initializes
         * the partner with essential details and sets its initial status.
         *
         * @param partnerInsurerCode A unique code for the new partner insurer.
         * @param legalName The legal name of the new partner insurer.
         * @param taxIdentificationNumber The tax identification number.
         * @param logoUrl The URL to the partner's logo.
         * @param contacts Contact persons for the partner.
         * @param address The physical address of the partner.
         * @param status The initial status of the partner, defaults to [PartnerInsurerStatus.ONBOARDING].
         * @return A [PartnerInsurer] object containing the details for creating a new partner.
         */
        fun create(
            partnerInsurerCode: String,
            legalName: String,
            taxIdentificationNumber: TaxIdentificationNumber,
            logoUrl: Url?,
            contacts: Set<Contact>,
            address: Address,
            status: PartnerInsurerStatus = PartnerInsurerStatus.ONBOARDING,
        ): PartnerInsurer {
            val id = DomainEntityId.random()

            val partnerInsurer = PartnerInsurer(
                id = id,
                partnerInsurerCode = partnerInsurerCode,
                legalName = legalName,
                brokerPartnerInsurerAgreements = mutableSetOf(),
                taxIdentificationNumber = taxIdentificationNumber,
                logoUrl = logoUrl,
                contacts = contacts.toMutableSet(),
                address = address,
                status = status
            )

            partnerInsurer.addDomainEvent(
                PartnerInsurerCreatedEvent(
                    aggregateIdValue = id,
                    partnerInsurerCode = partnerInsurerCode,
                    legalName = legalName,
                    taxIdentificationNumber = taxIdentificationNumber.value,
                    status = status,
                    createdAt = partnerInsurer.createdAt
                )
            )

            return partnerInsurer
        }

        /**
         * Factory method to reconstitute an existing [PartnerInsurer] instance from persistence.
         *
         * This method is used to load a partner insurer from a data store, restoring its
         * state without triggering any domain events related to creation.
         *
         * @param id The unique identifier of the partner insurer.
         * @param partnerInsurerCode The code of the partner insurer.
         * @param legalName The legal name of the partner insurer.
         * @param taxIdentificationNumber The tax identification number.
         * @param logoUrl The URL to the partner's logo.
         * @param contacts A mutable list of contact persons.
         * @param address The physical address of the partner.
         * @param status The current status of the partner.
         * @param brokerPartnerInsurerAgreements A mutable list of contracts.
         * @return A reconstituted [PartnerInsurer] instance.
         */
        fun reconstitute(
            id: DomainEntityId,
            partnerInsurerCode: String,
            legalName: String,
            taxIdentificationNumber: TaxIdentificationNumber,
            logoUrl: Url?,
            contacts: Set<Contact>,
            address: Address,
            status: PartnerInsurerStatus,
            brokerPartnerInsurerAgreements: Set<BrokerPartnerInsurerAgreement>,
        ): PartnerInsurer = PartnerInsurer(
            id = id,
            partnerInsurerCode = partnerInsurerCode,
            legalName = legalName,
            taxIdentificationNumber = taxIdentificationNumber,
            logoUrl = logoUrl,
            contacts = contacts.toMutableSet(),
            address = address,
            status = status,
            brokerPartnerInsurerAgreements = brokerPartnerInsurerAgreements.toMutableSet(),
        )
    }

    /**
     * Activates the partner insurer.
     *
     * Changes the partner's status to [PartnerInsurerStatus.ACTIVE].
     * Throws an [InvalidOperationException] if the partner is already active.
     *
     * @param reason An optional reason for the activation.
     */
    fun activate(reason: String? = null) {
        changeStatusTo(PartnerInsurerStatus.ACTIVE, reason) { status == PartnerInsurerStatus.ACTIVE }
    }

    /**
     * Suspends the partner insurer.
     *
     * Changes the partner's status to [PartnerInsurerStatus.SUSPENDED].
     * Throws an [InvalidOperationException] if the partner is already suspended,
     * in onboarding, or in maintenance.
     *
     * @param reason The reason for the suspension.
     */
    fun suspend(reason: String) {
        changeStatusTo(PartnerInsurerStatus.SUSPENDED, reason) { status == PartnerInsurerStatus.SUSPENDED }
    }

    /**
     * Puts the partner insurer on maintenance.
     *
     * Changes the partner's status to [PartnerInsurerStatus.MAINTENANCE].
     * Throws an [InvalidOperationException] if the partner is currently active.
     *
     * @param reason The reason for putting the partner on maintenance.
     */
    fun putInMaintenance(reason: String) {
        changeStatusTo(PartnerInsurerStatus.MAINTENANCE, reason) { status == PartnerInsurerStatus.ACTIVE }
    }

    /**
     * Deactivates the partner insurer.
     *
     * Changes the partner's status to [PartnerInsurerStatus.SUSPENDED].
     * Throws an [InvalidOperationException] if the partner is not active, onboarding, or in maintenance.
     *
     * @param reason The reason for the deactivation.
     */
    fun deactivate(reason: String) {
        changeStatusTo(PartnerInsurerStatus.DEACTIVATED, reason) { status == PartnerInsurerStatus.DEACTIVATED }
    }

    /**
     * Internal helper function to change the status of the partner insurer.
     *
     * This function applies a status change, performs a guard check, updates the `status` property,
     * and records a [PartnerInsurerStatusChangedEvent].
     * @param newStatus The new status to set for the partner insurer.
     * @param reason An optional reason for the status change.
     * @param guard A lambda function that returns `true` if the status change is invalid, `false` otherwise.
     */
    private fun changeStatusTo(newStatus: PartnerInsurerStatus, reason: String?,  guard: () -> Boolean) {
        if (guard()) {
            throw InvalidOperationException("Cannot change status from $status to $newStatus.")
        }

        val oldStatus = status
        status = newStatus
        touch()

        addDomainEvent(
            PartnerInsurerStatusChangedEvent(
                aggregateIdValue = id,
                oldStatus = oldStatus.name,
                newStatus = newStatus.name,
                reason = reason
            )
        )
    }

    fun addContact(contact: Contact) {
        contacts.add(contact)
        touch()

        addDomainEvent(
            PartnerInsurerContactAddedEvent(
                aggregateIdValue = id,
                contact = contact,
                partnerCode = partnerInsurerCode,
            )
        )
    }

    /**
     * Updates the partner insurer with new information.
     *
     * This method allows updating various properties of the partner insurer including
     * legal name, logo URL, and address.
     * It validates the updates and records a [PartnerInsurerUpdatedEvent].
     *
     * @param legalName The new legal name of the partner insurer.
     * @param logoUrl The new URL to the partner's logo.
     * @param address The new physical address of the partner.
     */
    fun update(
        legalName: String,
        logoUrl: Url?,
        address: Address,
    ) {
        require(legalName.isNotBlank()) { "Legal name cannot be blank" }

        // Update mutable properties
        this.legalName = legalName
        this.logoUrl = logoUrl
        this.address = address

        touch()

        addDomainEvent(
            PartnerInsurerUpdatedEvent(
                aggregateIdValue = id,
                legalName = legalName,
                logoUrl = logoUrl,
                address = address,
            )
        )
    }

    /**
     * Performs a partial update of the partner insurer with only the provided fields.
     * This is more efficient than a full update as it only changes the specified properties.
     *
     * @param update The partial update containing only the fields to be changed.
     */
    fun partialUpdate(update: PartnerInsurerUpdate) {
        if (!update.hasChanges()) return

        if (update.legalName != null) {
            require(update.legalName.isNotBlank()) { "Legal name cannot be blank" }
            this.legalName = update.legalName
        }

        if (update.logoUrl != null) {
            this.logoUrl = update.logoUrl
        }

        if (update.address != null) {
            this.address = update.address
        }

        touch()

        addDomainEvent(
            PartnerInsurerUpdatedEvent(
                aggregateIdValue = id,
                legalName = update.legalName,
                logoUrl = update.logoUrl,
                address = update.address,
            )
        )
    }
}
