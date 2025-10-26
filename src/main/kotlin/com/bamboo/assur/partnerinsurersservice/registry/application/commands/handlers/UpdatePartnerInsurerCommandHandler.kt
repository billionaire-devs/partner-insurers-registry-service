package com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurersservice.core.application.CommandHandler
import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.registry.domain.valueObjects.TaxIdentificationNumber
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UpdatePartnerInsurerCommandHandler(
    private val partnerInsurerRepository: PartnerInsurerRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<UpdatePartnerInsurerCommand, Result<UUID>> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: UpdatePartnerInsurerCommand): Result<UUID> {
        logger.info("Updating partner insurer with id: {}", command.id)
        
        val existingPartner = partnerInsurerRepository.findById(command.id)
            ?: throw EntityNotFoundException("PartnerInsurer", command.id.toString())

        try {
            existingPartner.update(
                legalName = command.legalName,
                logoUrl = command.logoUrl?.let { Url(it) },
                address = command.address,
            )

            val isUpdated = partnerInsurerRepository.update(existingPartner)
            if (!isUpdated) {
                return Result.failure("Failed to update partner insurer with id: ${command.id}")
            }

            // Publish domain events
            if (existingPartner.hasPendingEvents()) {
                domainEventPublisher.publish(existingPartner.getDomainEvents())
                existingPartner.clearDomainEvents()
            }

            logger.info("Successfully updated partner insurer with id: {}", command.id)
            return Result.success(command.id)
        } catch (e: Exception) {
            logger.error("Error updating partner insurer with id: ${command.id}", e)
            return Result.failure("Error updating partner insurer: ${e.message}")
        }
    }
}
