package com.bamboo.assur.partnerinsurers.registry.presentation.controllers

import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.AddPartnerInsurerContactCommandHandler
import com.bamboo.assur.partnerinsurers.registry.application.commands.handlers.UpdatePartnerInsurerContactCommandHandler
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.AddPartnerInsurerContactRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.requests.UpdatePartnerInsurerContactRequestDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.AddPartnerInsurerContactResponseDto
import com.bamboo.assur.partnerinsurers.registry.presentation.dtos.responses.UpdatePartnerInsurerContactResponseDto
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@RestController
@RequestMapping("/v1/partner-insurers")
class PartnerInsurerContactsController(
    private val addContactCommandHandler: AddPartnerInsurerContactCommandHandler,
    private val updateContactCommandHandler: UpdatePartnerInsurerContactCommandHandler,
) {
    @PostMapping("/{partnerInsurerId}/contacts")
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

    @PatchMapping("/{partnerInsurerId}/contacts/{contactId}")
    suspend fun updatePartnerInsurerContact(
        @PathVariable partnerInsurerId: UUID,
        @PathVariable contactId: UUID,
        @Validated @RequestBody request: UpdatePartnerInsurerContactRequestDto,
    ): ResponseEntity<UpdatePartnerInsurerContactResponseDto> {
        val command = request.toCommand(partnerInsurerId, contactId)
        val response = updateContactCommandHandler(command)
        return ResponseEntity.ok(response)
    }
}