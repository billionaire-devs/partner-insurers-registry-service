package com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.Result
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.ChangePartnerInsurerStatusCommand
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.responses.ChangePartnerInsurerStatusResponseDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChangePartnerInsurerStatusCommandHandler(
    private val repository: PartnerInsurerRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<ChangePartnerInsurerStatusCommand, Result<ChangePartnerInsurerStatusResponseDto>> {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(
        command: ChangePartnerInsurerStatusCommand,
    ): Result<ChangePartnerInsurerStatusResponseDto> = Result.of {
        logger.info("Changing status for partner insurer {} to {}", command.id, command.targetStatus)

        val partner = repository.findById(command.id)
            ?: throw NoSuchElementException("Partner insurer with id ${command.id} not found")

        val oldStatus = partner.status.name
        applyStatusChange(partner, command.targetStatus, command.reason)
        val newStatus = partner.status.name

        val updated = repository.update(partner)
        if (!updated) error("Failed to update partner insurer status for id ${command.id}")

        if (partner.hasPendingEvents()) {
            domainEventPublisher.publish(partner.getDomainEvents())
            partner.clearDomainEvents()
        }

        ChangePartnerInsurerStatusResponseDto(
            id = partner.id.value,
            oldStatus = oldStatus,
            newStatus = newStatus,
            message = "Partner insurer status changed from $oldStatus to $newStatus"
        )
    }

    private fun applyStatusChange(
        partner: PartnerInsurer,
        target: PartnerInsurerStatus,
        reason: String?,
    ) {
        require(target != PartnerInsurerStatus.ONBOARDING) {
            throw IllegalArgumentException("Cannot roll back to onboarding status. MAINTENANCE should be used instead.")
        }

        return when (target) {
            PartnerInsurerStatus.ACTIVE -> {
                require(!partner.status.isActive()) { "Partner insurer is already active." }
                partner.activate(reason)
            }

            PartnerInsurerStatus.SUSPENDED -> {
                require(!partner.status.isSuspended()) { "Partner insurer is already suspended." }
                partner.suspend(requireNotNull(reason) { "Reason is required to suspend" })
            }

            PartnerInsurerStatus.MAINTENANCE -> {
                require(!partner.status.isInMaintenance()) { "Partner insurer is already in maintenance." }
                partner.putInMaintenance(requireNotNull(reason) { "Reason is required to put on maintenance" })
            }

            PartnerInsurerStatus.DEACTIVATED -> {
                require(!partner.status.isDeactivated()) { "Partner insurer is already deactivated." }
                partner.deactivate(requireNotNull(reason) { "Reason is required to deactivate" })
            }

            else -> {
                error("Invalid status transition")
            }
        }
    }
}
