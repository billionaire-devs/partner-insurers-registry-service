package com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.core.application.CommandHandler
import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.CreatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.valueObjects.TaxIdentificationNumber
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Service
class CreatePartnerInsurerCommandHandler(
    private val partnerInsurerRepository: PartnerInsurerRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<CreatePartnerInsurerCommand, Result<UUID>> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: CreatePartnerInsurerCommand): Result<UUID> {
        if (partnerInsurerRepository.existsByPartnerCode(command.partnerCode)) {
            throw EntityAlreadyExistsException(
                "PartnerInsurer",
                entityIdentifierName = "Partner Code",
                entityIdentifier = command.partnerCode
            )
        }

        logger.info("Creating partner insurer: {}", command)
        val partnerInsurer = PartnerInsurer.create(
            legalName = command.legalName,
            partnerInsurerCode = command.partnerCode,
            taxIdentificationNumber = TaxIdentificationNumber(command.taxIdentificationNumber),
            logoUrl = command.logoUrl?.let { Url(it) },
            contacts = command.contacts,
            address = command.address,
            status = command.status,
        )

        // Let exceptions propagate to trigger rollback
        val isSaved = partnerInsurerRepository.save(partnerInsurer)
        logger.info("Partner insurer saved: {}", isSaved)

        if (!isSaved) {
            error("Partner insurer could not have been saved")
        }

        // Publish events to transactional outbox (joins same transaction)
        logger.info("Checking for pending events")
        if (partnerInsurer.hasPendingEvents()) {
            logger.info("Publishing events to transactional outbox")
            domainEventPublisher.publish(partnerInsurer.getDomainEvents())
            logger.info("Events published to transactional outbox")
            logger.info("Clearing domain events")
            partnerInsurer.clearDomainEvents()
            logger.info("Domain events cleared")
        }

        return Result.success(partnerInsurer.id.value)
    }
}

