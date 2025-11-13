package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.ChangePartnerInsurerStatusCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.DomainEvent
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ChangePartnerInsurerStatusCommandHandlerTest {

    @Mock
    private lateinit var queryRepository: PartnerInsurerQueryRepository

    @Mock
    private lateinit var commandRepository: PartnerInsurerCommandRepository

    @Mock
    private lateinit var domainEventPublisher: DomainEventPublisher

    private lateinit var handler: ChangePartnerInsurerStatusCommandHandler
    
    private lateinit var partnerId: UUID
    private lateinit var partnerInsurer: PartnerInsurer
    
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        handler = ChangePartnerInsurerStatusCommandHandler(
            queryRepository,
            commandRepository,
            domainEventPublisher
        )
        
        partnerId = UUID.randomUUID()
        partnerInsurer = createTestPartnerInsurer()
    }
    
    @Test
    fun `should successfully change status from ONBOARDING to ACTIVE`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ONBOARDING)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.ACTIVE, "Validation completed")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(true)
        
        // When
        val result = handler.invoke(command)
        
        // Then
        assertNotNull(result)
        assertEquals(partnerId, result.id)
        assertEquals("TEST-PARTNER", result.partnerInsurerCode)
        assertEquals("ONBOARDING", result.oldStatus)
        assertEquals("ACTIVE", result.newStatus)
        assertEquals("Validation completed", result.reason)
        assertEquals(PartnerInsurerStatus.ACTIVE, partnerInsurer.status)

        verify(commandRepository).update(partnerInsurer)
        verify(domainEventPublisher).publish(any<List<DomainEvent>>())

    }
    
    @Test
    fun `should successfully change status from ACTIVE to SUSPENDED`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.SUSPENDED, "Policy violation")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(true)
        
        // When
        val result = handler.invoke(command)
        
        // Then
        assertEquals("ACTIVE", result.oldStatus)
        assertEquals("SUSPENDED", result.newStatus)
        assertEquals("Policy violation", result.reason)
        assertEquals(PartnerInsurerStatus.SUSPENDED, partnerInsurer.status)
    }
    
    @Test
    fun `should successfully change status from ACTIVE to MAINTENANCE`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.MAINTENANCE, "System maintenance")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(true)
        
        // When
        val result = handler.invoke(command)
        
        // Then
        assertEquals("ACTIVE", result.oldStatus)
        assertEquals("MAINTENANCE", result.newStatus)
        assertEquals("System maintenance", result.reason)
        assertEquals(PartnerInsurerStatus.MAINTENANCE, partnerInsurer.status)
    }
    
    @Test
    fun `should successfully change status from ACTIVE to DEACTIVATED`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.DEACTIVATED, "Contract terminated")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(true)
        
        // When
        val result = handler.invoke(command)
        
        // Then
        assertEquals("ACTIVE", result.oldStatus)
        assertEquals("DEACTIVATED", result.newStatus)
        assertEquals("Contract terminated", result.reason)
        assertEquals(PartnerInsurerStatus.DEACTIVATED, partnerInsurer.status)
    }
    
    @Test
    fun `should successfully change status from SUSPENDED to ACTIVE`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.SUSPENDED)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.ACTIVE, "Issue resolved")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(true)
        
        // When
        val result = handler.invoke(command)
        
        // Then
        assertEquals("SUSPENDED", result.oldStatus)
        assertEquals("ACTIVE", result.newStatus)
        assertEquals("Issue resolved", result.reason)
        assertEquals(PartnerInsurerStatus.ACTIVE, partnerInsurer.status)
    }
    
    @Test
    fun `should successfully change status from MAINTENANCE to ACTIVE`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.MAINTENANCE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.ACTIVE, "Maintenance completed")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(true)
        
        // When
        val result = handler.invoke(command)
        
        // Then
        assertEquals("MAINTENANCE", result.oldStatus)
        assertEquals("ACTIVE", result.newStatus)
        assertEquals("Maintenance completed", result.reason)
        assertEquals(PartnerInsurerStatus.ACTIVE, partnerInsurer.status)
    }
    
    @Test
    fun `should throw IllegalArgumentException for invalid transition from ONBOARDING to SUSPENDED`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ONBOARDING)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.SUSPENDED, "Invalid transition")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            handler.invoke(command)
        }
        
        assertEquals("Invalid status transition from ONBOARDING to SUSPENDED. Valid transitions from ONBOARDING are: ACTIVE", exception.message)
    }
    
    @Test
    fun `should throw IllegalArgumentException for invalid transition from SUSPENDED to MAINTENANCE`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.SUSPENDED)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.MAINTENANCE, "Invalid transition")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            handler.invoke(command)
        }
        
        assertEquals("Invalid status transition from SUSPENDED to MAINTENANCE. Valid transitions from SUSPENDED are: ACTIVE", exception.message)
    }
    
    @Test
    fun `should throw IllegalArgumentException for transition to ONBOARDING`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.ONBOARDING, "Cannot go back")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            handler.invoke(command)
        }
        
        assertEquals("Invalid status transition from ACTIVE to ONBOARDING. Valid transitions from ACTIVE are: SUSPENDED, MAINTENANCE, DEACTIVATED", exception.message)
    }
    
    @Test
    fun `should throw IllegalStateException when status is already the target status`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.ACTIVE, "Already active")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        
        // When & Then
        val exception = assertThrows<IllegalStateException> {
            handler.invoke(command)
        }
        
        assertEquals("Partner insurer status is already ACTIVE", exception.message)
    }
    
    @Test
    fun `should throw NoSuchElementException when partner not found`() = runTest {
        // Given
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.ACTIVE, "Not found")

        whenever(queryRepository.findById(partnerId)).thenReturn(null)
        
        // When & Then
        val exception = assertThrows<NoSuchElementException> {
            handler.invoke(command)
        }
        
        assertEquals("Partner insurer with id $partnerId not found", exception.message)
    }
    
    @Test
    fun `should throw IllegalArgumentException when reason is missing for SUSPENDED`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.SUSPENDED, null)

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        
        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            handler.invoke(command)
        }
        
        assertEquals("Reason is required to suspend", exception.message)
    }
    
    @Test
    fun `should throw FailedToUpdateEntityException when update fails`() = runTest {
        // Given
        partnerInsurer = createTestPartnerInsurer(PartnerInsurerStatus.ACTIVE)
        val command = ChangePartnerInsurerStatusCommand(partnerId, PartnerInsurerStatus.SUSPENDED, "Test reason")

        whenever(queryRepository.findById(partnerId)).thenReturn(partnerInsurer)
        whenever(commandRepository.update(partnerInsurer)).thenReturn(false)
        
        // When & Then
        assertThrows<FailedToUpdateEntityException> {
            handler.invoke(command)
        }
    }
    
    private fun createTestPartnerInsurer(status: PartnerInsurerStatus = PartnerInsurerStatus.ACTIVE): PartnerInsurer {
        return PartnerInsurer.reconstitute(
            id = DomainEntityId(partnerId),
            partnerInsurerCode = "TEST-PARTNER",
            legalName = "Test Partner Insurer",
            taxIdentificationNumber = TaxIdentificationNumber("123456789"),
            logoUrl = null,
            contacts = emptySet(),
            address = Address(
                street = "123 Test St",
                city = "Test City",
                country = "Test Country",
                zipCode = "12345"
            ),
            status = status,
            brokerPartnerInsurerAgreements = emptySet()
        )
    }
}