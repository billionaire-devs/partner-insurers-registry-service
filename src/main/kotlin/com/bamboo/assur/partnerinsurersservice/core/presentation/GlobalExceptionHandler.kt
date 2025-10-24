package com.bamboo.assur.partnerinsurersservice.core.presentation

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainException
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.OffsetDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ApiError(
        val timestamp: OffsetDateTime = OffsetDateTime.now(),
        val status: Int,
        val error: String,
        val message: String?,
        val path: String?
    )

    private fun buildError(status: HttpStatus, message: String?, path: String?) =
        ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = path
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val body = buildError(HttpStatus.BAD_REQUEST, ex.message, request.requestURI)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val body = buildError(HttpStatus.NOT_FOUND, ex.message, request.requestURI)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(EntityAlreadyExistsException::class)
    fun handleAlreadyExists(ex: EntityAlreadyExistsException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val body = buildError(HttpStatus.CONFLICT, ex.message, request.requestURI)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(
        DuplicateKeyException::class,
        DataIntegrityViolationException::class,
//        PostgresqlDataIntegrityViolationException::class,
    )
    fun handleDuplicateKey(ex: Throwable, request: HttpServletRequest): ResponseEntity<ApiError> {
        val message = ex.message ?: "Duplicate key / data integrity violation"
        val body = buildError(HttpStatus.CONFLICT, message, request.requestURI)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomain(ex: DomainException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val body = buildError(HttpStatus.BAD_REQUEST, ex.message, request.requestURI)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(Throwable::class)
    fun handleUnexpected(ex: Throwable, request: HttpServletRequest): ResponseEntity<ApiError> {
        val body = buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request.requestURI)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}

