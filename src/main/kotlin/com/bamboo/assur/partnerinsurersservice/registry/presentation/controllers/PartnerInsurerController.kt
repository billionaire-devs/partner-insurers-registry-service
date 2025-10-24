package com.bamboo.assur.partnerinsurersservice.registry.presentation.controllers

import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers.CreatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.GetPartnerSummariesQuery
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.handlers.GetPartnerSummariesQueryHandler
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests.CreatePartnerInsurerRequestDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@RestController
@RequestMapping("/api/partner-insurers")
class PartnerInsurerController(
    private val createCommandHandler: CreatePartnerInsurerCommandHandler,
    private val getPartnerSummariesQueryHandler: GetPartnerSummariesQueryHandler,
) {

    @PostMapping
    suspend fun createPartnerInsurer(
        @Validated
        @RequestBody
        request: CreatePartnerInsurerRequestDto,
    ): ResponseEntity<Any> {
        val command = request.toCommand()
        return when (val result = createCommandHandler.handle(command)) {
            is Result.Success -> ResponseEntity.ok(mapOf("id" to result.value))
            is Result.Failure -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to result.message))
        }
    }

    @Suppress("LongParameterList")
    @GetMapping
    suspend fun getPartnerInsurers(
        @RequestParam(required = false, defaultValue = "ACTIVE") status: String?,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = "ASC") sortDirection: SortDirection,
    ): ResponseEntity<Any> {
        val query = GetPartnerSummariesQuery(status, search, page, size, sortBy, sortDirection)
        return when (val result = getPartnerSummariesQueryHandler.handle(query)) {
            is Result.Success -> ResponseEntity.ok(result.value)
            is Result.Failure -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to result.message))
        }
    }
}

