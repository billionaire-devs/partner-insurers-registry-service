package com.bamboo.assur.partnerinsurers.registry.presentation.controllers

import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.AddPartnerInsurerContactCommandHandler
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.AddPartnerInsurerContactRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.AddPartnerInsurerContactResponseDto
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@RestController
@RequestMapping("/v1/partner-insurers/{partnerInsurerId}/contacts")
class PartnerInsurerContactsController(
    private val addContactCommandHandler: AddPartnerInsurerContactCommandHandler,
) {
    @PostMapping
    suspend fun addPartnerInsurerContact(
        @PathVariable partnerInsurerId: UUID,
        @Validated @RequestBody request: AddPartnerInsurerContactRequestDto,
        serverRequest: HttpServletRequest,
    ): ResponseEntity<AddPartnerInsurerContactResponseDto> {
        val command = request.toCommand(partnerInsurerId)
        val response = addContactCommandHandler(command)
        val location = ServletUriComponentsBuilder
            .fromRequest(serverRequest)
            .path("/{contactId}")
            .buildAndExpand(response.contactId)
            .toUri()

        return ResponseEntity.created(location).body(response)
    }
}