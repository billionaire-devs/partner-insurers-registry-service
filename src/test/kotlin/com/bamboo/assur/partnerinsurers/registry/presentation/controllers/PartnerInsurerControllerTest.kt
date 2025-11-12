package com.bamboo.assur.partnerinsurers.registry.presentation.controllers

import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.ChangePartnerInsurerStatusCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.CreatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.UpdatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurerQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerDetailedProjectionQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerSummaryProjectionQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerSummariesQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.models.PartnerInsurerProjection
import com.bamboo.assur.partnerinsurers.registry.domain.entities.PartnerInsurer
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.domain.valueObjects.TaxIdentificationNumber
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.GetPartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryView
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Address
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.valueObjects.Url
import kotlinx.coroutines.test.runTest
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
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class PartnerInsurerControllerTest {

    @Mock private lateinit var createCommandHandler: CreatePartnerInsurerCommandHandler
    @Mock private lateinit var getPartnerSummariesQueryHandler: GetPartnerSummariesQueryHandler
    @Mock private lateinit var getPartnerInsurerSummaryProjectionQueryHandler: GetPartnerInsurerSummaryProjectionQueryHandler
    @Mock private lateinit var getPartnerInsurerDetailedProjectionQueryHandler: GetPartnerInsurerDetailedProjectionQueryHandler
    @Mock private lateinit var getFullPartnerInsurerQueryHandler: GetPartnerInsurerQueryHandler
    @Mock private lateinit var changeStatusCommandHandler: ChangePartnerInsurerStatusCommandHandler
    @Mock private lateinit var updatePartnerInsurerCommandHandler: UpdatePartnerInsurerCommandHandler

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
                createdAt = now,
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
            val detailed = PartnerInsurerProjection.DetailedProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                status = PartnerInsurerStatus.ACTIVE.name,
                logoUrl = Url("https://example.com/logo.png"),
                address = jsonAddress,
                contacts = emptySet(),
                agreementsSummary = emptySet(),
                createdAt = now,
                updatedAt = now,
            )

            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenReturn(detailed)

            val response = controller.getPartnerInsurerById(partnerId, QueryView.DETAILED)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.DetailedProjection
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
`when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenAnswer { throw EntityNotFoundException("PartnerInsurer", partnerId) }

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
                createdAt = now,
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
            val projection = PartnerInsurerProjection.DetailedProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                status = PartnerInsurerStatus.ACTIVE.name,
                logoUrl = Url("https://example.com/logo.png"),
                address = jsonAddress,
                contacts = emptySet(),
                agreementsSummary = emptySet(),
                createdAt = now,
                updatedAt = now,
            )

            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenReturn(projection)

            val response = controller.getPartnerInsurerByPartnerCode(partnerCode, QueryView.DETAILED)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.DetailedProjection
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
`when`(getPartnerInsurerSummaryProjectionQueryHandler.invoke(query)).thenAnswer { throw EntityNotFoundException("PartnerInsurer", partnerCode) }

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
                createdAt = now,
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
            val projection = PartnerInsurerProjection.DetailedProjection(
                id = partnerId,
                partnerInsurerCode = partnerCode,
                legalName = "AXA Gabon",
                taxIdentificationNumber = TaxIdentificationNumber(taxIdentificationNumber),
                status = PartnerInsurerStatus.ACTIVE.name,
                logoUrl = Url("https://example.com/logo.png"),
                address = jsonAddress,
                contacts = emptySet(),
                agreementsSummary = emptySet(),
                createdAt = now,
                updatedAt = now,
            )

            `when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenReturn(projection)

            val response = controller.getPartnerInsurerByTaxIdentificationNumber(taxIdentificationNumber, QueryView.DETAILED)

            assertEquals(200, response.statusCode.value())
            val body = response.body as PartnerInsurerProjection.DetailedProjection
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
`when`(getPartnerInsurerDetailedProjectionQueryHandler.invoke(query)).thenAnswer { throw EntityNotFoundException("PartnerInsurer", taxIdentificationNumber) }

            assertThrows<EntityNotFoundException> {
                controller.getPartnerInsurerByTaxIdentificationNumber(taxIdentificationNumber, QueryView.DETAILED)
            }
        }
    }
}