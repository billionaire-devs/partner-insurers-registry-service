package com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories

import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerSummary
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.entities.PartnerInsurerTable
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

    @Query(
        """
        SELECT id, partner_insurer_code, tax_identification_number, legal_name, status, created_at, updated_at
        FROM partner_insurers
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun findByIdSummary(id: UUID): PartnerInsurerProjection.SummaryProjection

    @Query(
        """
        SELECT id, partner_insurer_code, tax_identification_number, legal_name, status, created_at, updated_at
        FROM partner_insurers
        WHERE partner_insurer_code = :partnerCode
        LIMIT 1
        """
    )
    suspend fun findByPartnerCodeSummary(partnerCode: String): PartnerInsurerProjection.SummaryProjection?

    @Query(
        """
        SELECT id, partner_insurer_code, tax_identification_number, legal_name, status, logo_url, address, created_at, updated_at
        FROM partner_insurers
        WHERE tax_identification_number = :taxIdentificationNumber
        LIMIT 1;
        """
    )
    suspend fun findByTaxIdentificationNumberSummary(
        taxIdentificationNumber: String
    ): PartnerInsurerProjection.SummaryProjection?


    @Query(
        """
            SELECT pi.id, pi.partner_insurer_code, pi.tax_identification_number, pi.legal_name, pi.status, pi.logo_url, pi.address, pi.created_at, pi.updated_at
            FROM partner_insurers as pi
            WHERE id = :id
            LIMIT 1
        """
    )
    suspend fun findByIdDetailed(id: UUID): PartnerInsurerProjection.DetailedProjection?

    @Query(
        """
            SELECT pi.id, pi.partner_insurer_code, pi.tax_identification_number, pi.legal_name, pi.status, pi.logo_url, pi.address, pi.created_at, pi.updated_at
            FROM partner_insurers as pi
            WHERE partner_insurer_code = :partnerCode
            LIMIT 1
        """
    )
    suspend fun findByPartnerCodeDetailed(
        partnerCode: String
    ): PartnerInsurerProjection.DetailedProjection?

    @Query(
        """
            SELECT pi.id, pi.partner_insurer_code, pi.tax_identification_number, pi.legal_name, pi.status, pi.logo_url, pi.address, pi.created_at, pi.updated_at
            FROM partner_insurers as pi
            WHERE tax_identification_number = :taxIdentificationNumber
            LIMIT 1;
        """
    )
    suspend fun findByTaxIdentificationNumberDetailed(
        taxIdentificationNumber: String
    ): PartnerInsurerProjection.DetailedProjection?

    suspend fun findByPartnerInsurerCode(partnerCode: String): PartnerInsurerTable?
    suspend fun existsByPartnerInsurerCode(partnerCode: String): Boolean
    suspend fun existsByTaxIdentificationNumber(taxIdentificationNumber: String): Boolean
    fun findByTaxIdentificationNumber(taxIdentificationNumber: String): PartnerInsurerTable
}
