package com.bamboo.assur.partnerinsurers.registry.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurers.registry.registry.application.commands.models.PartnerInsurerUpdate
import java.util.*

data class UpdatePartnerInsurerCommand(
    val id: UUID,
    val legalName: String?,
    val logoUrl: String?,
    val address: Address?,
) : Command {
    fun toPartialUpdate(): PartnerInsurerUpdate {
        return PartnerInsurerUpdate(
            legalName = legalName,
            logoUrl = logoUrl?.let { Url(it) },
            address = address
        )
    }
}
