package com.bamboo.assur.partnerinsurers.registry.registry.presentation.dtos.requests

import com.bamboo.assur.partnerinsurers.registry.registry.application.commands.ChangePartnerInsurerStatusCommand
import com.bamboo.assur.partnerinsurers.registry.registry.domain.enums.PartnerInsurerStatus
import jakarta.validation.constraints.NotBlank
import java.util.UUID

/**
 * Request body for changing a partner insurer status.
 */
data class ChangePartnerInsurerStatusRequestDto(
    @field:NotBlank(message = "Target status cannot be blank")
    val targetStatus: String,
    val reason: String?,
) {

    fun toCommand(id: UUID): ChangePartnerInsurerStatusCommand {

        val status = try {
            PartnerInsurerStatus.valueOf(targetStatus)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Invalid target status: $targetStatus. Allowed values are: ${
                    PartnerInsurerStatus.entries.joinToString(", ") { it.name }
                }",
                e
            )
        }

        return ChangePartnerInsurerStatusCommand(
            id = id,
            targetStatus = status,
            reason = reason
        )
    }
}
