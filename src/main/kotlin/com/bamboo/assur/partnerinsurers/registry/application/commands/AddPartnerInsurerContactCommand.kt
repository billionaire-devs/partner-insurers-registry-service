package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId

data class AddPartnerInsurerContactCommand(
    val partnerInsurerId: DomainEntityId,
    val fullName: String,
    val email: String,
    val phone: String,
    val contactRole: String,
) : Command