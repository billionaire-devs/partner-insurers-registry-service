package com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@OptIn(ExperimentalUuidApi::class)
@Serializable
@JvmInline
value class DomainEntityId(val value: Uuid) {
    companion object {
        fun random(): DomainEntityId = DomainEntityId(Uuid.random())

        fun fromString(uuidString: String): DomainEntityId {
            require(uuidString.isNotEmpty()) { "UUID string cannot be empty" }
            return DomainEntityId(Uuid.parse(uuidString))
        }
    }
}