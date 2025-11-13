package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.UpdatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerCommandRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.FailedToUpdateEntityException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePartnerInsurerCommandHandler(
    private val queryRepository: PartnerInsurerQueryRepository,
    private val commandRepository: PartnerInsurerCommandRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<UpdatePartnerInsurerCommand, UpdatePartnerInsurerResponseDto> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: UpdatePartnerInsurerCommand): UpdatePartnerInsurerResponseDto {
        require(command.hasChanges()) { "Command has no changes" }

        val partnerInsurer = queryRepository.findById(command.id)
            ?: throw NoSuchElementException("PartnerInsurer not found with id: ${command.id}")

        partnerInsurer.update(
            legalName = command.legalName,
            logoUrl = command.logoUrl?.let { Url(it) },
            address = command.address
        )

        try {
            commandRepository.update(partnerInsurer)
        } catch (e: Exception) {
            logger.error("Error updating partner insurer with id: ${command.id}", e)
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
