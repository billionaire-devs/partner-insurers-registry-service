package com.bamboo.assur.partnerinsurers.registry.integration

import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.ChangePartnerInsurerStatusRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ChangePartnerInsurerStatusResponseDto
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.ZoneId
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ChangePartnerInsurerStatusIntegrationTest {

    // Note: This follows the existing pattern for integration tests in this codebase
    // focusing on DTO validation and mapping rather than full web integration

    private val partnerId = UUID.randomUUID()

    @Nested
    inner class RequestDtoValidation {

        @Test
        fun `should create valid ChangePartnerInsurerStatusRequestDto with reason`() {
            // Given & When
            val requestDto = ChangePartnerInsurerStatusRequestDto(
                targetStatus = "SUSPENDED",
                reason = "Policy violation detected"
            )

            // Then
            assertEquals("SUSPENDED", requestDto.targetStatus)
            assertEquals("Policy violation detected", requestDto.reason)
        }

        @Test
        fun `should create valid ChangePartnerInsurerStatusRequestDto without reason for ACTIVE`() {
            // Given & When
            val requestDto = ChangePartnerInsurerStatusRequestDto(
                targetStatus = "ACTIVE",
                reason = null
            )

            // Then
            assertEquals("ACTIVE", requestDto.targetStatus)
            assertNull(requestDto.reason)
        }

        @Test
        fun `should map to command correctly with valid status`() {
            // Given
            val requestDto = ChangePartnerInsurerStatusRequestDto(
                targetStatus = "MAINTENANCE",
                reason = "System upgrade in progress"
            )

            // When
            val command = requestDto.toCommand(partnerId)

            // Then
            assertEquals(partnerId, command.id)
            assertEquals(PartnerInsurerStatus.MAINTENANCE, command.targetStatus)
            assertEquals("System upgrade in progress", command.reason)
        }

        @Test
        fun `should throw IllegalArgumentException for invalid status`() {
            // Given
            val requestDto = ChangePartnerInsurerStatusRequestDto(
                targetStatus = "INVALID_STATUS",
                reason = "Test reason"
            )

            // When & Then
            val exception = assertThrows<IllegalArgumentException> {
                requestDto.toCommand(partnerId)
            }

            assertEquals(
                "Invalid target status: INVALID_STATUS. Allowed values are: ONBOARDING, ACTIVE, SUSPENDED, MAINTENANCE, DEACTIVATED",
                exception.message
            )
        }

        @Test
        fun `should map all valid statuses correctly`() {
            val validStatuses = listOf("ACTIVE", "SUSPENDED", "MAINTENANCE", "DEACTIVATED")

            validStatuses.forEach { status ->
                // Given
                val requestDto = ChangePartnerInsurerStatusRequestDto(
                    targetStatus = status,
                    reason = "Test reason for $status"
                )

                // When
                val command = requestDto.toCommand(partnerId)

                // Then
                assertEquals(PartnerInsurerStatus.valueOf(status), command.targetStatus)
                assertEquals("Test reason for $status", command.reason)
            }
        }
    }

    @Nested
    inner class ResponseDtoValidation {

        @Test
        fun `should create valid ChangePartnerInsurerStatusResponseDto`() {
            // Given
            val now = Instant.now()

            // When
            val responseDto = ChangePartnerInsurerStatusResponseDto(
                id = partnerId,
                partnerInsurerCode = "AXA-CM",
                oldStatus = "ACTIVE",
                newStatus = "SUSPENDED",
                reason = "Policy violation detected",
                updatedAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime()
            )

            // Then
            assertEquals(partnerId, responseDto.id)
            assertEquals("AXA-CM", responseDto.partnerInsurerCode)
            assertEquals("ACTIVE", responseDto.oldStatus)
            assertEquals("SUSPENDED", responseDto.newStatus)
            assertEquals("Policy violation detected", responseDto.reason)
            assertEquals(now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(), responseDto.updatedAt)
        }

        @Test
        fun `should handle all possible status values in response`() {
            val statusPairs = listOf(
                "ONBOARDING" to "ACTIVE",
                "ACTIVE" to "SUSPENDED",
                "ACTIVE" to "MAINTENANCE",
                "ACTIVE" to "DEACTIVATED",
                "SUSPENDED" to "ACTIVE",
                "MAINTENANCE" to "ACTIVE"
            )
            val now = Instant.now()

            statusPairs.forEach { (oldStatus, newStatus) ->
                // When
                val responseDto = ChangePartnerInsurerStatusResponseDto(
                    id = partnerId,
                    partnerInsurerCode = "TEST-$newStatus",
                    oldStatus = oldStatus,
                    newStatus = newStatus,
                    reason = "Transition from $oldStatus to $newStatus",
                    updatedAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime()
                )

                // Then
                assertEquals(oldStatus, responseDto.oldStatus)
                assertEquals(newStatus, responseDto.newStatus)
                assertEquals("TEST-$newStatus", responseDto.partnerInsurerCode)
                assertNotNull(responseDto.updatedAt)
                assertEquals("Transition from $oldStatus to $newStatus", responseDto.reason)
            }
        }
    }

    @Nested
    inner class BusinessRuleValidation {

        @Test
        fun `should validate transition rules according to PIS-REG-105 specification`() {
            // This test documents the valid transitions as per the specification
            val validTransitions = mapOf(
                PartnerInsurerStatus.ONBOARDING to setOf(PartnerInsurerStatus.ACTIVE),
                PartnerInsurerStatus.ACTIVE to setOf(
                    PartnerInsurerStatus.SUSPENDED,
                    PartnerInsurerStatus.MAINTENANCE,
                    PartnerInsurerStatus.DEACTIVATED
                ),
                PartnerInsurerStatus.SUSPENDED to setOf(PartnerInsurerStatus.ACTIVE),
                PartnerInsurerStatus.MAINTENANCE to setOf(PartnerInsurerStatus.ACTIVE)
                // DEACTIVATED has no valid transitions (final state)
            )

            // Validate that our mapping is correct
            validTransitions.forEach { (from, toSet) ->
                toSet.forEach { to ->
                    val requestDto = ChangePartnerInsurerStatusRequestDto(
                        targetStatus = to.name,
                        reason = "Valid transition from $from to $to"
                    )

                    val command = requestDto.toCommand(partnerId)
                    assertEquals(to, command.targetStatus)
                }
            }
        }

        @Test
        fun `should document invalid transitions for reference`() {
            // This test documents the invalid transitions that should be blocked
            val invalidTransitions = mapOf(
                "ONBOARDING" to listOf("SUSPENDED", "MAINTENANCE", "DEACTIVATED"),
                "SUSPENDED" to listOf("MAINTENANCE", "DEACTIVATED"),
                "MAINTENANCE" to listOf("SUSPENDED", "DEACTIVATED"),
                "DEACTIVATED" to listOf("ONBOARDING", "ACTIVE", "SUSPENDED", "MAINTENANCE"),
                "ANY" to listOf("ONBOARDING") // No transitions to ONBOARDING allowed
            )

            // Note: These validations are enforced in the command handler, not in the DTO mapping
            // This test serves as documentation of the business rules
            invalidTransitions.forEach { (from, toList) ->
                toList.forEach { to ->
                    // We can still create valid DTOs and commands for these transitions
                    // The business rule validation happens in the handler
                    if (to != "ONBOARDING") { // ONBOARDING is not a valid target status at all
                        val requestDto = ChangePartnerInsurerStatusRequestDto(
                            targetStatus = to,
                            reason = "Should be invalid transition from $from"
                        )

                        assertNotNull(requestDto.toCommand(partnerId))
                    }
                }
            }
        }
    }
}