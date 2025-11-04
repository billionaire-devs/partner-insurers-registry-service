package com.bamboo.assur.partnerinsurers.registry.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.registry.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurers.registry.registry.domain.enums.PartnerInsurerStatus

data class CreatePartnerInsurerCommand(
    val partnerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val logoUrl: String?,
    val contacts: Set<Contact>,
    val address: Address,
    val status: PartnerInsurerStatus,
): Command
