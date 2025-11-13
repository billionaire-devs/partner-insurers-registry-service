package com.bamboo.assur.partnerinsurers.registry.presentation.controllers

import com.bamboo.assur.partnerinsurers.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.UpdatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.UpdatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerResponseDto
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class UpdatePartnerInsurerControllerTest {

    @Mock
    private lateinit var updatePartnerInsurerCommandHandler: UpdatePartnerInsurerCommandHandler

    private lateinit var controller: PartnerInsurerController

    private val partnerId = UUID.randomUUID()
    private val partnerCode = "AXA-GA"
    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        controller = PartnerInsurerController(
            createCommandHandler = mock(),
            getPartnerInsurersQueryHandler = mock(),
            getPartnerInsurerSummaryProjectionQueryHandler = mock(),
            getPartnerInsurerDetailedProjectionQueryHandler = mock(),
            getFullPartnerInsurerQueryHandler = mock(),
            changeStatusCommandHandler = mock(),
            updatePartnerInsurerCommandHandler = updatePartnerInsurerCommandHandler,
            getPartnerInsurersSummariesQueryHandler = mock()
        )
    }

    private fun createSuccessResponseDto(): UpdatePartnerInsurerResponseDto {
        return UpdatePartnerInsurerResponseDto(
            id = partnerId.toString(),
            partnerInsurerCode = partnerCode,
            legalName = "AXA Gabon SA",
            logoUrl = "https://new-logo.com/logo.png",
            status = "ACTIVE",
            address = AddressDto(
                zipCode = "BP 001",
                street = "Boulevard",
                city = "Libreville",
                country = "GA"
            ),
            createdAt = now.toString(),
            updatedAt = now.toString()
        )
    }

    @Nested
    inner class SuccessfulUpdates {

        @Test
        fun `should update partner with legal name only`() = runTest {
            // Given
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "AXA Gabon SA",
                logoUrl = null,
                address = null
            )
            val expectedResponse = createSuccessResponseDto()

            `when`(updatePartnerInsurerCommandHandler.invoke(any<UpdatePartnerInsurerCommand>()))
                .thenReturn(expectedResponse)

            // When
            val response = controller.updatePartnerInsurer(partnerId, requestDto)

            // Then
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(expectedResponse, response.body)

            // Verify command handler was called
            verify(updatePartnerInsurerCommandHandler).invoke(any<UpdatePartnerInsurerCommand>())
        }

        @Test
        fun `should update partner with all fields`() = runTest {
            // Given
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "AXA Gabon SA",
                logoUrl = "https://new-logo.com/logo.png",
                address = AddressDto(
                    zipCode = "BP 001",
                    street = "Boulevard",
                    city = "Libreville",
                    country = "GA"
                )
            )
            val expectedResponse = createSuccessResponseDto()

            `when`(updatePartnerInsurerCommandHandler.invoke(any<UpdatePartnerInsurerCommand>()))
                .thenReturn(expectedResponse)

            // When
            val response = controller.updatePartnerInsurer(partnerId, requestDto)

            // Then
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(expectedResponse, response.body)

            verify(updatePartnerInsurerCommandHandler).invoke(any<UpdatePartnerInsurerCommand>())
        }
    }

    @Nested
    inner class ErrorHandling {

        @Test
        fun `should propagate NoSuchElementException from command handler`() = runTest {
            // Given
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "New Name",
                logoUrl = null,
                address = null
            )

            `when`(updatePartnerInsurerCommandHandler.invoke(any<UpdatePartnerInsurerCommand>()))
                .thenThrow(NoSuchElementException("PartnerInsurer not found with id: $partnerId"))

            // When & Then
            assertThrows<NoSuchElementException> {
                controller.updatePartnerInsurer(partnerId, requestDto)
            }

            verify(updatePartnerInsurerCommandHandler).invoke(any<UpdatePartnerInsurerCommand>())
        }

        @Test
        fun `should propagate IllegalArgumentException from command handler`() = runTest {
            // Given
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = null,
                logoUrl = null,
                address = null
            )

            `when`(updatePartnerInsurerCommandHandler.invoke(any<UpdatePartnerInsurerCommand>()))
                .thenThrow(IllegalArgumentException("Command has no changes"))

            // When & Then
            assertThrows<IllegalArgumentException> {
                controller.updatePartnerInsurer(partnerId, requestDto)
            }

            verify(updatePartnerInsurerCommandHandler).invoke(any<UpdatePartnerInsurerCommand>())
        }

        @Test
        fun `should propagate runtime exceptions from command handler`() = runTest {
            // Given
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "New Name",
                logoUrl = null,
                address = null
            )

            `when`(updatePartnerInsurerCommandHandler.invoke(any<UpdatePartnerInsurerCommand>()))
                .thenThrow(RuntimeException("Database connection failed"))

            // When & Then
            assertThrows<RuntimeException> {
                controller.updatePartnerInsurer(partnerId, requestDto)
            }

            verify(updatePartnerInsurerCommandHandler).invoke(any<UpdatePartnerInsurerCommand>())
        }
    }

    @Nested
    inner class ResponseMapping {

        @Test
        fun `should return HTTP 200 with response DTO on success`() = runTest {
            // Given
            val requestDto = UpdatePartnerInsurerRequestDto(
                legalName = "Updated Name",
                logoUrl = null,
                address = null
            )
            val expectedResponse = UpdatePartnerInsurerResponseDto(
                id = partnerId.toString(),
                partnerInsurerCode = partnerCode,
                legalName = "Updated Name",
                logoUrl = null,
                status = "ACTIVE",
                address = AddressDto(
                    zipCode = "BP 001",
                    street = "Boulevard",
                    city = "Libreville",
                    country = "GA"
                ),
                createdAt = now.toString(),
                updatedAt = now.toString()
            )

            `when`(updatePartnerInsurerCommandHandler.invoke(any<UpdatePartnerInsurerCommand>()))
                .thenReturn(expectedResponse)

            // When
            val response = controller.updatePartnerInsurer(partnerId, requestDto)

            // Then
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(expectedResponse, response.body)
            assertNotNull(response.body)
            assertEquals("Updated Name", response.body!!.legalName)
            assertEquals(partnerId.toString(), response.body!!.id)
            assertEquals(partnerCode, response.body!!.partnerInsurerCode)
        }
    }
}