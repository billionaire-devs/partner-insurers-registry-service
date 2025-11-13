package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.DeletePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.DeletePartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.DeletePartnerInsurerResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePartnerInsurerCommandHandler(
    private val queryRepository: PartnerInsurerQueryRepository,
    private val commandRepository: PartnerInsurerCommandRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<DeletePartnerInsurerCommand, DeletePartnerInsurerResponseDto> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: DeletePartnerInsurerCommand): DeletePartnerInsurerResponseDto {
        val partnerInsurer = queryRepository.findById(command.id)
            ?: throw NoSuchElementException("PartnerInsurer not found with id: ${command.id}")

        // This may throw InvalidOperationException - let it propagate to caller
        // TODO: Pass actual user ID when authentication context is available
        partnerInsurer.delete(command.reason, deletedBy = null)

        val updateSuccessful = try {
            commandRepository.update(partnerInsurer)
        } catch (e: Exception) {
            logger.error("Error updating partner insurer with id: ${command.id}", e)
            throw FailedToUpdateEntityException(
                getAggregateTypeOrEmpty<PartnerInsurer>(),
                command.id
            )
        }

        if (!updateSuccessful) {
            logger.error("Failed to update partner insurer with id: ${command.id}")
            throw FailedToUpdateEntityException(
                getAggregateTypeOrEmpty<PartnerInsurer>(),
                command.id
            )
        }

        with(partnerInsurer) {
            if (hasPendingEvents()) {
                domainEventPublisher.publish(getDomainEvents())
                clearDomainEvents()
            }
        }

        return partnerInsurer.toResponseDto()
    }
}