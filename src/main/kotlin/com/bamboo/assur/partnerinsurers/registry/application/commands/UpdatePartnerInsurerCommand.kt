package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurers.registry.application.commands.models.PartnerInsurerUpdate
import java.util.*

data class UpdatePartnerInsurerCommand(
    val id: UUID,
    val legalName: String?,
    val logoUrl: String?,
    val address: Address?,
) : Command {
    fun toPartialUpdate(): com.bamboo.assur.partnerinsurers.registry.application.commands.models.PartnerInsurerUpdate {
        return _root_ide_package_.com.bamboo.assur.partnerinsurers.registry.application.commands.models.PartnerInsurerUpdate(
            legalName = legalName,
            logoUrl = logoUrl?.let { Url(it) },
            address = address
        )
    }
}
