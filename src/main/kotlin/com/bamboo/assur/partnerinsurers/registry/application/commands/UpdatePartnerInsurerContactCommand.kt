package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import java.util.UUID

data class UpdatePartnerInsurerContactCommand(
    val partnerInsurerId: UUID,
    val contactId: UUID,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val contactRole: String?
) : Command
