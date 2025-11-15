package com.bamboo.assur.partnerinsurers.registry.application.commands.handlers

import com.bamboo.assur.partnerinsurers.registry.application.commands.UpdatePartnerInsurerContactCommand
import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.ContactRepository
import com.bamboo.assur.partnerinsurers.registry.domain.repositories.PartnerInsurerQueryRepository
import com.bamboo.assur.partnerinsurers.registry.infrastructure.events.DomainEventPublisher
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerContactResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.CommandHandler
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.getAggregateTypeOrEmpty
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Email
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Phone
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@OptIn(ExperimentalTime::class)
@Service
class UpdatePartnerInsurerContactCommandHandler(
    private val partnerInsurerQueryRepository: PartnerInsurerQueryRepository,
    private val contactRepository: ContactRepository,
    private val domainEventPublisher: DomainEventPublisher,
) : CommandHandler<UpdatePartnerInsurerContactCommand, UpdatePartnerInsurerContactResponseDto> {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override suspend fun invoke(command: UpdatePartnerInsurerContactCommand): UpdatePartnerInsurerContactResponseDto {
        logger.info("Updating contact ${command.contactId} for partner insurer ${command.partnerInsurerId}")

        val partnerInsurerId = DomainEntityId(command.partnerInsurerId)
        val contactId = DomainEntityId(command.contactId)

        val partnerInsurer = partnerInsurerQueryRepository.findById(partnerInsurerId.value)
            ?: throw EntityNotFoundException(getAggregateTypeOrEmpty<PartnerInsurer>(), partnerInsurerId.value)

        // The PartnerInsurer aggregate holds the contacts, so we can find it there.
        val existingContact = partnerInsurer.contacts.find { it.id == contactId }
            ?: throw EntityNotFoundException(getAggregateTypeOrEmpty<Contact>(), contactId.value)

        val (updatedContact, updatedFields) = existingContact.update(
            fullName = command.fullName,
            email = command.email?.let { Email(it) },
            phone = command.phone?.let { Phone(it) },
            contactRole = command.contactRole
        )

        if (updatedFields.isNotEmpty()) {
            partnerInsurer.updateContact(
                contactId = contactId,
                fullName = command.fullName,
                email = command.email?.let { Email(it) },
                phone = command.phone?.let { Phone(it) },
                contactRole = command.contactRole
            )

            contactRepository.update(updatedContact, partnerInsurer.id.value)

            with(partnerInsurer) {
                if (hasPendingEvents()) {
                    domainEventPublisher.publish(getDomainEvents())
                    clearDomainEvents()
                }
            }
        }

        return UpdatePartnerInsurerContactResponseDto(
            contactId = updatedContact.id.value,
            partnerInsurerId = partnerInsurer.id.value,
            fullName = updatedContact.fullName,
            email = updatedContact.email.value,
            phone = updatedContact.phone.value,
            contactRole = updatedContact.contactRole,
            createdAt = updatedContact.createdAt
                .toJavaInstant()
                .atZone(ZoneId.of("Africa/Libreville"))
                .toOffsetDateTime(),
            updatedAt = updatedContact.updatedAt
                .toJavaInstant()
                .atZone(ZoneId.of("Africa/Libreville"))
                .toOffsetDateTime(),
        )
    }
}
