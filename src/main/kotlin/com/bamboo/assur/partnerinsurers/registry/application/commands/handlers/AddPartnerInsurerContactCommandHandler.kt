package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.AddPartnerInsurerContactCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.ContactRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.AddPartnerInsurerContactResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Email
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Service
class AddPartnerInsurerContactCommandHandler(
    private val partnerInsurerQueryRepository: PartnerInsurerQueryRepository,
    private val contactRepository: ContactRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<AddPartnerInsurerContactCommand, AddPartnerInsurerContactResponseDto> {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: AddPartnerInsurerContactCommand): AddPartnerInsurerContactResponseDto {
        logger.info("Adding contact for partner insurer {}", command.partnerInsurerId)

        // Fetch the partner insurer (full aggregate needed for business rule validation)
        val partnerInsurer = partnerInsurerQueryRepository.findById(command.partnerInsurerId.value)
            ?: throw EntityNotFoundException(
                getAggregateTypeOrEmpty<PartnerInsurer>(),
                command.partnerInsurerId.value
            )


        // Create the contact
        val contact = Contact.create(
            fullName = command.fullName,
            email = Email(command.email),
            phone = Phone(command.phone),
            contactRole = command.contactRole
        )

        partnerInsurer.addContact(contact)

        // Save the contact with partner insurer ID
        val saved = contactRepository.save(partnerInsurerId = command.partnerInsurerId.value, contact = contact)

        if (!saved) {
            logger.error(
                "Unexpected error while adding contact to partner insurer {}",
                command.partnerInsurerId,
            )
        }

        logger.info("Contact added successfully for partner insurer {}", command.partnerInsurerId)

        // Publish events to transactional outbox
        with(partnerInsurer) {
            if (hasPendingEvents()) {
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
        }

        return AddPartnerInsurerContactResponseDto(
            contactId = contact.id.value.toString(),
            partnerInsurerId = command.partnerInsurerId.value.toString(),
            fullName = contact.fullName,
            email = contact.email.value,
            phone = contact.phone.value,
            contactRole = contact.contactRole,
            createdAt = contact.createdAt.toJavaInstant()
                .atZone(ZoneId.of("Africa/Libreville"))
                .toOffsetDateTime(),
            )
    }
}