@file:OptIn(ExperimentalTime::class)

package com.bamboo.assur.partnerinsurers.registry.domain.entities

import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerUpdatedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.time.ExperimentalTime

class PartnerInsurerUpdateTest {

    private lateinit var partnerInsurer: PartnerInsurer
    private val partnerId = UUID.randomUUID()
    private val partnerCode = "AXA-GA"
    private val originalLegalName = "AXA Gabon"
    private val taxId = "GA-123456"
    private val originalLogoUrl = Url("https://old-logo.com/logo.png")
    private val originalAddress = Address("Original Street", "Libreville", "GA", "BP 001")

    @BeforeEach
    fun setUp() {
        partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = originalLegalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = originalLogoUrl,
            contacts = emptySet(),
            address = originalAddress,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )
    }

    @Nested
    inner class SuccessfulUpdates {

        @Test
        fun `should update legal name only`() {
            // Given
            val newLegalName = "AXA Gabon SA"

            // When
            partnerInsurer.update(
                legalName = newLegalName,
                logoUrl = null,
                address = null
            )

            // Then
            assertEquals(newLegalName, partnerInsurer.legalName)
            assertEquals(originalLogoUrl, partnerInsurer.logoUrl)
            assertEquals(originalAddress, partnerInsurer.address)
            
            // Should have generated a domain event
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            assertEquals(1, events.size)
            
            val event = events.first() as PartnerInsurerUpdatedEvent
            assertEquals(DomainEntityId(partnerId), event.aggregateIdValue)
            assertEquals(newLegalName, event.legalName)
            assertNull(event.logoUrl)
            assertNull(event.address)
        }

        @Test
        fun `should update logo URL only`() {
            // Given
            val newLogoUrl = Url("https://new-logo.com/logo.png")

            // When
            partnerInsurer.update(
                legalName = null,
                logoUrl = newLogoUrl,
                address = null
            )

            // Then
            assertEquals(originalLegalName, partnerInsurer.legalName)
            assertEquals(newLogoUrl, partnerInsurer.logoUrl)
            assertEquals(originalAddress, partnerInsurer.address)
            
            // Should have generated a domain event
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            val event = events.first() as PartnerInsurerUpdatedEvent
            assertNull(event.legalName)
            assertEquals(newLogoUrl, event.logoUrl)
            assertNull(event.address)
        }

        @Test
        fun `should update address only`() {
            // Given
            val newAddress = Address("New Street", "Port-Gentil", "GA", "BP 002")

            // When
            partnerInsurer.update(
                legalName = null,
                logoUrl = null,
                address = newAddress
            )

            // Then
            assertEquals(originalLegalName, partnerInsurer.legalName)
            assertEquals(originalLogoUrl, partnerInsurer.logoUrl)
            assertEquals(newAddress, partnerInsurer.address)
            
            // Should have generated a domain event
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            val event = events.first() as PartnerInsurerUpdatedEvent
            assertNull(event.legalName)
            assertNull(event.logoUrl)
            assertEquals(newAddress, event.address)
        }

        @Test
        fun `should update all fields together`() {
            // Given
            val newLegalName = "AXA Gabon SA"
            val newLogoUrl = Url("https://new-logo.com/logo.png")
            val newAddress = Address("New Street", "Port-Gentil", "GA", "BP 002")

            // When
            partnerInsurer.update(
                legalName = newLegalName,
                logoUrl = newLogoUrl,
                address = newAddress
            )

            // Then
            assertEquals(newLegalName, partnerInsurer.legalName)
            assertEquals(newLogoUrl, partnerInsurer.logoUrl)
            assertEquals(newAddress, partnerInsurer.address)
            
            // Should have generated a domain event with all changes
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            val event = events.first() as PartnerInsurerUpdatedEvent
            assertEquals(newLegalName, event.legalName)
            assertEquals(newLogoUrl, event.logoUrl)
            assertEquals(newAddress, event.address)
        }

        @Test
        fun `should not update logo URL when null is passed`() {
            // Given - Partner has a logo URL initially
            assertTrue(partnerInsurer.logoUrl != null)

            // When - Pass null for logoUrl (means don't update)
            partnerInsurer.update(
                legalName = "Updated Name",
                logoUrl = null, // null means don't update this field
                address = null
            )

            // Then - Logo URL should remain unchanged (null means don't update)
            assertEquals(originalLogoUrl, partnerInsurer.logoUrl)
        }

        @Test
        fun `should update timestamps when fields are updated`() {
            // Given
            val originalUpdatedAt = partnerInsurer.updatedAt

            // When
            Thread.sleep(10) // Ensure time difference
            partnerInsurer.update(
                legalName = "Updated Name",
                logoUrl = null,
                address = null
            )

            // Then - Updated timestamp should be after original
            assertTrue(partnerInsurer.updatedAt > originalUpdatedAt)
        }

        @Test
        fun `should handle address with null zipCode`() {
            // Given
            val addressWithoutZip = Address("Street", "City", "GA", null)

            // When
            partnerInsurer.update(
                legalName = null,
                logoUrl = null,
                address = addressWithoutZip
            )

            // Then
            assertEquals(addressWithoutZip, partnerInsurer.address)
            assertNull(partnerInsurer.address.zipCode)
        }
    }

    @Nested
    inner class ValidationRules {

        @Test
        fun `should reject update with all null parameters`() {
            // When & Then
            assertThrows<IllegalArgumentException> {
                partnerInsurer.update(
                    legalName = null,
                    logoUrl = null,
                    address = null
                )
            }
            
            // No events should be generated
            assertFalse(partnerInsurer.hasPendingEvents())
        }

        @Test
        fun `should reject blank legal name`() {
            // When & Then
            assertThrows<IllegalArgumentException> {
                partnerInsurer.update(
                    legalName = "",
                    logoUrl = null,
                    address = null
                )
            }
            
            // Original values should be preserved
            assertEquals(originalLegalName, partnerInsurer.legalName)
            assertFalse(partnerInsurer.hasPendingEvents())
        }

        @Test
        fun `should reject whitespace-only legal name`() {
            // When & Then
            assertThrows<IllegalArgumentException> {
                partnerInsurer.update(
                    legalName = "   ",
                    logoUrl = null,
                    address = null
                )
            }
            
            // Original values should be preserved
            assertEquals(originalLegalName, partnerInsurer.legalName)
            assertFalse(partnerInsurer.hasPendingEvents())
        }

        @Test
        fun `should accept valid legal name with special characters`() {
            // Given
            val specialName = "AXA Gabon S.A. - Société d'Assurance & Réassurance"

            // When
            partnerInsurer.update(
                legalName = specialName,
                logoUrl = null,
                address = null
            )

            // Then
            assertEquals(specialName, partnerInsurer.legalName)
            assertTrue(partnerInsurer.hasPendingEvents())
        }

        @Test
        fun `should accept legal name with numbers`() {
            // Given
            val nameWithNumbers = "AXA Gabon 2025"

            // When
            partnerInsurer.update(
                legalName = nameWithNumbers,
                logoUrl = null,
                address = null
            )

            // Then
            assertEquals(nameWithNumbers, partnerInsurer.legalName)
            assertTrue(partnerInsurer.hasPendingEvents())
        }

        @Test
        fun `should accept very long legal name`() {
            // Given
            val longName = "A".repeat(255)

            // When
            partnerInsurer.update(
                legalName = longName,
                logoUrl = null,
                address = null
            )

            // Then
            assertEquals(longName, partnerInsurer.legalName)
            assertTrue(partnerInsurer.hasPendingEvents())
        }
    }

    @Nested
    inner class DomainEvents {

        @Test
        fun `should generate single event for single field update`() {
            // When
            partnerInsurer.update(
                legalName = "New Name",
                logoUrl = null,
                address = null
            )

            // Then
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            assertEquals(1, events.size)
            assertEquals(PartnerInsurerUpdatedEvent::class, events.first()::class)
        }

        @Test
        fun `should generate single event for multiple field update`() {
            // When
            partnerInsurer.update(
                legalName = "New Name",
                logoUrl = Url("https://new.com/logo.png"),
                address = Address("New St", "New City", "GA", null)
            )

            // Then
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            assertEquals(1, events.size)
            
            val event = events.first() as PartnerInsurerUpdatedEvent
            assertEquals("New Name", event.legalName)
            assertEquals(Url("https://new.com/logo.png"), event.logoUrl)
            assertNotNull(event.address)
        }

        @Test
        fun `should clear events after retrieval`() {
            // Given
            partnerInsurer.update(
                legalName = "New Name",
                logoUrl = null,
                address = null
            )
            assertTrue(partnerInsurer.hasPendingEvents())

            // When
            partnerInsurer.getDomainEvents()
            partnerInsurer.clearDomainEvents()

            // Then
            assertFalse(partnerInsurer.hasPendingEvents())
        }

        @Test
        fun `should accumulate events from multiple updates`() {
            // When
            partnerInsurer.update(
                legalName = "First Update",
                logoUrl = null,
                address = null
            )
            partnerInsurer.update(
                legalName = null,
                logoUrl = Url("https://second-update.com/logo.png"),
                address = null
            )

            // Then
            assertTrue(partnerInsurer.hasPendingEvents())
            val events = partnerInsurer.getDomainEvents()
            assertEquals(2, events.size)
            
            events.forEach { event ->
                assertTrue(event is PartnerInsurerUpdatedEvent)
                assertEquals(DomainEntityId(partnerId), (event as PartnerInsurerUpdatedEvent).aggregateIdValue)
            }
        }
    }

    @Nested
    inner class StateConsistency {

        @Test
        fun `should preserve immutable fields during update`() {
            // Given
            val originalId = partnerInsurer.id
            val originalCode = partnerInsurer.partnerInsurerCode
            val originalTaxId = partnerInsurer.taxIdentificationNumber
            val originalStatus = partnerInsurer.status
            val originalCreatedAt = partnerInsurer.createdAt

            // When
            partnerInsurer.update(
                legalName = "Updated Name",
                logoUrl = Url("https://updated.com/logo.png"),
                address = Address("Updated St", "Updated City", "GA", "Updated ZIP")
            )

            // Then - Immutable fields should remain unchanged
            assertEquals(originalId, partnerInsurer.id)
            assertEquals(originalCode, partnerInsurer.partnerInsurerCode)
            assertEquals(originalTaxId, partnerInsurer.taxIdentificationNumber)
            assertEquals(originalStatus, partnerInsurer.status)
            assertEquals(originalCreatedAt, partnerInsurer.createdAt)
        }

        @Test
        fun `should update only requested fields`() {
            // Given
            val newLegalName = "Only Legal Name Updated"

            // When
            partnerInsurer.update(
                legalName = newLegalName,
                logoUrl = null,
                address = null
            )

            // Then
            assertEquals(newLegalName, partnerInsurer.legalName)
            assertEquals(originalLogoUrl, partnerInsurer.logoUrl) // Unchanged
            assertEquals(originalAddress, partnerInsurer.address) // Unchanged
        }

        @Test
        fun `should maintain entity integrity across multiple updates`() {
            // When - Multiple sequential updates
            partnerInsurer.update(
                legalName = "First Update",
                logoUrl = null,
                address = null
            )
            
            partnerInsurer.update(
                legalName = null,
                logoUrl = Url("https://second-update.com/logo.png"),
                address = null
            )
            
            partnerInsurer.update(
                legalName = null,
                logoUrl = null,
                address = Address("Final St", "Final City", "GA", "Final ZIP")
            )

            // Then - Final state should reflect all updates
            assertEquals("First Update", partnerInsurer.legalName)
            assertEquals(Url("https://second-update.com/logo.png"), partnerInsurer.logoUrl)
            assertEquals(Address("Final St", "Final City", "GA", "Final ZIP"), partnerInsurer.address)
            
            // Should have all events
            assertEquals(3, partnerInsurer.getDomainEvents().size)
        }
    }
}