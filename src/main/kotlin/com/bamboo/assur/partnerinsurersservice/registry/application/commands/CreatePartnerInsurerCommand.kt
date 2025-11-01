package com.bamboo.assur.partnerinsurersservice.registry.application.commands

import com.bamboo.assur.partnerinsurersservice.core.application.Command
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.registry.domain.entities.Contact
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus

data class CreatePartnerInsurerCommand(
    val partnerCode: String,
    val legalName: String,
    val taxIdentificationNumber: String,
    val logoUrl: String?,
    val contacts: Set<Contact>,
    val address: Address,
    val status: PartnerInsurerStatus,
): Command
