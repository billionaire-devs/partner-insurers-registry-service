package com.bamboo.assur.partnerinsurersservice.registry.application.commands

import com.bamboo.assur.partnerinsurersservice.core.application.Command
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.models.PartnerInsurerUpdate
import java.util.UUID

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
