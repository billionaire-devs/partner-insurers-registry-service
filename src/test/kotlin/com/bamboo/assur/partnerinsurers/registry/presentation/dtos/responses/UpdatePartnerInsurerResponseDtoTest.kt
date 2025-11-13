package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class UpdatePartnerInsurerResponseDtoTest {

    private val partnerId = UUID.randomUUID()
    private val partnerCode = "AXA-GA"
    private val legalName = "AXA Gabon"
    private val taxId = "GA-123456"
    private val logoUrl = "https://example.com/logo.png"
    private val address = Address("Boulevard", "Libreville", "GA", "BP 001")

    private fun createPartnerInsurer(): PartnerInsurer {
        return PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = legalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = Url(logoUrl),
            contacts = emptySet(),
            address = address,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )
    }

    @Test
    fun `should convert PartnerInsurer to UpdatePartnerInsurerResponseDto`() {
        // Given
        val partnerInsurer = createPartnerInsurer()

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertEquals(partnerId.toString(), responseDto.id)
        assertEquals(partnerCode, responseDto.partnerInsurerCode)
        assertEquals(legalName, responseDto.legalName)
        assertEquals(logoUrl, responseDto.logoUrl)
        assertEquals(PartnerInsurerStatus.ACTIVE.name, responseDto.status)
        
        // Address mapping
        assertEquals("Boulevard", responseDto.address.street)
        assertEquals("Libreville", responseDto.address.city)
        assertEquals("GA", responseDto.address.country)
        assertEquals("BP 001", responseDto.address.zipCode)
        
        // Timestamps should be present as strings
        assertNotNull(responseDto.createdAt)
        assertNotNull(responseDto.updatedAt)
    }

    @Test
    fun `should handle null logo URL`() {
        // Given
        val partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = legalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = null,
            contacts = emptySet(),
            address = address,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertNull(responseDto.logoUrl)
        assertEquals(legalName, responseDto.legalName)
        assertEquals(partnerCode, responseDto.partnerInsurerCode)
    }

    @Test
    fun `should handle null zip code in address`() {
        // Given
        val addressWithoutZip = Address("Boulevard", "Libreville", "GA", null)
        val partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = legalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = Url(logoUrl),
            contacts = emptySet(),
            address = addressWithoutZip,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertEquals("Boulevard", responseDto.address.street)
        assertEquals("Libreville", responseDto.address.city)
        assertEquals("GA", responseDto.address.country)
        assertNull(responseDto.address.zipCode)
    }

    @Test
    fun `should handle different partner statuses`() {
        // Given
        val statuses = listOf(
            PartnerInsurerStatus.ONBOARDING,
            PartnerInsurerStatus.ACTIVE,
            PartnerInsurerStatus.SUSPENDED,
            PartnerInsurerStatus.DEACTIVATED,
            PartnerInsurerStatus.MAINTENANCE
        )

        statuses.forEach { status ->
            val partnerInsurer = PartnerInsurer.reconstitute(
                id = DomainEntityId(partnerId),
                partnerInsurerCode = partnerCode,
                legalName = legalName,
                taxIdentificationNumber = TaxIdentificationNumber(taxId),
                logoUrl = Url(logoUrl),
                contacts = emptySet(),
                address = address,
                status = status,
                brokerPartnerInsurerAgreements = emptySet(),
            )

            // When
            val responseDto = partnerInsurer.toResponseDto()

            // Then
            assertEquals(status.name, responseDto.status)
        }
    }

    @Test
    fun `should preserve UUID format in id field`() {
        // Given
        val specificUuid = UUID.fromString("12345678-1234-1234-1234-123456789012")
        val partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(specificUuid),
            partnerInsurerCode = partnerCode,
            legalName = legalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = Url(logoUrl),
            contacts = emptySet(),
            address = address,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertEquals("12345678-1234-1234-1234-123456789012", responseDto.id)
        assertEquals(specificUuid.toString(), responseDto.id)
    }

    @Test
    fun `should handle long legal names`() {
        // Given
        val longLegalName = "A".repeat(255) // Very long legal name
        val partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = longLegalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = Url(logoUrl),
            contacts = emptySet(),
            address = address,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertEquals(longLegalName, responseDto.legalName)
        assertEquals(255, responseDto.legalName.length)
    }

    @Test
    fun `should handle special characters in fields`() {
        // Given
        val specialLegalName = "AXA Gabon S.A. - Société d'Assurance & Réassurance"
        val specialAddress = Address(
            "Rue de l'Indépendance N°123, 2ème étage",
            "Libreville",
            "GA",
            "BP 001/002"
        )
        val partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = specialLegalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = Url(logoUrl),
            contacts = emptySet(),
            address = specialAddress,
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertEquals(specialLegalName, responseDto.legalName)
        assertEquals("Rue de l'Indépendance N°123, 2ème étage", responseDto.address.street)
        assertEquals("BP 001/002", responseDto.address.zipCode)
    }

    @Test
    fun `should create immutable response DTO`() {
        // Given
        val partnerInsurer = createPartnerInsurer()

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then - All fields should be accessible and immutable (data class properties)
        assertNotNull(responseDto.id)
        assertNotNull(responseDto.partnerInsurerCode)
        assertNotNull(responseDto.legalName)
        assertNotNull(responseDto.status)
        assertNotNull(responseDto.address)
        assertNotNull(responseDto.createdAt)
        assertNotNull(responseDto.updatedAt)

        // Logo URL can be null
        responseDto.logoUrl // Should not throw
    }

    @Test
    fun `should handle minimal valid string fields appropriately`() {
        // Given - Use minimal valid strings instead of empty strings which may be rejected
        val partnerInsurer = PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = legalName,
            taxIdentificationNumber = TaxIdentificationNumber(taxId),
            logoUrl = null, // Avoid empty string URL which throws IllegalArgumentException
            contacts = emptySet(),
            address = Address("S", "C", "GA", ""), // Minimal valid strings (zipCode can be empty)
            status = PartnerInsurerStatus.ACTIVE,
            brokerPartnerInsurerAgreements = emptySet(),
        )

        // When
        val responseDto = partnerInsurer.toResponseDto()

        // Then
        assertEquals("S", responseDto.address.street)
        assertEquals("C", responseDto.address.city)
        assertEquals("GA", responseDto.address.country)
        assertEquals("", responseDto.address.zipCode)
        assertNull(responseDto.logoUrl)
    }
}