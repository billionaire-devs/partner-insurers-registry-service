package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.application.commands.CreatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.CreatePartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.CreatePartnerInsurerResponseDto.Companion.toResponseDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Service
class CreatePartnerInsurerCommandHandler(
    private val partnerInsurerRepository: PartnerInsurerRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<CreatePartnerInsurerCommand, CreatePartnerInsurerResponseDto> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: CreatePartnerInsurerCommand): CreatePartnerInsurerResponseDto {
        logger.info("Creating partner insurer with code {}", command.partnerInsurerCode)
        val partnerInsurer = PartnerInsurer.create(
            legalName = command.legalName,
            partnerInsurerCode = command.partnerInsurerCode,
            taxIdentificationNumber = TaxIdentificationNumber(command.taxIdentificationNumber),
            logoUrl = command.logoUrl?.let { Url(it) },
            contacts = command.contacts,
            address = command.address,
            status = command.status,
        )

        try {
            partnerInsurerRepository.save(partnerInsurer)
            logger.info("Partner insurer persisted with id {}", partnerInsurer.id)

            // Publish events to transactional outbox (joins same transaction)
            if (partnerInsurer.hasPendingEvents()) {
                val events = partnerInsurer.getDomainEvents()
                logger.info(
                    "Publishing {} domain events for partner insurer {}",
                    events.size,
                    partnerInsurer.id
                )
                domainEventPublisher.publish(events)
                partnerInsurer.clearDomainEvents()
                logger.debug("Domain events cleared for partner insurer {}", partnerInsurer.id)
            }

            return partnerInsurer.toResponseDto()
        } catch (e: EntityAlreadyExistsException) {
            logger.warn(
                "Failed to create partner insurer with code {} due to duplicate entity",
                command.partnerInsurerCode,
                e
            )
            throw e
        } catch (e: Exception) {
            logger.error(
                "Unexpected error while creating partner insurer with code {}",
                command.partnerInsurerCode,
                e
            )
            throw IllegalStateException("Unable to create partner insurer", e)
        }
    }
}

