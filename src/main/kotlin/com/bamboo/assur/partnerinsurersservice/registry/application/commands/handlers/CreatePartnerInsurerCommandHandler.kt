package com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurersservice.registry.domain.repositories.PartnerInsurerRepository
import com.bamboo.assur.partnerinsurersservice.core.application.CommandHandler
import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.CreatePartnerInsurerCommand
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurersservice.registry.domain.valueObjects.TaxIdentificationNumber
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Service
class CreatePartnerInsurerCommandHandler(
    private val partnerInsurerRepository: PartnerInsurerRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<CreatePartnerInsurerCommand, Result<Uuid>> {

    @Transactional
    override suspend fun handle(command: CreatePartnerInsurerCommand): Result<Uuid> {
        require(partnerInsurerRepository.findByPartnerCode(command.partnerCode) == null) {
            "Partner Insurer already registered with this partner code"
        }

        return Result.of {
            val partnerInsurer = PartnerInsurer.create(
                legalName = command.legalName,
                partnerInsurerCode = command.partnerCode,
                taxIdentificationNumber = TaxIdentificationNumber(command.taxIdentificationNumber),
                logoUrl = command.logoUrl?.let { Url(it) },
                contacts = command.contacts,
                address = command.address,
                status = command.status,
            )

            val isSaved = partnerInsurerRepository.save(partnerInsurer)

            if (!isSaved) {
                error("Failed to save partner insurer")
            }

            // Publish events to transactional outbox
            if (partnerInsurer.hasPendingEvents()) {
                domainEventPublisher.publish(partnerInsurer.getDomainEvents())
                partnerInsurer.clearDomainEvents()
            }

            partnerInsurer.id.value
        }
    }
}
