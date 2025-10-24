package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerContactTable
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Repository
interface PartnerInsurerContactR2dbcRepository : CoroutineCrudRepository<PartnerInsurerContactTable, UUID> {

//    @Query(
//        """
//        SELECT * FROM partner_insurer_contacts WHERE partner_insurer_id = :partnerInsurerId
//        """
//    )
    fun findByPartnerInsurerId(@Param("partnerInsurerId") partnerInsurerId: UUID): Flow<PartnerInsurerContactTable>

//    @Query(
//        """
//        DELETE FROM partner_insurer_contacts WHERE partner_insurer_id = :partnerInsurerId
//        """
//    )
//    @Modifying
    suspend fun deleteByPartnerInsurerId(@Param("partnerInsurerId") partnerInsurerId: UUID)
}
