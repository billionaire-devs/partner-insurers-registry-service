package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.application.commands.ChangePartnerInsurerStatusCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.ChangePartnerInsurerStatusResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
@Service
class ChangePartnerInsurerStatusCommandHandler(
    private val queryRepository: PartnerInsurerQueryRepository,
    private val commandRepository: PartnerInsurerCommandRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<ChangePartnerInsurerStatusCommand, ChangePartnerInsurerStatusResponseDto> {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(
        command: ChangePartnerInsurerStatusCommand,
    ): ChangePartnerInsurerStatusResponseDto {
        logger.info("Changing status for partner insurer {} to {}", command.id, command.targetStatus)

        val partner = queryRepository.findById(command.id)
            ?: throw NoSuchElementException("Partner insurer with id ${command.id} not found")

        // Check if status is already the target status (409 CONFLICT)
        if (partner.status == command.targetStatus) {
            error("Partner insurer status is already ${command.targetStatus}")
        }

        val oldStatus = partner.status
        validateTransition(partner.status, command.targetStatus)
        applyStatusChange(partner, command.targetStatus, command.reason)

        val updateSuccessful = try {
            commandRepository.update(partner)
        } catch (e: Exception) {
            logger.error("Error updating partner insurer status with id: ${command.id}", e)
            throw FailedToUpdateEntityException(
                getAggregateTypeOrEmpty<PartnerInsurer>(),
                command.id
            )
        }

        if (!updateSuccessful) {
            logger.error("Failed to update partner insurer status with id: ${command.id}")
            throw FailedToUpdateEntityException(
                getAggregateTypeOrEmpty<PartnerInsurer>(),
                command.id
            )
        }

        with(partner) {
            if (hasPendingEvents()) {
                domainEventPublisher.publish(getDomainEvents())
                clearDomainEvents()
            }
        }

        return ChangePartnerInsurerStatusResponseDto(
            id = partner.id.value,
            partnerInsurerCode = partner.partnerInsurerCode,
            newStatus = partner.status.name,
            oldStatus = oldStatus.name,
            reason = command.reason,
            updatedAt = partner.updatedAt
                .toJavaInstant()
                .atZone(ZoneId.of("Africa/Libreville"))
                .toOffsetDateTime(),
        )
    }

    /**
     * Validates if the transition from current status to target status is allowed
     * according to PIS-REG-105 specification.
     */
    private fun validateTransition(currentStatus: PartnerInsurerStatus, targetStatus: PartnerInsurerStatus) {
        val allowedTransitions = mapOf(
            PartnerInsurerStatus.ONBOARDING to setOf(PartnerInsurerStatus.ACTIVE),
            PartnerInsurerStatus.ACTIVE to setOf(
                PartnerInsurerStatus.SUSPENDED,
                PartnerInsurerStatus.MAINTENANCE,
                PartnerInsurerStatus.DEACTIVATED
            ),
            PartnerInsurerStatus.SUSPENDED to setOf(PartnerInsurerStatus.ACTIVE),
            PartnerInsurerStatus.MAINTENANCE to setOf(PartnerInsurerStatus.ACTIVE)
        )

        val validTargets = allowedTransitions[currentStatus] ?: emptySet()

        if (targetStatus !in validTargets) {
            throw IllegalArgumentException(
                "Invalid status transition from $currentStatus to $targetStatus. " +
                        "Valid transitions from $currentStatus are: ${validTargets.joinToString(", ")}"
            )
        }
    }

    private fun applyStatusChange(
        partner: PartnerInsurer,
        targetStatus: PartnerInsurerStatus,
        reason: String?,
    ) {
        when (targetStatus) {
            PartnerInsurerStatus.ACTIVE -> {
                partner.activate(reason)
            }
            PartnerInsurerStatus.SUSPENDED -> {
                partner.suspend(requireNotNull(reason) { "Reason is required to suspend" })
            }
            PartnerInsurerStatus.MAINTENANCE -> {
                partner.putInMaintenance(requireNotNull(reason) { "Reason is required to put on maintenance" })
            }
            PartnerInsurerStatus.DEACTIVATED -> {
                partner.deactivate(requireNotNull(reason) { "Reason is required to deactivate" })
            }
            PartnerInsurerStatus.ONBOARDING -> {
                throw IllegalArgumentException("Cannot transition back to ONBOARDING status")
            }
        }
    }
}
