package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerUpdatedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
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
import java.util.*

@ExtendWith(MockitoExtension::class)
class UpdatePartnerInsurerCommandHandlerTest {

    @Mock
    private lateinit var queryRepository: PartnerInsurerQueryRepository

    @Mock
    private lateinit var commandRepository: PartnerInsurerCommandRepository

    @Mock
    private lateinit var domainEventPublisher: DomainEventPublisher

    private lateinit var handler: UpdatePartnerInsurerCommandHandler

    private val partnerId = UUID.randomUUID()
    private val partnerCode = "AXA-GA"
    private val taxId = "GA-123456"
    private val originalLegalName = "AXA Gabon"
    private val originalAddress = Address("Boulevard", "Libreville", "GA", "BP 001")
    private val originalLogoUrl = Url("https://old-logo.com/logo.png")

    @BeforeEach
    fun setUp() {
        handler = UpdatePartnerInsurerCommandHandler(
            queryRepository = queryRepository,
            commandRepository = commandRepository,
            domainEventPublisher = domainEventPublisher
        )
    }

    private fun createPartnerInsurer(): PartnerInsurer {
        return PartnerInsurer.reconstitute(
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
        fun `should update legal name only`() = runTest {
            // Given
            val newLegalName = "AXA Gabon SA"
            val command = UpdatePartnerInsurerCommand(
                id = partnerId,
                legalName = newLegalName,
                logoUrl = null,
                address = null
            )
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any<PartnerInsurer>())).thenReturn(true)

            // When
            val result = handler.invoke(command)

            // Then
            assertNotNull(result)
            assertEquals(partnerId.toString(), result.id)
            assertEquals(newLegalName, result.legalName)
            assertEquals(partnerCode, result.partnerInsurerCode)
            assertEquals(originalLogoUrl.value, result.logoUrl)

            // Verify repository interactions
            verify(queryRepository).findById(partnerId)
            verify(commandRepository).update(any<PartnerInsurer>())
        }

        @Test
        fun `should update logo URL only`() = runTest {
            // Given
            val newLogoUrl = "https://new-logo.com/logo.png"
            val command = UpdatePartnerInsurerCommand(
                id = partnerId,
                legalName = null,
                logoUrl = newLogoUrl,
                address = null
            )
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any<PartnerInsurer>())).thenReturn(true)

            // When
            val result = handler.invoke(command)

            // Then
            assertEquals(newLogoUrl, result.logoUrl)
            assertEquals(originalLegalName, result.legalName)

            verify(queryRepository).findById(partnerId)
            verify(commandRepository).update(any<PartnerInsurer>())
        }

        @Test
        fun `should update address only`() = runTest {
            // Given
            val newAddress = Address("Avenue de l'Indépendance", "Port-Gentil", "GA", "BP 002")
            val command = UpdatePartnerInsurerCommand(
                id = partnerId,
                legalName = null,
                logoUrl = null,
                address = newAddress
            )
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any<PartnerInsurer>())).thenReturn(true)

            // When
            val result = handler.invoke(command)

            // Then
            assertEquals("Avenue de l'Indépendance", result.address.street)
            assertEquals("Port-Gentil", result.address.city)
            assertEquals("GA", result.address.country)
            assertEquals("BP 002", result.address.zipCode)
            assertEquals(originalLegalName, result.legalName)

            verify(queryRepository).findById(partnerId)
            verify(commandRepository).update(any<PartnerInsurer>())
        }
    }

    @Nested
    inner class ErrorCases {

        @Test
        fun `should throw NoSuchElementException when partner not found`() = runTest {
            // Given
            val command = UpdatePartnerInsurerCommand(
                id = partnerId,
                legalName = "New Name",
                logoUrl = null,
                address = null
            )

            `when`(queryRepository.findById(partnerId)).thenReturn(null)

            // When & Then
            assertThrows<NoSuchElementException> {
                handler.invoke(command)
            }

            verify(queryRepository).findById(partnerId)
            verifyNoInteractions(commandRepository)
            verifyNoInteractions(domainEventPublisher)
        }

        @Test
        fun `should throw IllegalArgumentException when command has no changes`() = runTest {
            // Given
            val command = UpdatePartnerInsurerCommand(
                id = partnerId,
                legalName = null,
                logoUrl = null,
                address = null
            )

            // When & Then
            assertThrows<IllegalArgumentException> {
                handler.invoke(command)
            }

            verifyNoInteractions(queryRepository)
            verifyNoInteractions(commandRepository)
            verifyNoInteractions(domainEventPublisher)
        }

        @Test
        fun `should throw FailedToUpdateEntityException when repository update fails`() = runTest {
            // Given
            val command = UpdatePartnerInsurerCommand(
                id = partnerId,
                legalName = "New Name",
                logoUrl = null,
                address = null
            )
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any<PartnerInsurer>())).thenReturn(false)

            // When & Then
            assertThrows<FailedToUpdateEntityException> {
                handler.invoke(command)
            }

            verify(queryRepository).findById(partnerId)
            verify(commandRepository).update(any<PartnerInsurer>())
            verifyNoInteractions(domainEventPublisher)
        }
    }
}