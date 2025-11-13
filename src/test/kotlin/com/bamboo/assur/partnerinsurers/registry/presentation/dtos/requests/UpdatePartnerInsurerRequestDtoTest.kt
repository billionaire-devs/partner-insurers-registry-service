package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class UpdatePartnerInsurerRequestDtoTest {

    private val partnerId = UUID.randomUUID()

    @Test
    fun `should map to command with all fields`() {
        // Given
        val addressDto = AddressDto(
            zipCode = "12345",
            street = "Test Street",
            city = "Test City",
            country = "TC"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "Test Legal Name",
            logoUrl = "https://test.com/logo.png",
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals(partnerId, command.id)
        assertEquals("Test Legal Name", command.legalName)
        assertEquals("https://test.com/logo.png", command.logoUrl)
        assertNotNull(command.address)
        assertEquals("Test Street", command.address!!.street)
        assertEquals("Test City", command.address!!.city)
        assertEquals("TC", command.address!!.country)
        assertEquals("12345", command.address!!.zipCode)
    }

    @Test
    fun `should map to command with null fields`() {
        // Given
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = null,
            logoUrl = null,
            address = null
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals(partnerId, command.id)
        assertNull(command.legalName)
        assertNull(command.logoUrl)
        assertNull(command.address)
    }

    @Test
    fun `should map to command with legal name only`() {
        // Given
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "Only Legal Name",
            logoUrl = null,
            address = null
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals(partnerId, command.id)
        assertEquals("Only Legal Name", command.legalName)
        assertNull(command.logoUrl)
        assertNull(command.address)
    }

    @Test
    fun `should map to command with logo URL only`() {
        // Given
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = null,
            logoUrl = "https://only-logo.com/logo.png",
            address = null
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals(partnerId, command.id)
        assertNull(command.legalName)
        assertEquals("https://only-logo.com/logo.png", command.logoUrl)
        assertNull(command.address)
    }

    @Test
    fun `should map to command with address only`() {
        // Given
        val addressDto = AddressDto(
            zipCode = "54321",
            street = "Only Address Street",
            city = "Only Address City",
            country = "OA"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = null,
            logoUrl = null,
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals(partnerId, command.id)
        assertNull(command.legalName)
        assertNull(command.logoUrl)
        assertNotNull(command.address)
        assertEquals("Only Address Street", command.address!!.street)
        assertEquals("Only Address City", command.address!!.city)
        assertEquals("OA", command.address!!.country)
        assertEquals("54321", command.address!!.zipCode)
    }

    @Test
    fun `should handle address without zip code`() {
        // Given
        val addressDto = AddressDto(
            zipCode = null,
            street = "No Zip Street",
            city = "No Zip City",
            country = "NZ"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = null,
            logoUrl = null,
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertNotNull(command.address)
        assertEquals("No Zip Street", command.address!!.street)
        assertEquals("No Zip City", command.address!!.city)
        assertEquals("NZ", command.address!!.country)
        assertNull(command.address!!.zipCode)
    }

    @Test
    fun `should handle minimal valid strings`() {
        // Given - Use minimal valid strings instead of empty strings which are rejected by validation
        val addressDto = AddressDto(
            zipCode = "", // zipCode can be empty
            street = "S", // Minimal non-blank street
            city = "C", // Minimal non-blank city
            country = "ES"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "L", // Minimal non-blank legal name
            logoUrl = null, // Empty string logoUrl would throw IllegalArgumentException from Url validation
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals("L", command.legalName)
        assertNull(command.logoUrl) // Since we passed null
        assertNotNull(command.address)
        assertEquals("S", command.address!!.street)
        assertEquals("C", command.address!!.city)
        assertEquals("ES", command.address!!.country)
        assertEquals("", command.address!!.zipCode)
    }

    @Test
    fun `should handle special characters`() {
        // Given
        val addressDto = AddressDto(
            zipCode = "BP 001/002",
            street = "Rue de l'Indépendance N°123",
            city = "Libreville",
            country = "GA"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "Société d'Assurance & Réassurance",
            logoUrl = "https://example.com/société-logo.png?param=value",
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals("Société d'Assurance & Réassurance", command.legalName)
        assertEquals("https://example.com/société-logo.png?param=value", command.logoUrl)
        assertEquals("Rue de l'Indépendance N°123", command.address!!.street)
        assertEquals("BP 001/002", command.address!!.zipCode)
    }

    @Test
    fun `should preserve partner ID during mapping`() {
        // Given
        val specificId = UUID.fromString("12345678-1234-1234-1234-123456789012")
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "Test Name",
            logoUrl = null,
            address = null
        )

        // When
        val command = requestDto.toCommand(specificId)

        // Then
        assertEquals(specificId, command.id)
        assertEquals("12345678-1234-1234-1234-123456789012", command.id.toString())
    }

    @Test
    fun `should create immutable DTO`() {
        // Given
        val addressDto = AddressDto(
            zipCode = "12345",
            street = "Street",
            city = "City",
            country = "Country"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "Test Name",
            logoUrl = "https://test.com/logo.png",
            address = addressDto
        )

        // When & Then - Should be able to access all properties
        assertEquals("Test Name", requestDto.legalName)
        assertEquals("https://test.com/logo.png", requestDto.logoUrl)
        assertEquals(addressDto, requestDto.address)
    }

    @Test
    fun `should handle very long strings`() {
        // Given
        val longName = "A".repeat(1000)
        val longUrl = "https://example.com/" + "a".repeat(500) + ".png"
        val longStreet = "B".repeat(500)
        val addressDto = AddressDto(
            zipCode = "12345",
            street = longStreet, 
            city = "City", 
            country = "Country"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = longName,
            logoUrl = longUrl,
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals(longName, command.legalName)
        assertEquals(longUrl, command.logoUrl)
        assertEquals(longStreet, command.address!!.street)
    }

    @Test
    fun `should be equal for same content`() {
        // Given
        val addressDto = AddressDto(
            zipCode = "12345",
            street = "Street",
            city = "City",
            country = "Country"
        )
        val dto1 = UpdatePartnerInsurerRequestDto(
            legalName = "Same Name",
            logoUrl = "https://same.com/logo.png",
            address = addressDto
        )
        val dto2 = UpdatePartnerInsurerRequestDto(
            legalName = "Same Name",
            logoUrl = "https://same.com/logo.png",
            address = addressDto
        )

        // When & Then
        assertEquals(dto1, dto2)
        assertEquals(dto1.hashCode(), dto2.hashCode())
    }

    @Test
    fun `should not be equal for different content`() {
        // Given
        val dto1 = UpdatePartnerInsurerRequestDto(
            legalName = "Name 1",
            logoUrl = null,
            address = null
        )
        val dto2 = UpdatePartnerInsurerRequestDto(
            legalName = "Name 2",
            logoUrl = null,
            address = null
        )

        // When & Then
        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `should handle mixed null and non-null values`() {
        // Given
        val addressDto = AddressDto(
            zipCode = null,
            street = "Mixed Street", 
            city = "Mixed City", 
            country = "MC"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = "Mixed Legal Name",
            logoUrl = null,
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)

        // Then
        assertEquals("Mixed Legal Name", command.legalName)
        assertNull(command.logoUrl)
        assertNotNull(command.address)
        assertEquals("Mixed Street", command.address!!.street)
        assertEquals("Mixed City", command.address!!.city)
        assertEquals("MC", command.address!!.country)
        assertNull(command.address!!.zipCode)
    }

    @Test
    fun `should correctly map address DTO to domain Address`() {
        // Given
        val addressDto = AddressDto(
            zipCode = "DOMAIN123",
            street = "Domain Test Street",
            city = "Domain Test City",
            country = "DT"
        )
        val requestDto = UpdatePartnerInsurerRequestDto(
            legalName = null,
            logoUrl = null,
            address = addressDto
        )

        // When
        val command = requestDto.toCommand(partnerId)
        val domainAddress = command.address!!

        // Then - Verify the address is properly converted to domain object
        assertEquals("Domain Test Street", domainAddress.street)
        assertEquals("Domain Test City", domainAddress.city)
        assertEquals("DT", domainAddress.country)
        assertEquals("DOMAIN123", domainAddress.zipCode)
        
        // Verify it's a proper domain Address object
        assertTrue(domainAddress is com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address)
    }
}