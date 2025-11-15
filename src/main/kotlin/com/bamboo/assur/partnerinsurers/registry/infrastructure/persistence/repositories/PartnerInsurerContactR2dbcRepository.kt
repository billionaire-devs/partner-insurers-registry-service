package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerContactTable
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Repository
interface PartnerInsurerContactR2dbcRepository : CoroutineCrudRepository<PartnerInsurerContactTable, UUID> {

    fun findByPartnerInsurerIdAndDeletedAtIsNull(
        @Param("partnerInsurerId") partnerInsurerId: UUID
    ): Flow<PartnerInsurerContactTable>

    suspend fun findByIdAndDeletedAtIsNotNull(@Param("id") id: UUID): PartnerInsurerContactTable?

    suspend fun findByIdAndDeletedAtIsNull(@Param("id") id: UUID): PartnerInsurerContactTable?

    suspend fun deleteByPartnerInsurerId(@Param("partnerInsurerId") partnerInsurerId: UUID)
}
