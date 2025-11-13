package com.bamboo.assur.partnerinsurers.registry.integration

import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.UpdatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerResponseDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class UpdatePartnerInsurerIntegrationTest {

    // Note: This is a placeholder integration test structure
    // Full integration tests would require actual database setup and WebTestClient configuration

    private val partnerId = UUID.randomUUID()

    @Nested
    inner class RequestDtoMapping {

        @Test
        fun `should create valid UpdatePartnerInsurerRequestDto with all fields`() {
            // Given
            val addressDto = AddressDto(
                zipCode = "BP 001",
                street = "Test Street",
                city = "Test City",
                country = "TC"
            )

            // When
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "Test Insurance Company",
                logoUrl = "https://example.com/logo.png",
                address = addressDto
            )

            // Then
            assertEquals("Test Insurance Company", requestDto.legalName)
            assertEquals("https://example.com/logo.png", requestDto.logoUrl)
            assertNotNull(requestDto.address)
            assertEquals("Test Street", requestDto.address!!.street)
            assertEquals("Test City", requestDto.address!!.city)
            assertEquals("TC", requestDto.address!!.country)
            assertEquals("BP 001", requestDto.address!!.zipCode)
        }

        @Test
        fun `should create valid UpdatePartnerInsurerRequestDto with partial fields`() {
            // When
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "Partial Update",
                logoUrl = null,
                address = null
            )

            // Then
            assertEquals("Partial Update", requestDto.legalName)
            assertNull(requestDto.logoUrl)
            assertNull(requestDto.address)
        }

        @Test
        fun `should map to command correctly`() {
            // Given
            val addressDto = AddressDto(
                zipCode = "12345",
                street = "Command Street",
                city = "Command City",
                country = "CC"
            )
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "Command Test",
                logoUrl = "https://command.test/logo.png",
                address = addressDto
            )

            // When
            val command = requestDto.toCommand(partnerId)

            // Then
            assertEquals(partnerId, command.id)
            assertEquals("Command Test", command.legalName)
            assertEquals("https://command.test/logo.png", command.logoUrl)
            assertNotNull(command.address)
            assertEquals("Command Street", command.address!!.street)
            assertEquals("Command City", command.address!!.city)
            assertEquals("CC", command.address!!.country)
            assertEquals("12345", command.address!!.zipCode)
        }
    }

    @Nested
    inner class ResponseDtoMapping {

        @Test
        fun `should create valid UpdatePartnerInsurerResponseDto`() {
            // When
            val responseDto = UpdatePartnerInsurerResponseDto(
                id = partnerId.toString(),
                partnerInsurerCode = "TEST-01",
                legalName = "Test Response Insurance",
                logoUrl = "https://response.test/logo.png",
                status = "ACTIVE",
                address = AddressDto(
                    zipCode = "RESP001",
                    street = "Response Street",
                    city = "Response City",
                    country = "RC"
                ),
                createdAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-12-31T23:59:59Z"
            )

            // Then
            assertEquals(partnerId.toString(), responseDto.id)
            assertEquals("TEST-01", responseDto.partnerInsurerCode)
            assertEquals("Test Response Insurance", responseDto.legalName)
            assertEquals("https://response.test/logo.png", responseDto.logoUrl)
            assertEquals("ACTIVE", responseDto.status)
            assertEquals("Response Street", responseDto.address.street)
            assertEquals("Response City", responseDto.address.city)
            assertEquals("RC", responseDto.address.country)
            assertEquals("RESP001", responseDto.address.zipCode)
            assertEquals("2023-01-01T00:00:00Z", responseDto.createdAt)
            assertEquals("2023-12-31T23:59:59Z", responseDto.updatedAt)
        }

        @Test
        fun `should handle null logoUrl in response`() {
            // When
            val responseDto = UpdatePartnerInsurerResponseDto(
                id = partnerId.toString(),
                partnerInsurerCode = "TEST-02",
                legalName = "Test No Logo",
                logoUrl = null,
                status = "SUSPENDED",
                address = AddressDto(
                    zipCode = null,
                    street = "No Logo Street",
                    city = "No Logo City",
                    country = "NL"
                ),
                createdAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-12-31T23:59:59Z"
            )

            // Then
            assertNull(responseDto.logoUrl)
            assertEquals("Test No Logo", responseDto.legalName)
            assertEquals("SUSPENDED", responseDto.status)
            assertNull(responseDto.address.zipCode)
        }
    }
}