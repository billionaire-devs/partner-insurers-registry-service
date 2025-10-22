package com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects

import java.net.URI

@JvmInline
value class Url(val value: String) {
    init {
        require(value.isNotBlank()) { "URL cannot be blank." }
        require(isValidUrl(value)) { "Invalid URL format." }
    }

    private fun isValidUrl(url: String) = try {
        URI.create(url).toURL()
        true
    } catch (_: Exception) {
        false
    }
}