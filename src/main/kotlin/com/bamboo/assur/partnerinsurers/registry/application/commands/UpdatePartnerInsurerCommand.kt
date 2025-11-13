package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import java.util.*

data class UpdatePartnerInsurerCommand(
    val id: UUID,
    val legalName: String?,
    val logoUrl: String?,
    val address: Address?,
) : Command {

    /**
     * Checks if this update contains any actual changes.
     */
    fun hasChanges(): Boolean = legalName != null || logoUrl != null || address != null
}
