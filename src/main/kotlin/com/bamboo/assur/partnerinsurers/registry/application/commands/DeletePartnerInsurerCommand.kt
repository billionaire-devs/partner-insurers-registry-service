package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import java.util.*

data class DeletePartnerInsurerCommand(
    val id: UUID,
    val reason: String?,
) : Command