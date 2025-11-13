package com.bamboo.assur.partnerinsurers.registry.presentation.controllers

import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.ChangePartnerInsurerStatusCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.CreatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.UpdatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurerQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.GetPartnerInsurersQuery
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerDetailedProjectionQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurerSummaryProjectionQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurersQueryHandler
import com.bamboo.assur.partnerinsurers.registry.application.queries.handlers.GetPartnerInsurersSummariesQueryHandler
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerSortTerm
import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.ChangePartnerInsurerStatusRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.CreatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.UpdatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.CreatePartnerInsurerResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.GetPartnerInsurerResponseDto.Companion.toResponseDto
import com.bamboo.assur.partnerinsurers.sharedkernel.application.QueryView
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.Result
import com.bamboo.assur.partnerinsurers.sharedkernel.domain.utils.SortDirection
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Suppress("LongParameterList")
@RestController
@RequestMapping("/v1/partner-insurers")
class PartnerInsurerController(
    private val createCommandHandler: CreatePartnerInsurerCommandHandler,
    private val getPartnerInsurersQueryHandler: GetPartnerInsurersQueryHandler,
    private val getPartnerInsurerSummaryProjectionQueryHandler: GetPartnerInsurerSummaryProjectionQueryHandler,
    private val getPartnerInsurerDetailedProjectionQueryHandler: GetPartnerInsurerDetailedProjectionQueryHandler,
    private val getFullPartnerInsurerQueryHandler: GetPartnerInsurerQueryHandler,
    private val changeStatusCommandHandler: ChangePartnerInsurerStatusCommandHandler,
    private val updatePartnerInsurerCommandHandler: UpdatePartnerInsurerCommandHandler,
    private val getPartnerInsurersSummariesQueryHandler: GetPartnerInsurersSummariesQueryHandler,
) {

    @PostMapping
    suspend fun createPartnerInsurer(
        @Validated
        @RequestBody
        request: CreatePartnerInsurerRequestDto,
        serverRequest: HttpServletRequest
    ): ResponseEntity<CreatePartnerInsurerResponseDto> {
        val response = createCommandHandler(request.toCommand())
        val location = ServletUriComponentsBuilder
            .fromRequest(serverRequest)
            .buildAndExpand(response.id)
            .toUri()

        return ResponseEntity.created(location).body(response)
    }

    @GetMapping("/{id}")
    suspend fun getPartnerInsurerById(
        @PathVariable id: UUID,
        @RequestParam(required = false, defaultValue = "DETAILED") view: QueryView,
    ): ResponseEntity<Any> {
        val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ById(id))
        val result = getPartnerInsurer(view, query)

        return ResponseEntity.ok(result)
    }

    @GetMapping("/by-partner-code/{partnerCode}")
    suspend fun getPartnerInsurerByPartnerCode(
        @PathVariable partnerCode: String,
        @RequestParam(required = false, defaultValue = "DETAILED") view: QueryView,
    ): ResponseEntity<Any> {
        val query = GetPartnerInsurerQuery(GetPartnerInsurerQuery.Identifier.ByPartnerCode(partnerCode))
        val result = getPartnerInsurer(view, query)

        return ResponseEntity.ok(result)
    }

    @GetMapping("/by-tax-identification-number/{taxIdentificationNumber}")
    suspend fun getPartnerInsurerByTaxIdentificationNumber(
        @PathVariable taxIdentificationNumber: String,
        @RequestParam(required = false, defaultValue = "DETAILED")
        view: QueryView = QueryView.DETAILED,
    ): ResponseEntity<Any> {
        val query = GetPartnerInsurerQuery(
            GetPartnerInsurerQuery.Identifier.ByTaxIdentificationNumber(taxIdentificationNumber)
        )
        val result = getPartnerInsurer(view, query)

        return ResponseEntity.ok(result)
    }

    @PatchMapping("/{id}/status")
    suspend fun changePartnerInsurerStatus(
        @PathVariable id: UUID,
        @Validated @RequestBody request: ChangePartnerInsurerStatusRequestDto,
    ): ResponseEntity<Any> {
        val command = request.toCommand(id)
        return when (val result = changeStatusCommandHandler(command)) {
            is Result.Success -> ResponseEntity.ok(result.value)
            is Result.Failure -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to result.message))
        }
    }

    @Suppress("LongParameterList")
    @GetMapping
    suspend fun getPartnerInsurers(
        @RequestParam(required = false, defaultValue = "ACTIVE") status: PartnerInsurerStatus?,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) sort: PartnerInsurerSortTerm?,
        @RequestParam(defaultValue = "ASC") sortDirection: SortDirection,
        @RequestParam(required = false) createdBefore: String?,
        @RequestParam(required = false) createdAfter: String?,
        @RequestParam(required = false, defaultValue = "SUMMARY") view: QueryView,
    ): ResponseEntity<Flow<Any>> {

        val query = GetPartnerInsurersQuery(
            status,
            search,
            page,
            size,
            sort,
            sortDirection,
            createdBefore,
            createdAfter,
            view
        )

        val result = when (view) {
            QueryView.SUMMARY -> getPartnerInsurersSummariesQueryHandler(query)
            QueryView.DETAILED -> getPartnerInsurersQueryHandler(query)
            else -> throw IllegalArgumentException("Invalid view: $view")
        }
        return ResponseEntity.ok(result)
    }

    @PatchMapping("/{id}")
    suspend fun updatePartnerInsurer(
        @PathVariable id: UUID,
        @Validated @RequestBody request: UpdatePartnerInsurerRequestDto,
    ): ResponseEntity<Any> {
        val command = request.toCommand(id)
        val result = updatePartnerInsurerCommandHandler(command)
        return ResponseEntity.ok(result)
    }


    private suspend fun getPartnerInsurer(
        view: QueryView,
        query: GetPartnerInsurerQuery,
    ): Any? = when (view) {
        QueryView.SUMMARY -> getPartnerInsurerSummaryProjectionQueryHandler(query)
        QueryView.DETAILED -> getPartnerInsurerDetailedProjectionQueryHandler(query)
        QueryView.FULL -> getFullPartnerInsurerQueryHandler(query).toResponseDto()
    }
}
