package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.DeletePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerDeletedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.InvalidOperationException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
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
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class DeletePartnerInsurerCommandHandlerTest {

    @Mock
    private lateinit var queryRepository: PartnerInsurerQueryRepository

    @Mock
    private lateinit var commandRepository: PartnerInsurerCommandRepository

    @Mock
    private lateinit var domainEventPublisher: DomainEventPublisher

    private lateinit var handler: DeletePartnerInsurerCommandHandler

    private val partnerId = UUID.randomUUID()
    private val partnerCode = "AXA-GA"
    private val reason = "Partnership terminated"

    @BeforeEach
    fun setUp() {
        handler = DeletePartnerInsurerCommandHandler(
            queryRepository = queryRepository,
            commandRepository = commandRepository,
            domainEventPublisher = domainEventPublisher
        )
    }

    private fun createPartnerInsurer(status: PartnerInsurerStatus = PartnerInsurerStatus.ACTIVE): PartnerInsurer {
        return PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = partnerCode,
            legalName = "AXA Gabon",
            taxIdentificationNumber = TaxIdentificationNumber("GA123456"),
            logoUrl = null,
            contacts = emptySet(),
            address = Address("Street", "City", "Country", "ZIP"),
            status = status,
            brokerPartnerInsurerAgreements = emptySet()
        )
    }

    @Nested
    inner class SuccessfulDeletion {

        @Test
        fun `should delete partner insurer successfully`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any())).thenReturn(true)

            // When
            val result = handler.invoke(command)

            // Then
            assertEquals(partnerId.toString(), result.id)
            assertEquals(partnerCode, result.partnerInsurerCode)

            // Verify repository interactions
            verify(queryRepository).findById(partnerId)
            verify(commandRepository).update(any())

            // Verify domain event publishing
            val eventCaptor = argumentCaptor<List<DomainEvent>>()
            verify(domainEventPublisher).publish(eventCaptor.capture())

            val capturedEvents = eventCaptor.firstValue
            assertEquals(1, capturedEvents.size)
            assertTrue(capturedEvents.first() is PartnerInsurerDeletedEvent)

            val deletedEvent = capturedEvents.first() as PartnerInsurerDeletedEvent
            assertEquals(partnerCode, deletedEvent.partnerInsurerCode)
            assertEquals(reason, deletedEvent.reason)
        }

        @Test
        fun `should delete partner insurer without reason`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, null)
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any())).thenReturn(true)

            // When
            val result = handler.invoke(command)

            // Verify domain event with null reason
            val eventCaptor = argumentCaptor<List<DomainEvent>>()
            verify(domainEventPublisher).publish(eventCaptor.capture())

            val deletedEvent = eventCaptor.firstValue.first() as PartnerInsurerDeletedEvent
            assertNull(deletedEvent.reason)
        }
    }

    @Nested
    inner class ErrorHandling {

        @Test
        fun `should throw NoSuchElementException when partner not found`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            `when`(queryRepository.findById(partnerId)).thenReturn(null)

            // When & Then
            assertThrows<NoSuchElementException> {
                handler.invoke(command)
            }

            // Verify no update or event publishing occurred
            verify(commandRepository, never()).update(any<PartnerInsurer>())
            verify(domainEventPublisher, never()).publish(any<List<DomainEvent>>())
        }

        @Test
        fun `should propagate InvalidOperationException when partner already deleted`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            // Create an active partner first, then delete it to make it properly soft-deleted
            val partnerToDelete = createPartnerInsurer(PartnerInsurerStatus.ACTIVE)
            partnerToDelete.delete(reason, null) // This makes it properly soft-deleted

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerToDelete)

            // When & Then
            assertThrows<InvalidOperationException> {
                handler.invoke(command)
            }

            // Verify no update or event publishing occurred
            verify(commandRepository, never()).update(any<PartnerInsurer>())
            verify(domainEventPublisher, never()).publish(any<List<DomainEvent>>())
        }

        @Test
        fun `should throw FailedToUpdateEntityException when update fails`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any())).thenReturn(false)

            // When & Then
            assertThrows<FailedToUpdateEntityException> {
                handler.invoke(command)
            }

            // Verify update was attempted but no events published
            verify(commandRepository).update(any())
            verify(domainEventPublisher, never()).publish(any<List<DomainEvent>>())
        }

        @Test
        fun `should handle repository exception during update`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any())).thenThrow(RuntimeException("Database error"))

            // When & Then
            assertThrows<FailedToUpdateEntityException> {
                handler.invoke(command)
            }

            // Verify no events published when update fails
            verify(domainEventPublisher, never()).publish(any<List<DomainEvent>>())
        }
    }

    @Nested
    inner class DomainLogicValidation {

        @Test
        fun `should verify domain delete method is called with correct reason`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            val partnerInsurer = spy(createPartnerInsurer())

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any())).thenReturn(true)

            // When
            handler.invoke(command)

            // Then
            verify(partnerInsurer).delete(reason, null)
        }

        @Test
        fun `should verify partner status is DEACTIVATED after deletion`() = runTest {
            // Given
            val command = DeletePartnerInsurerCommand(partnerId, reason)
            val partnerInsurer = createPartnerInsurer()

            `when`(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
            `when`(commandRepository.update(any())).thenReturn(true)

            // When
            handler.invoke(command)

            // Then
            assertEquals(PartnerInsurerStatus.DEACTIVATED, partnerInsurer.status)
        }
    }
}