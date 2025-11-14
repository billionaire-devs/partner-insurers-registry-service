package com.bamboo.assur.partnerinsurers.registry.integration

import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.ContactDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.AddPartnerInsurerContactRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.CreatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.AddPartnerInsurerContactResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.CreatePartnerInsurerResponseDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
@Disabled("Spring Boot WebTestClient configuration needs adjustment for full integration testing")
@DisplayName("Add Partner Insurer Contact Integration Tests")
class AddPartnerInsurerContactIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    private suspend fun createPartnerInsurer(): CreatePartnerInsurerResponseDto {
        val request = CreatePartnerInsurerRequestDto(
            legalName = "Test Insurance Company",
            partnerInsurerCode = "TIC-2024",
            taxIdentificationNumber = "M123456789C",
            logoUrl = "https://test.com/logo.png",
            contacts = setOf(
                ContactDto(
                    fullName = "Initial Contact",
                    email = "initial@test.com",
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
            status = "ACTIVE"
        )

        return webTestClient
            .post()
            .uri("/api/partner-insurers/registry/v1/partner-insurers")
            .headers { it.setBasicAuth("morriss", "morrisson") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CreatePartnerInsurerResponseDto::class.java)
            .returnResult()
            .responseBody!!
    }

    private fun createValidAddContactRequest() = AddPartnerInsurerContactRequestDto(
        fullName = "Jean Dupont",
        email = "jean.dupont@axa.ga",
        phone = "+24106223344",
        contactRole = "Responsable Technique"
    )

    @Nested
    @DisplayName("Successful Contact Addition")
    inner class SuccessfulContactAddition {

        @Test
        fun `should add contact to existing partner insurer`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val request = createValidAddContactRequest()

            // When
            // Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().exists("Location")
        }

        @Test
        fun `should add multiple contacts to same partner insurer`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val firstContactRequest = createValidAddContactRequest()
            val secondContactRequest = AddPartnerInsurerContactRequestDto(
                fullName = "Marie Martin",
                email = "marie.martin@axa.ga",
                phone = "+24106223355",
                contactRole = "Responsable Commercial"
            )

            // When - Add first contact
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(firstContactRequest)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().exists("Location")

            // When - Add second contact
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(secondContactRequest)
                .exchange()
                .expectStatus().isCreated
                .expectHeader().exists("Location")
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    inner class ValidationErrors {

        @Test
        fun `should return 400 Bad Request for blank full name`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val request = AddPartnerInsurerContactRequestDto(
                fullName = "   ",
                email = "valid@email.com",
                phone = "+24106223344",
                contactRole = "Manager"
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `should return 400 Bad Request for invalid email`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val request = AddPartnerInsurerContactRequestDto(
                fullName = "Valid Name",
                email = "invalid-email",
                phone = "+24106223344",
                contactRole = "Manager"
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `should return 400 Bad Request for invalid phone format`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val request = AddPartnerInsurerContactRequestDto(
                fullName = "Valid Name",
                email = "valid@email.com",
                phone = "123", // Too short
                contactRole = "Manager"
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `should return 400 Bad Request for blank contact role`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val request = AddPartnerInsurerContactRequestDto(
                fullName = "Valid Name",
                email = "valid@email.com",
                phone = "+24106223344",
                contactRole = ""
            )

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `should return 404 Not Found for non-existent partner insurer`() = runTest {
            // Given
            val nonExistentPartnerId = "550e8400-e29b-41d4-a716-446655440000"
            val request = createValidAddContactRequest()

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", nonExistentPartnerId)
                .headers { it.setBasicAuth("morriss", "morrisson") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `should return 401 Unauthorized without authentication`() = runTest {
            // Given
            val partnerInsurer = createPartnerInsurer()
            val request = createValidAddContactRequest()

            // When & Then
            webTestClient
                .post()
                .uri("/api/partner-insurers/registry/v1/partner-insurers/{partnerId}/contacts", partnerInsurer.id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized
        }
    }
}