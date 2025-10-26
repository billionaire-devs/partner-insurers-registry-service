package com.bamboo.assur.partnerinsurersservice.registry.presentation.controllers

import com.bamboo.assur.partnerinsurersservice.core.domain.Result
import com.bamboo.assur.partnerinsurersservice.core.utils.SortDirection
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers.CreatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers.ChangePartnerInsurerStatusCommandHandler
import com.bamboo.assur.partnerinsurersservice.registry.application.commands.handlers.UpdatePartnerInsurerCommandHandler
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.GetPartnerInsurerByIdQuery
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.GetPartnerSummariesQuery
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.handlers.GetPartnerInsurerByIdQueryHandler
import com.bamboo.assur.partnerinsurersservice.registry.application.queries.handlers.GetPartnerSummariesQueryHandler
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests.CreatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests.ChangePartnerInsurerStatusRequestDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.requests.UpdatePartnerInsurerRequestDto
import com.bamboo.assur.partnerinsurersservice.registry.presentation.dtos.responses.PartnerInsurerDetailResponseDto.Companion.toResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@RestController
@RequestMapping("/v1/partner-insurers")
class PartnerInsurerController(
    private val createCommandHandler: CreatePartnerInsurerCommandHandler,
    private val getPartnerSummariesQueryHandler: GetPartnerSummariesQueryHandler,
    private val getPartnerInsurerByIdQueryHandler: GetPartnerInsurerByIdQueryHandler,
    private val changeStatusCommandHandler: ChangePartnerInsurerStatusCommandHandler,
    private val updatePartnerInsurerCommandHandler: UpdatePartnerInsurerCommandHandler,
) {

    @PostMapping
    suspend fun createPartnerInsurer(
        @Validated
        @RequestBody
        request: CreatePartnerInsurerRequestDto,
    ): ResponseEntity<Any> {
        val command = request.toCommand()
        return when (val result = createCommandHandler(command)) {
            is Result.Success -> ResponseEntity.ok(mapOf("id" to result.value))
            is Result.Failure -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to result.message))
        }
    }

    @GetMapping("/{id}")
    suspend fun getPartnerInsurerById(
        @PathVariable id: UUID,
    ): ResponseEntity<Any> {
        val query = GetPartnerInsurerByIdQuery(id)
        return when (val result = getPartnerInsurerByIdQueryHandler(query)) {
            is Result.Success -> ResponseEntity.ok(result.value.toResponseDTO())
            is Result.Failure -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to result.message))
        }
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
        @RequestParam(required = false, defaultValue = "ACTIVE") status: String?,
        @RequestParam(required = false) search: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = "ASC") sortDirection: SortDirection,
    ): ResponseEntity<Any> {
        val query = GetPartnerSummariesQuery(status, search, page, size, sortBy, sortDirection)
        return when (val result = getPartnerSummariesQueryHandler(query)) {
            is Result.Success -> ResponseEntity.ok(result.value)
            is Result.Failure -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to result.message))
        }
    }

    @PutMapping("/{id}")
    suspend fun updatePartnerInsurer(
        @PathVariable id: UUID,
        @Validated @RequestBody request: UpdatePartnerInsurerRequestDto,
    ): ResponseEntity<Any> {
        val command = request.toCommand(id)
        return when (val result = updatePartnerInsurerCommandHandler(command)) {
            is Result.Success -> ResponseEntity.ok(result.value)
            is Result.Failure -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to result.message))
        }
    }
}

