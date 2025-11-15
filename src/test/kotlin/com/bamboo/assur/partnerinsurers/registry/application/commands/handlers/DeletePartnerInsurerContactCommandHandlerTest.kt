package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.DeletePartnerInsurerContactCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.events.PartnerInsurerContactDeletedEvent
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.ContactRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Email
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@ExtendWith(MockitoExtension::class)
class DeletePartnerInsurerContactCommandHandlerTest {

    @Mock
    private lateinit var partnerInsurerQueryRepository: PartnerInsurerQueryRepository

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var domainEventPublisher: DomainEventPublisher

    private lateinit var handler: DeletePartnerInsurerContactCommandHandler

    private val partnerId = UUID.randomUUID()
    private val contactId = UUID.randomUUID()
    private val partnerCode = "AXA-GA"

    @BeforeEach
    fun setUp() {
        handler = DeletePartnerInsurerContactCommandHandler(
            partnerInsurerQueryRepository,
            contactRepository,
            domainEventPublisher
        )
    }

    @Nested
    inner class `When deleting contact successfully` {

        @OptIn(ExperimentalTime::class)
        @Test
        fun `should delete contact and publish event`() = runTest {
            // Given
            val contact = Contact.create(
                fullName = "John Doe",
                email = Email("john.doe@example.com"),
                phone = Phone("+24106000000"),
                contactRole = "MAIN_CONTACT"
            )

            val partnerInsurer = PartnerInsurer.reconstitute(
                id = DomainEntityId(partnerId),
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber("123456789"),
                logoUrl = null,
                contacts = setOf(contact),
                address = Address("Boulevard Triomphal", "Libreville", "GA", "BP 123"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet()
            )

            whenever(partnerInsurerQueryRepository.findById(partnerId))
                .thenReturn(partnerInsurer)

            val command = DeletePartnerInsurerContactCommand(
                partnerInsurerId = partnerId,
                contactId = contact.id.value
            )

            // When
            handler.invoke(command)

            // Then
            verify(contactRepository).delete(contact, deletedAt = Clock.System.now(), UUID.randomUUID())

            val eventCaptor = argumentCaptor<List<DomainEvent>>()
            verify(domainEventPublisher).publish(eventCaptor.capture())

            val events = eventCaptor.firstValue
            assertEquals(1, events.size)
            assertTrue(events.first() is PartnerInsurerContactDeletedEvent)

            val deletedEvent = events.first() as PartnerInsurerContactDeletedEvent
            assertEquals(partnerInsurer.id, deletedEvent.aggregateId)
            assertEquals(contact.id, deletedEvent.contactId)
        }
    }

    @Nested
    inner class `When partner insurer not found` {

        @Test
        fun `should throw EntityNotFoundException`() = runTest {
            // Given
            whenever(partnerInsurerQueryRepository.findById(partnerId))
                .thenReturn(null)

            val command = DeletePartnerInsurerContactCommand(
                partnerInsurerId = partnerId,
                contactId = contactId
            )

            // When & Then
            assertThrows<EntityNotFoundException> {
                handler.invoke(command)
            }
        }
    }

    @Nested
    inner class `When contact not found` {

        @Test
        fun `should throw EntityNotFoundException`() = runTest {
            // Given
            val partnerInsurer = PartnerInsurer.reconstitute(
                id = DomainEntityId(partnerId),
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber("123456789"),
                logoUrl = null,
                contacts = emptySet(),
                address = Address("Boulevard Triomphal", "Libreville", "GA", "BP 123"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet()
            )

            whenever(partnerInsurerQueryRepository.findById(partnerId))
                .thenReturn(partnerInsurer)

            val command = DeletePartnerInsurerContactCommand(
                partnerInsurerId = partnerId,
                contactId = contactId
            )

            // When & Then
            assertThrows<EntityNotFoundException> {
                handler.invoke(command)
            }
        }
    }
}