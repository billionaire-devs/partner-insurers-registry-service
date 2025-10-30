package com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurersservice.registry.application.queries.models.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurersservice.registry.infrastructure.persistence.entities.PartnerInsurerTable
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Repository
interface PartnerInsurerR2dbcRepository : CoroutineCrudRepository<PartnerInsurerTable, UUID> {

    @Suppress("LongParameterList")
    @Query("""
        SELECT id, partner_insurer_code, legal_name, tax_identification_number, status, logo_url, address
        FROM partner_insurers
        WHERE (:status IS NULL OR status::text = :status)
            AND (:search IS NULL OR search_vector @@ plainto_tsquery('french', :search) 
            OR LOWER(legal_name) LIKE LOWER(CONCAT('%', :search, '%')) 
            OR LOWER(partner_insurer_code) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY
            CASE WHEN :sortDirection = 'DESC' THEN
                CASE
                    WHEN :sortBy = 'legalName' THEN legal_name
                    WHEN :sortBy = 'partnerInsurerCode' THEN partner_insurer_code
                    WHEN :sortBy = 'status' THEN status::text
                    ELSE legal_name
                END
            END DESC,
            CASE WHEN :sortDirection = 'ASC' OR :sortDirection IS NULL THEN
                CASE
                    WHEN :sortBy = 'legalName' THEN legal_name
                    WHEN :sortBy = 'partnerInsurerCode' THEN partner_insurer_code
                    WHEN :sortBy = 'status' THEN status::text
                    ELSE legal_name
                END
            END
        LIMIT :size OFFSET :offset
    """)
    suspend fun findAll(
        status: String?,
        search: String?,
        offset: Int,
        size: Int,
        sortBy: String?,
        sortDirection: String,
    ): Flow<PartnerInsurerSummary>

    suspend fun findByPartnerInsurerCode(partnerCode: String): PartnerInsurerTable?
    suspend fun existsByPartnerInsurerCode(partnerCode: String): Boolean
    suspend fun existsByTaxIdentificationNumber(taxIdentificationNumber: String): Boolean
}
