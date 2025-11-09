package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address

data class CreatePartnerInsurerCommand(
    val partnerInsurerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val logoUrl: String?,
    val contacts: Set<com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact>,
    val address: Address,
    val status: com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus,
) : Command
