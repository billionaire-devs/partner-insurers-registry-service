package com.bamboo.assur.partnerinsurers.registry.domain.repositories

import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Suppress("TooManyFunctions")
interface PartnerInsurerCommandRepository {
    suspend fun save(partnerInsurer: PartnerInsurer): Boolean
    suspend fun update(partnerInsurer: PartnerInsurer): Boolean
    suspend fun delete(partnerInsurer: PartnerInsurer)
}
