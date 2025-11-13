package com.bamboo.assur.partnerinsurers.registry.application.commands

import com.bamboo.assur.partnerinsurers.sharedkernel.application.Command
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import java.util.UUID

/**
 * Command to change the status of a Partner Insurer.
 */
data class ChangePartnerInsurerStatusCommand(
    val id: UUID,
    val targetStatus: com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus,
    val reason: String?
): Command
