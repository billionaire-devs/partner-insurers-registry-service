package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class DeletePartnerInsurerContactCommand(
    val partnerInsurerId: UUID,
    val contactId: UUID,
) : Command