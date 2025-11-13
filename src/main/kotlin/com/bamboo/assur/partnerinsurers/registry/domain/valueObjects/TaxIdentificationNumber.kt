package com.bamboo.assur.partnerinsurers.registry.domain.valueObjects

@JvmInline
value class TaxIdentificationNumber(val value: String) { // TODO(business validation rules for TaxIdentificationNumber)
    init {
        require(value.isNotBlank()) { "Tax Identification Number cannot be blank" }
    }
}