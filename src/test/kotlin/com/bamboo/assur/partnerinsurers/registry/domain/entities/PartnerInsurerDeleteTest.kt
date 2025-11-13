package com.bamboo.assur.partnerinsurers.registry.domain.entities

import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerDeletedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.InvalidOperationException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PartnerInsurerDeleteTest {

    private lateinit var partnerInsurer: PartnerInsurer
    private val partnerId = DomainEntityId(UUID.randomUUID())
    private val partnerCode = "TEST-PARTNER"
    private val reason = "Partnership terminated"

    @BeforeEach
    fun setUp() {
        partnerInsurer = PartnerInsurer.reconstitute(
            id = partnerId,
            partnerInsurerCode = partnerCode,
            legalName = "Test Partner",
            taxIdentificationNumber = TaxIdentificationNumber("TEST123"),
            logoUrl = null,
            contacts = emptySet(),
            address = Address("Street", "City", "Country", "ZIP"),
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet()
        )
    }

    @Nested
    inner class SuccessfulDeletion {

        @Test
        fun `should soft delete partner insurer with reason`() {
            // When
            partnerInsurer.delete(reason, null)

            // Then
            assertEquals(PartnerInsurerStatus.DEACTIVATED, partnerInsurer.status)
            assertTrue(partnerInsurer.hasPendingEvents())

            val events = partnerInsurer.getDomainEvents()
            assertEquals(1, events.size)

            val event = events.first() as PartnerInsurerDeletedEvent
            assertEquals(partnerId, event.aggregateIdValue)
            assertEquals(partnerCode, event.partnerInsurerCode)
            assertEquals(reason, event.reason)
        }

        @Test
        fun `should soft delete partner insurer without reason`() {
            // When
            partnerInsurer.delete(null, null)

            // Then
            assertEquals(PartnerInsurerStatus.DEACTIVATED, partnerInsurer.status)

            val event = partnerInsurer.getDomainEvents().first() as PartnerInsurerDeletedEvent
            assertNull(event.reason)
        }

        @Test
        fun `should delete partner in ONBOARDING status`() {
            // Given
            val onboardingPartner = PartnerInsurer.reconstitute(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "Test Partner",
                taxIdentificationNumber = TaxIdentificationNumber("TEST123"),
                logoUrl = null,
                contacts = emptySet(),
                address = Address("Street", "City", "Country", "ZIP"),
                status = PartnerInsurerStatus.ONBOARDING,
                brokerPartnerInsurerAgreements = emptySet()
            )

            // When & Then
            assertDoesNotThrow {
                onboardingPartner.delete(reason, null)
            }
            assertEquals(PartnerInsurerStatus.DEACTIVATED, onboardingPartner.status)
        }

        @Test
        fun `should delete partner in SUSPENDED status`() {
            // Given
            val suspendedPartner = PartnerInsurer.reconstitute(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "Test Partner",
                taxIdentificationNumber = TaxIdentificationNumber("TEST123"),
                logoUrl = null,
                contacts = emptySet(),
                address = Address("Street", "City", "Country", "ZIP"),
                status = PartnerInsurerStatus.SUSPENDED,
                brokerPartnerInsurerAgreements = emptySet()
            )

            // When & Then
            assertDoesNotThrow {
                suspendedPartner.delete(reason, null)
            }
            assertEquals(PartnerInsurerStatus.DEACTIVATED, suspendedPartner.status)
        }
    }

    @Nested
    inner class ValidationErrors {

        @Test
        fun `should throw exception when partner already deleted`() {
            // Given - create a partner and delete it first
            val partnerToDelete = PartnerInsurer.reconstitute(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "Test Partner",
                taxIdentificationNumber = TaxIdentificationNumber("TEST123"),
                logoUrl = null,
                contacts = emptySet(),
                address = Address("Street", "City", "Country", "ZIP"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet()
            )

            // Delete it first to make it actually deleted
            partnerToDelete.delete(reason, null)

            // When & Then - try to delete it again
            val exception = assertThrows<InvalidOperationException> {
                partnerToDelete.delete(reason, null)
            }
            assertEquals("Partner insurer is already deleted.", exception.message)
        }

        // TODO: Add test for active agreements validation once we have proper test data setup
    }

    @Nested
    inner class EntityStateValidation {

        @Test
        fun `should update updatedAt timestamp when deleted`() {
            // Given
            val originalUpdatedAt = partnerInsurer.updatedAt

            // When
            partnerInsurer.delete(reason, null)

            // Then
            assertTrue(partnerInsurer.updatedAt > originalUpdatedAt)
        }

        @Test
        fun `should not generate events when already deleted`() {
            // Given - create a partner and delete it first
            val partnerToDelete = PartnerInsurer.reconstitute(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "Test Partner",
                taxIdentificationNumber = TaxIdentificationNumber("TEST123"),
                logoUrl = null,
                contacts = emptySet(),
                address = Address("Street", "City", "Country", "ZIP"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet()
            )

            // Delete it first to make it actually deleted
            partnerToDelete.delete(reason, null)
            // Clear events from the first deletion
            partnerToDelete.clearDomainEvents()

            // When & Then - try to delete it again
            assertThrows<InvalidOperationException> {
                partnerToDelete.delete(reason, null)
            }

            // Should not have generated any new events
            assertFalse(partnerToDelete.hasPendingEvents())
        }
    }
}