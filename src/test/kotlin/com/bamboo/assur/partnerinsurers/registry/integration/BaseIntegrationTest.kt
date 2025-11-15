package com.bamboo.assur.partnerinsurers.registry.integration

import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.ContactDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.CreatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.CreatePartnerInsurerResponseDto
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestContainersConfiguration::class)
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
abstract class BaseIntegrationTest {
    suspend fun createPartnerInsurer(webTestClient: WebTestClient, partnerInsurerCode: String): CreatePartnerInsurerResponseDto {
        val request = CreatePartnerInsurerRequestDto(
            legalName = "Test Insurance Company",
            partnerInsurerCode = partnerInsurerCode,
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
            .uri("/v1/partner-insurers")
            .headers { it.setBasicAuth("morriss", "morrisson") }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CreatePartnerInsurerResponseDto::class.java)
            .returnResult()
            .responseBody!!
    }
}