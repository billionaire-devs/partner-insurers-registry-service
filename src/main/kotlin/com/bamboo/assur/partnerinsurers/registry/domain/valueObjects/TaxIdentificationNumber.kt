package com.bamboo.assur.partnerinsurers.registry.domain.valueObjects

@JvmInline
value class TaxIdentificationNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Tax Identification Number cannot be blank" }
    }
}