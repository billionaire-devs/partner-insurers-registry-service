package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.Result
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdatePartnerInsurerCommandHandler(
    private val queryRepository: PartnerInsurerQueryRepository,
    private val commandRepository: PartnerInsurerCommandRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<UpdatePartnerInsurerCommand, Result<UUID>> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: UpdatePartnerInsurerCommand): Result<UUID> {
        logger.info("Updating partner insurer with id: {}", command.id)

        return try {
            val partialUpdate = command.toPartialUpdate()

            if (!partialUpdate.hasChanges()) {
                logger.info("No changes detected for partner insurer with id: {}", command.id)
                return Result.success(command.id)
            }

            // Use partial update to avoid fetching contacts and updating only changed fields
            val success = commandRepository.partialUpdate(command.id, partialUpdate)

            if (!success) {
                return Result.failure("Failed to update partner insurer with id: ${command.id}")
            }

            // For domain events, we need to get the updated entity to publish events
            val updatedPartner = queryRepository.findByIdForUpdate(command.id)
                ?: throw EntityNotFoundException("PartnerInsurer", command.id.toString())

            // Apply the partial update to trigger domain events
            updatedPartner.partialUpdate(partialUpdate)

            // Publish domain events
            if (updatedPartner.hasPendingEvents()) {
                domainEventPublisher.publish(updatedPartner.getDomainEvents())
                updatedPartner.clearDomainEvents()
            }

            logger.info("Successfully updated partner insurer with id: {}", command.id)
            Result.success(command.id)
        } catch (e: Exception) {
            logger.error("Error updating partner insurer with id: ${command.id}", e)
            Result.failure("Error updating partner insurer: ${e.message}")
        }
    }
}
