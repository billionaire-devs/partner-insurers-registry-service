package com.bamboo.assur.partnerinsurers.registry.presentation.controllers

import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.ChangePartnerInsurerStatusCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.CreatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.DeletePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.UpdatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurerQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurersQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerDetailedProjectionQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerSummaryProjectionQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurersQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurersSummariesQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerSortTerm
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.AddressDto.Companion.toResponseDTO
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.GetPartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryView
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.time.ZoneId
import java.util.*

@ExtendWith(MockitoExtension::class)
class PartnerInsurerControllerTest {

    @Mock private lateinit var createCommandHandler: CreatePartnerInsurerCommandHandler
    @Mock
    private lateinit var getPartnerInsurersQueryHandler: GetPartnerInsurersQueryHandler
    @Mock private lateinit var getPartnerInsurerSummaryProjectionQueryHandler: GetPartnerInsurerSummaryProjectionQueryHandler
    @Mock private lateinit var getPartnerInsurerDetailedProjectionQueryHandler: GetPartnerInsurerDetailedProjectionQueryHandler
    @Mock private lateinit var getFullPartnerInsurerQueryHandler: GetPartnerInsurerQueryHandler
    @Mock private lateinit var changeStatusCommandHandler: ChangePartnerInsurerStatusCommandHandler
    @Mock private lateinit var updatePartnerInsurerCommandHandler: UpdatePartnerInsurerCommandHandler
    @Mock
    private lateinit var deletePartnerInsurerCommandHandler: DeletePartnerInsurerCommandHandler
    @Mock
    private lateinit var getPartnerInsurersSummariesQueryHandler: GetPartnerInsurersSummariesQueryHandler

    @InjectMocks
    private lateinit var controller: PartnerInsurerController

    private val partnerId = UUID.fromString("f30c5b74-ba15-4abe-9d9c-3ce635b8286e")
    private val partnerCode = "AXA-GA"
    private val taxIdentificationNumber = "GA-123456"
    private val now = Instant.parse("2025-01-01T10:15:30Z")
    private val jsonAddress: JsonElement = Json.parseToJsonElement(
        """{"street":"Boulevard","city":"Libreville","country":"GA"}"""
    )

    @Nested
    inner class GetById {

        @Test
        fun `should return summary projection`() = runTest {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ById(partnerId))
            val summary = PartnerInsurerProjection.SummaryProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                legalName = "AXA Gabon",
                status = PartnerInsurerStatus.ACTIVE,
                createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
            )

            `when`(getPartnerInsurerSummaryProjectionQueryHandler.invoke(query)).thenReturn(summary)

            val response = controller.getPartnerInsurerById(partnerId, QueryView.SUMMARY)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.SummaryProjection
            assertEquals(partnerCode, body.partnerInsurerCode)
            assertEquals("AXA Gabon", body.legalName)
            assertEquals(PartnerInsurerStatus.ACTIVE, body.status)
        }

        @Test
        fun `should return detailed projection`() = runTest {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ById(partnerId))
            val detailed = PartnerInsurerProjection.FullProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                status = PartnerInsurerStatus.ACTIVE.name,
                logoUrl = Url("https://example.com/logo.png"),
                address = jsonAddress,
                contacts = emptySet(),
                agreementsSummary = emptySet(),
                createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                updatedAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
            )

            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenReturn(detailed)

            val response = controller.getPartnerInsurerById(partnerId, QueryView.DETAILED)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.FullProjection
            assertEquals("AXA Gabon", body.legalName)
            assertEquals(PartnerInsurerStatus.ACTIVE.name, body.status)
            assertEquals(jsonAddress, body.address)
        }

        @Test
        fun `should return full projection`() = runTest {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ById(partnerId))
            val partner = PartnerInsurer.reconstitute(
                id = DomainEntityId(partnerId),
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                logoUrl = Url("https://example.com/logo.png"),
                contacts = emptySet(),
                address = Address("Boulevard", "Libreville", "GA", "BP 001"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet(),
            )

            `when`(getFullPartnerInsurerQueryHandler.invoke(query)).thenReturn(partner)

            val response = controller.getPartnerInsurerById(partnerId, QueryView.FULL)

            assertEquals(200, response.statusCode.value())
            val body = response.body as GetPartnerInsurerResponseDto
            assertEquals(taxIdentificationNumber, body.taxIdentificationNumber)
            assertEquals("AXA Gabon", body.legalName)
            assertEquals(partnerCode, body.partnerInsurerCode)
        }

        @Test
        suspend fun `should throw when partner not found`() {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ById(partnerId))
            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenAnswer {
                throw EntityNotFoundException(
                    "PartnerInsurer",
                    partnerId
                )
            }

            assertThrows<EntityNotFoundException> {
                controller.getPartnerInsurerById(partnerId, QueryView.DETAILED)
            }
        }
    }

    @Nested
    inner class GetByPartnerCode {

        @Test
        fun `should delegate to summary handler`() = runTest {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ByPartnerCode(partnerCode))
            val projection = PartnerInsurerProjection.SummaryProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                legalName = "AXA Gabon",
                status = PartnerInsurerStatus.ACTIVE,
                createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
            )

            `when`(getPartnerInsurerSummaryProjectionQueryHandler.invoke(query)).thenReturn(projection)

            val response = controller.getPartnerInsurerByPartnerCode(partnerCode, QueryView.SUMMARY)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.SummaryProjection
            assertEquals(partnerCode, body.partnerInsurerCode)
        }

        @Test
        fun `should delegate to detailed handler`() = runTest {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ByPartnerCode(partnerCode))
            val projection = PartnerInsurerProjection.FullProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                status = PartnerInsurerStatus.ACTIVE.name,
                logoUrl = Url("https://example.com/logo.png"),
                address = jsonAddress,
                contacts = emptySet(),
                agreementsSummary = emptySet(),
                createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                updatedAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
            )

            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenReturn(projection)

            val response = controller.getPartnerInsurerByPartnerCode(partnerCode, QueryView.DETAILED)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.FullProjection
            assertEquals("AXA Gabon", body.legalName)
        }

        @Test
        fun `should delegate to full handler`() = runTest {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ByPartnerCode(partnerCode))
            val domain = PartnerInsurer.reconstitute(
                id = DomainEntityId(partnerId),
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                logoUrl = Url("https://example.com/logo.png"),
                contacts = emptySet(),
                address = Address("Boulevard", "Libreville", "GA", "BP 001"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet(),
            )

            `when`(getFullPartnerInsurerQueryHandler.invoke(query)).thenReturn(domain)

            val response = controller.getPartnerInsurerByPartnerCode(partnerCode, QueryView.FULL)

            assertEquals(200, response.statusCode.value())
            val body = response.body as GetPartnerInsurerResponseDto
            assertEquals("AXA Gabon", body.legalName)
        }

        @Test
        suspend fun `should throw when partner code unknown`() {
            val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ByPartnerCode(partnerCode))
            `when`(getPartnerInsurerSummaryProjectionQueryHandler.invoke(query)).thenAnswer {
                throw EntityNotFoundException(
                    "PartnerInsurer",
                    partnerCode
                )
            }

            assertThrows<EntityNotFoundException> {
                controller.getPartnerInsurerByPartnerCode(partnerCode, QueryView.SUMMARY)
            }
        }
    }

    @Nested
    inner class GetByTaxIdentificationNumber {

        @Test
        fun `should return summary projection`() = runTest {
            val query = GetPartnerInsurerQuery(
                GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber(taxIdentificationNumber)
            )
            val projection = PartnerInsurerProjection.SummaryProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                legalName = "AXA Gabon",
                status = PartnerInsurerStatus.ACTIVE,
                createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
            )

            `when`(getPartnerInsurerSummaryProjectionQueryHandler.invoke(query)).thenReturn(projection)

            val response = controller.getPartnerInsurerByTaxIdentificationNumber(taxIdentificationNumber, QueryView.SUMMARY)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.SummaryProjection
            assertEquals(taxIdentificationNumber, body.taxIdentificationNumber.value)
        }

        @Test
        fun `should return detailed projection`() = runTest {
            val query = GetPartnerInsurerQuery(
                GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber(taxIdentificationNumber)
            )
            val projection = PartnerInsurerProjection.FullProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                status = PartnerInsurerStatus.ACTIVE.name,
                logoUrl = Url("https://example.com/logo.png"),
                address = jsonAddress,
                contacts = emptySet(),
                agreementsSummary = emptySet(),
                createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                updatedAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
            )

            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenReturn(projection)

            val response = controller.getPartnerInsurerByTaxIdentificationNumber(taxIdentificationNumber, QueryView.DETAILED)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.FullProjection
            assertEquals("ACTIVE", body.status)
        }

        @Test
        fun `should return full projection`() = runTest {
            val query = GetPartnerInsurerQuery(
                GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber(taxIdentificationNumber)
            )
            val domain = PartnerInsurer.reconstitute(
                id = DomainEntityId(partnerId),
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                logoUrl = Url("https://example.com/logo.png"),
                contacts = emptySet(),
                address = Address("Boulevard", "Libreville", "GA", "BP 001"),
                status = PartnerInsurerStatus.ACTIVE,
                brokerPartnerInsurerAgreements = emptySet(),
            )

            `when`(getFullPartnerInsurerQueryHandler.invoke(query)).thenReturn(domain)

            val response = controller.getPartnerInsurerByTaxIdentificationNumber(taxIdentificationNumber, QueryView.FULL)

            assertEquals(200, response.statusCode.value())
            val body = response.body as GetPartnerInsurerResponseDto
            assertEquals(partnerCode, body.partnerInsurerCode)
            assertEquals("AXA Gabon", body.legalName)
            assertTrue(body.contacts.isEmpty())
        }

        @Test
        suspend fun `should throw when tax id unknown`() {
            val query = GetPartnerInsurerQuery(
                GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber(taxIdentificationNumber)
            )
            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenAnswer {
                throw EntityNotFoundException(
                    "PartnerInsurer",
                    taxIdentificationNumber
                )
            }

            assertThrows<EntityNotFoundException> {
                controller.getPartnerInsurerByTaxIdentificationNumber(taxIdentificationNumber, QueryView.DETAILED)
            }
        }
    }

    @Nested
    inner class GetPartnerInsurers {

        @Test
        fun `should return summary projections with default parameters`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                    legalName = "AXA Gabon",
                    status = PartnerInsurerStatus.ACTIVE,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(query)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }

        @Test
        fun `should return detailed projections with custom parameters`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.SUSPENDED,
                search = "AXA",
                page = 1,
                size = 10,
                sort = PartnerInsurerSortTerm.LEGAL_NAME,
                sortDirection = SortDirection.DESC,
                createdBefore = "2025-12-31",
                createdAfter = "2025-01-01",
                view = QueryView.DETAILED
            )

            val detailedProjections = flowOf(
                GetPartnerInsurerResponseDto(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = taxIdentificationNumber,
                    legalName = "AXA Gabon",
                    status = PartnerInsurerStatus.SUSPENDED.name,
                    logoUrl = "https://example.com/logo.png",
                    address = Address("Boulevard", "Libreville", "GA", "BP 001").toResponseDTO(),
                    contacts = emptyList(),
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime()
                )
            )

            `when`(getPartnerInsurersQueryHandler.invoke(query)).thenReturn(detailedProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.SUSPENDED,
                search = "AXA",
                page = 1,
                size = 10,
                sort = PartnerInsurerSortTerm.LEGAL_NAME,
                sortDirection = SortDirection.DESC,
                createdBefore = "2025-12-31",
                createdAfter = "2025-01-01",
                view = QueryView.DETAILED
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(detailedProjections, response.body)
        }

        @Test
        fun `should handle search parameter correctly`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.ACTIVE,
                search = "Allianz",
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = "ALLIANZ-GA",
                    taxIdentificationNumber = TaxIdentificationNumber("GA-654321"),
                    legalName = "Allianz Gabon",
                    status = PartnerInsurerStatus.ACTIVE,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(query)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.ACTIVE,
                search = "Allianz",
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }

        @Test
        fun `should handle sorting parameters correctly`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = PartnerInsurerSortTerm.CREATED_AT,
                sortDirection = SortDirection.DESC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                    legalName = "AXA Gabon",
                    status = PartnerInsurerStatus.ACTIVE,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(query)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = PartnerInsurerSortTerm.CREATED_AT,
                sortDirection = SortDirection.DESC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }

        @Test
        fun `should throw IllegalArgumentException for invalid view`() = runTest {
            assertThrows<IllegalArgumentException> {
                controller.getPartnerInsurers(
                    status = PartnerInsurerStatus.ACTIVE,
                    search = null,
                    page = 0,
                    size = 20,
                    sort = null,
                    sortDirection = SortDirection.ASC,
                    createdBefore = null,
                    createdAfter = null,
                    view = QueryView.FULL
                )
            }
        }

        @Test
        fun `should handle date filtering correctly`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = "2025-06-30",
                createdAfter = "2025-01-01",
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                    legalName = "AXA Gabon",
                    status = PartnerInsurerStatus.ACTIVE,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(query)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = "2025-06-30",
                createdAfter = "2025-01-01",
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }

        @Test
        fun `should handle different status filters`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.SUSPENDED,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                    legalName = "Suspended Partner",
                    status = PartnerInsurerStatus.SUSPENDED,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(query)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.SUSPENDED,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }

        @Test
        fun `should handle null status parameter`() = runTest {
            val query = GetPartnerInsurersQuery(
                status = null,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                    legalName = "AXA Gabon",
                    status = PartnerInsurerStatus.ACTIVE,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(query)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = null,
                search = null,
                page = 0,
                size = 20,
                sort = null,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }

        @Test
        fun `should handle all sorting terms`() = runTest {
            val statusQuery = GetPartnerInsurersQuery(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = PartnerInsurerSortTerm.STATUS,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            val summaryProjections = flowOf(
                PartnerInsurerProjection.SummaryProjection(
                    id = partnerId,
                    partnerInsurerCode = partnerCode,
                    taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                    legalName = "AXA Gabon",
                    status = PartnerInsurerStatus.ACTIVE,
                    createdAt = now.atZone(ZoneId.of("Africa/Libreville")).toOffsetDateTime(),
                )
            )

            `when`(getPartnerInsurersSummariesQueryHandler.invoke(statusQuery)).thenReturn(summaryProjections)

            val response = controller.getPartnerInsurers(
                status = PartnerInsurerStatus.ACTIVE,
                search = null,
                page = 0,
                size = 20,
                sort = PartnerInsurerSortTerm.STATUS,
                sortDirection = SortDirection.ASC,
                createdBefore = null,
                createdAfter = null,
                view = QueryView.SUMMARY
            )

            assertEquals(200, response.statusCode.value())
            assertEquals(summaryProjections, response.body)
        }
    }
}