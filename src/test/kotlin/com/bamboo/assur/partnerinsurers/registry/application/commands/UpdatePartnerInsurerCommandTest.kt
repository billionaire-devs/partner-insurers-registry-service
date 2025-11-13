package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class UpdatePartnerInsurerCommandTest {

    private val partnerId = UUID.randomUUID()

    @Test
    fun `should return true when legal name is provided`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "AXA Gabon",
            logoUrl = null,
            address = null
        )

        // When & Then
        assertTrue(command.hasChanges())
    }

    @Test
    fun `should return true when logo URL is provided`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = null,
            logoUrl = "https://example.com/logo.png",
            address = null
        )

        // When & Then
        assertTrue(command.hasChanges())
    }

    @Test
    fun `should return true when address is provided`() {
        // Given
        val address = Address("Street", "City", "Country", "12345")
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = null,
            logoUrl = null,
            address = address
        )

        // When & Then
        assertTrue(command.hasChanges())
    }

    @Test
    fun `should return true when multiple fields are provided`() {
        // Given
        val address = Address("Street", "City", "Country", "12345")
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "New Name",
            logoUrl = "https://example.com/logo.png",
            address = address
        )

        // When & Then
        assertTrue(command.hasChanges())
    }

    @Test
    fun `should return false when all fields are null`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = null,
            logoUrl = null,
            address = null
        )

        // When & Then
        assertFalse(command.hasChanges())
    }

    @Test
    fun `should return true for empty string legal name`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "",
            logoUrl = null,
            address = null
        )

        // When & Then
        assertTrue(command.hasChanges()) // Empty string is still a change
    }

    @Test
    fun `should return true for empty string logo URL`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = null,
            logoUrl = "",
            address = null
        )

        // When & Then
        assertTrue(command.hasChanges()) // Empty string is still a change
    }

    @Test
    fun `should preserve all command properties`() {
        // Given
        val legalName = "Test Legal Name"
        val logoUrl = "https://test.com/logo.png"
        val address = Address("Test Street", "Test City", "TC", "12345")
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = legalName,
            logoUrl = logoUrl,
            address = address
        )

        // When & Then
        assertEquals(partnerId, command.id)
        assertEquals(legalName, command.legalName)
        assertEquals(logoUrl, command.logoUrl)
        assertEquals(address, command.address)
    }

    @Test
    fun `should handle null values correctly`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = null,
            logoUrl = null,
            address = null
        )

        // When & Then
        assertEquals(partnerId, command.id)
        assertNull(command.legalName)
        assertNull(command.logoUrl)
        assertNull(command.address)
        assertFalse(command.hasChanges())
    }

    @Test
    fun `should handle address with null zipCode`() {
        // Given
        val address = Address("Street", "City", "Country", null)
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = null,
            logoUrl = null,
            address = address
        )

        // When & Then
        assertTrue(command.hasChanges())
        assertEquals(address, command.address)
        assertNull(command.address?.zipCode)
    }

    @Test
    fun `should be a data class with proper equality`() {
        // Given
        val address = Address("Street", "City", "Country", "12345")
        val command1 = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "Test Name",
            logoUrl = "https://test.com",
            address = address
        )
        val command2 = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "Test Name",
            logoUrl = "https://test.com",
            address = address
        )

        // When & Then
        assertEquals(command1, command2)
        assertEquals(command1.hashCode(), command2.hashCode())
    }

    @Test
    fun `should have different equality for different commands`() {
        // Given
        val command1 = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "Name 1",
            logoUrl = null,
            address = null
        )
        val command2 = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "Name 2",
            logoUrl = null,
            address = null
        )

        // When & Then
        assertNotEquals(command1, command2)
        assertNotEquals(command1.hashCode(), command2.hashCode())
    }

    @Test
    fun `should implement Command interface`() {
        // Given
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = "Test",
            logoUrl = null,
            address = null
        )

        // When & Then
        assertTrue(command is com.bamboo.assur.partnerinsurers.sharedkernel.application.Command)
    }

    @Test
    fun `should handle very long strings`() {
        // Given
        val longName = "A".repeat(1000)
        val longUrl = "https://example.com/" + "a".repeat(1000) + ".png"
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = longName,
            logoUrl = longUrl,
            address = null
        )

        // When & Then
        assertTrue(command.hasChanges())
        assertEquals(longName, command.legalName)
        assertEquals(longUrl, command.logoUrl)
    }

    @Test
    fun `should handle special characters in strings`() {
        // Given
        val specialName = "Société d'Assurance & Réassurance «AXA» - Gabon"
        val specialUrl = "https://example.com/logos/société-axa.png?param=value&other=123"
        val command = UpdatePartnerInsurerCommand(
            id = partnerId,
            legalName = specialName,
            logoUrl = specialUrl,
            address = null
        )

        // When & Then
        assertTrue(command.hasChanges())
        assertEquals(specialName, command.legalName)
        assertEquals(specialUrl, command.logoUrl)
    }
}