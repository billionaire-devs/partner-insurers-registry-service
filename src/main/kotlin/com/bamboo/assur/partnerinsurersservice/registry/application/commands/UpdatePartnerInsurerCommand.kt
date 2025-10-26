package com.bamboo.assur.partnerinsurersservice.registry.application.commands

import com.bamboo.assur.partnerinsurersservice.core.application.Command
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import java.util.UUID

data class UpdatePartnerInsurerCommand(
    val id: UUID,
    val legalName: String,
    val logoUrl: String?,
    val address: Address,
) : Command
