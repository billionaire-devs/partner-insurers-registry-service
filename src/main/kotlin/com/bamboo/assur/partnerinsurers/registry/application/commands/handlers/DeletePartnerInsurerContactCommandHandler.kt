package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.DeletePartnerInsurerContactCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.ContactRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Service
class DeletePartnerInsurerContactCommandHandler(
    private val partnerInsurerQueryRepository: PartnerInsurerQueryRepository,
    private val contactRepository: ContactRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<DeletePartnerInsurerContactCommand, Unit> {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: DeletePartnerInsurerContactCommand) {
        logger.info("Deleting contact ${command.contactId} for partner insurer ${command.partnerInsurerId}")

        val partnerInsurerId = DomainEntityId(command.partnerInsurerId)
        val contactId = DomainEntityId(command.contactId)

        val partnerInsurer = partnerInsurerQueryRepository.findById(partnerInsurerId.value)
            ?: throw EntityNotFoundException(getAggregateTypeOrEmpty<PartnerInsurer>(), partnerInsurerId.value)

        // Verify that the contact exists and belongs to this partner insurer
        val existingContact = partnerInsurer.contacts.find { it.id == contactId }
            ?: throw EntityNotFoundException(getAggregateTypeOrEmpty<Contact>(), contactId.value)

        // Perform soft deletion through the domain (generates events)
        val deletedAt = partnerInsurer.deleteContact(contactId)

        // TODO: Use actual user ID when authentication context is available
        val deletedById = java.util.UUID.randomUUID()

        // Perform soft deletion through the repository (handles idempotency)
        contactRepository.delete(existingContact, deletedAt, deletedById)

        // Publish domain events
        with(partnerInsurer) {
            if (hasPendingEvents()) {
                domainEventPublisher.publish(getDomainEvents())
                clearDomainEvents()
            }
        }

        logger.info("Successfully deleted contact ${command.contactId} for partner insurer ${command.partnerInsurerId}")
    }
}