package com.bamboo.assur.partnerinsurers.registry.integration

import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.CreatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.CreatePartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.ContactDto
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Disabled("Spring Boot WebTestClient configuration needs adjustment for full integration testing")
@DisplayName("Create Partner Insurer Integration Tests")  
class CreatePartnerInsurerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private fun createValidCreateRequest(
        legalName: String,
        code: String,
        taxId: String,
        logoUrl: String? = null
    ) = CreatePartnerInsurerRequestDto(
        legalName = legalName,
        partnerInsurerCode = code,
        taxIdentificationNumber = taxId,
        logoUrl = logoUrl,
        contacts = setOf(
            ContactDto(
                fullName = "Test Contact",
                email = "test@insurance.com",
                phone = "+237123456789",
                role = "Manager"
            )
        ),
        address = AddressDto(
            street = "123 Test Street",
            city = "Test City",
            country = "Cameroun",
            zipCode = "12345"
        ),
        status = "ONBOARDING"
    )

    @Nested
    @DisplayName("Successful Creation")
    inner class SuccessfulCreation {

        @Test
        fun `should create partner insurer with all fields via HTTP POST`() = runTest {
            // Given
            val request = createValidCreateRequest(
                legalName = "Integration Test Insurance Co",
                code = "ITIC-2024",
                taxId = "M123456789C",
                logoUrl = "https://integration-test.com/logo.png"
            )

            // When
            val result = webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers")
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().exists("Location")
                .expectBody<CreatePartnerInsurerResponseDto>()
                .returnResult()
                .responseBody

            // Then
            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(request.legalName, result.legalName)
            assertEquals(request.partnerInsurerCode, result.partnerInsurerCode)
            // Note: Response DTO only includes basic fields, not tax ID or logo URL
            assertEquals("ONBOARDING", result.status)
        }

        @Test
        fun `should create partner insurer without optional logoUrl`() = runTest {
            // Given
            val request = createValidCreateRequest(
                legalName = "No Logo Insurance Co",
                code = "NLIC-2024",
                taxId = "M987654321C",
                logoUrl = null
            )

            // When
            val result = webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers")
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().exists("Location")
                .expectBody<CreatePartnerInsurerResponseDto>()
                .returnResult()
                .responseBody

            // Then
            assertNotNull(result)
            assertEquals(request.legalName, result.legalName)
            // Logo URL is not included in the create response DTO
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    inner class ValidationErrors {

        @Test
        fun `should return 400 Bad Request for blank legal name`() = runTest {
            // Given
            val request = createValidCreateRequest(
                legalName = "   ",
                code = "BLANK-2024",
                taxId = "M123456789C"
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers")
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `should return 400 Bad Request for empty partner insurer code`() = runTest {
            // Given
            val request = createValidCreateRequest(
                legalName = "Valid Insurance Company",
                code = "",
                taxId = "M123456789C"
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers")
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `should return 401 Unauthorized without authentication`() = runTest {
            // Given
            val request = createValidCreateRequest(
                legalName = "Unauthorized Test",
                code = "UT-2024",
                taxId = "M123456789C"
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized
        }
    }
}