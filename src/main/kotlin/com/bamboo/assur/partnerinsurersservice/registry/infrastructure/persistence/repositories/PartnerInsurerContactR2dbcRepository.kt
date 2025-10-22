package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerContactTable
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Repository
interface PartnerInsurerContactR2dbcRepository : CoroutineCrudRepository<PartnerInsurerContactTable, Uuid> {

//    @Query(
//        """
//        SELECT * FROM partner_insurer_contacts WHERE partner_insurer_id = :partnerInsurerId
//        """
//    )
    fun findByPartnerInsurerId(@Param("partnerInsurerId") partnerInsurerId: Uuid): Flow<PartnerInsurerContactTable>

//    @Query(
//        """
//        DELETE FROM partner_insurer_contacts WHERE partner_insurer_id = :partnerInsurerId
//        """
//    )
//    @Modifying
    suspend fun deleteByPartnerInsurerId(@Param("partnerInsurerId") partnerInsurerId: Uuid)
}
