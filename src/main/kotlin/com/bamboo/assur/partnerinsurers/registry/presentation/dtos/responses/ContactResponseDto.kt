package com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses

import com.bamboo.assur.partnerinsurers.registry.domain.entities.Contact
import kotlin.time.ExperimentalTime

data class ContactResponseDto(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val createdAt: String,
) {
    companion object {
        @OptIn(ExperimentalTime::class)
        fun Contact.toResponseDto() = ContactResponseDto(
            id = this.id.value.toString(),
            fullName = this.fullName,
            email = this.email.value,
            phone = this.phone.value,
            createdAt = this.createdAt.toString()
        )
    }
}
