@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.bamboo.assur.partnerinsurers.registry.domain.entities

import com.bamboo.assur.partnerinsurers.registry.domain.enums.AgreementStatus
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerContactAddedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerCreatedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerDeletedEvent
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
 * ## Business Rules:
 *
 * ### Creation Rules:
 * - Partner insurer code must be unique and not blank
 * - Legal name must not be blank
 * - Tax identification number must be valid and unique
 * - Initial status defaults to ONBOARDING
 * - At least one contact is required during creation
 *
 * ### Status Transition Rules:
 * - ONBOARDING → ACTIVE: Partner can be activated after setup completion
 * - ACTIVE → SUSPENDED: Active partners can be suspended for violations
 * - ACTIVE → MAINTENANCE: Active partners can be put in maintenance mode
 * - ACTIVE → DEACTIVATED: Active partners can be deactivated
 * - SUSPENDED → ACTIVE: Suspended partners can be reactivated
 * - MAINTENANCE → ACTIVE: Partners in maintenance can be reactivated
 * - ONBOARDING → DEACTIVATED: Onboarding partners can be deactivated
 * - Invalid transitions throw InvalidOperationException
 *
 * ### Update Rules:
 * - At least one field must be updated when calling update()
 * - Legal name cannot be blank if provided
 * - Updates trigger PartnerInsurerUpdatedEvent
 *
 * ### Deletion Rules:
 * - Cannot delete partner with active agreements
 * - Soft deletion is used (sets deleted_at timestamp)
 * - Status is set to DEACTIVATED upon deletion
 * - Already deleted partners cannot be deleted again
 *
 * ### Contact Management Rules:
 * - A contact can be added to a partner insurer if the partner insurer is not deleted and not deactivated
 * - A contact can be added if the contact's email is not already associated with the partner insurer
 * - Maximum 10 contacts per partner insurer
 * - Contact must have valid email format
 * - Contact must have valid phone format
 * - Contact role must not be blank
 * - Full name must not be blank
 * - Contact associations are maintained at database level
 *
 * ### Agreement Rules:
 * - Partner cannot be deleted if it has active agreements
 * - Agreement status checking is performed before deletion
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
    val brokerPartnerInsurerAgreements: MutableSet<BrokerPartnerInsurerAgreement>, // TODO: To remove
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
         * Maximum number of contacts allowed per partner insurer.
         * This prevents unbounded growth and ensures manageable contact lists.
         */
        const val MAX_CONTACTS_PER_PARTNER = 10

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
     * Throws an [InvalidOperationException] if the partner is already in maintenance.
     *
     * @param reason The reason for putting the partner on maintenance.
     */
    fun putInMaintenance(reason: String) {
        changeStatusTo(PartnerInsurerStatus.MAINTENANCE, reason) { status == PartnerInsurerStatus.MAINTENANCE }
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

    /**
     * Adds a new contact to the partner insurer.
     *
     * This method enforces business rules for contact management and ensures
     * that contacts are added only under valid business conditions.
     *
     * Business Rules Applied:
     * - Partner insurer must not be deleted
     * - Partner insurer must be in a valid status (not DEACTIVATED)
     * - Contact cannot be null
     * - Contact must have valid email format
     * - Contact must have valid phone format
     * - Contact role must not be blank
     * - Full name must not be blank
     * - Cannot add duplicate contacts (same email)
     * - Maximum 10 contacts per partner insurer
     * - Contact email must be unique within the partner insurer
     *
     * @param contact The contact to add to this partner insurer
     * @throws InvalidOperationException if business rules are violated
     * @throws IllegalArgumentException if contact data is invalid
     */
    fun addContact(contact: Contact) {
        // Rule: Partner insurer must not be deleted
        if (isDeleted()) {
            throw InvalidOperationException("Cannot add contact to deleted partner insurer")
        }

        // Rule: Partner insurer must be in a valid status
        if (status == PartnerInsurerStatus.DEACTIVATED) {
            throw InvalidOperationException("Cannot add contact to deactivated partner insurer")
        }

        // Rule: Contact cannot be null (handled by type system, but explicit check for clarity)
        requireNotNull(contact) { "Contact cannot be null" }

        // Rule: Contact must have valid data (delegated to Contact entity validation)
        require(contact.fullName.isNotBlank()) { "Contact full name cannot be blank" }
        require(contact.contactRole.isNotBlank()) { "Contact role cannot be blank" }

        // Rule: Cannot exceed maximum contacts per partner
        if (contacts.size >= MAX_CONTACTS_PER_PARTNER) {
            throw InvalidOperationException(
                "Cannot add more than $MAX_CONTACTS_PER_PARTNER contacts to partner insurer. " +
                        "Current count: ${contacts.size}"
            )
        }

        // Rule: Contact email must be unique within the partner insurer
        val existingContact = contacts.find { it.email.value.equals(contact.email.value, ignoreCase = true) }
        if (existingContact != null) {
            throw InvalidOperationException(
                "Contact with email '${contact.email.value}' already exists for this partner insurer. " +
                        "Contact ID: ${existingContact.id}"
            )
        }

        // Add the contact and update entity state
        contacts.add(contact)
        touch()

        // Generate domain event for contact addition
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
        legalName: String?,
        logoUrl: Url?,
        address: Address?,
    ) {
        require(legalName != null || logoUrl != null || address != null) { "At least one field must be updated" }
        legalName?.let { require(it.isNotBlank()) { "Legal name cannot be blank" } }

        // Update mutable properties
        legalName?.let { this.legalName = it }
        logoUrl?.let { this.logoUrl = it }
        address?.let { this.address = it }

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
     * Soft deletes the partner insurer using the shared kernel soft deletion mechanism.
     *
     * This method performs a logical deletion which ensures data traceability
     * and preserves historical records. It validates that no active contracts
     * are still associated with the partner.
     *
     * @param reason An optional reason for the deletion.
     * @param deletedBy The ID of the user performing the deletion (optional for now).
     * @throws InvalidOperationException if the partner is already deleted or has active contracts.
     */
    fun delete(reason: String?, deletedBy: DomainEntityId? = null) {
        // Check if already deleted using shared kernel method
        if (isDeleted()) {
            throw InvalidOperationException("Partner insurer is already deleted.")
        }

        // Check for active agreements - prevent deletion if any active contracts exist
        val activeAgreements = brokerPartnerInsurerAgreements.filter {
            it.status == AgreementStatus.ACTIVE
        }

        if (activeAgreements.isNotEmpty()) {
            throw InvalidOperationException(
                "Cannot delete partner insurer with active contracts. " +
                        "Found ${activeAgreements.size} active agreement(s)."
            )
        }

        // Use shared kernel soft deletion
        if (deletedBy != null) {
            softDelete(deletedBy)
        } else {
            // For now, we don't have user context, so we'll need to update this when auth is implemented
            // TODO: Pass actual user ID when authentication context is available
            softDelete(DomainEntityId.random()) // Temporary placeholder
        }

        // Set status to DEACTIVATED for domain-specific logic
        status = PartnerInsurerStatus.DEACTIVATED

        addDomainEvent(
            PartnerInsurerDeletedEvent(
                aggregateIdValue = id,
                partnerInsurerCode = partnerInsurerCode,
                status = status.name,
                reason = reason
            )
        )
    }
}
