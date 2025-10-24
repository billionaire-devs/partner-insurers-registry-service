package com.bamboo.assur.partnerinsurersservice.core.presentation

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainException
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.BeanInstantiationException
import org.springframework.data.mapping.model.MappingInstantiationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    data class ApiError(
        val timestamp: OffsetDateTime = OffsetDateTime.now(),
        val status: Int,
        val error: String,
        val message: String?,
        val path: String?
    )

    private fun sanitize(input: String?): String? = input?.replace(Regex("[<>\"']"), "")

    private fun buildError(status: HttpStatus, message: String?, path: String?) =
        ApiError(
            status = status.value(),
            error = status.reasonPhrase,
            message = sanitize(message),
            path = sanitize(path)
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

    // Handle Spring mapping/instantiation failures (e.g. when a DB row has null for a non-null constructor param)
    @ExceptionHandler(BeanInstantiationException::class)
    fun handleBeanInstantiation(ex: BeanInstantiationException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val causeMessage = ex.cause?.message ?: ex.message
        val message = "Failed to instantiate projection / DTO: ${causeMessage ?: "see server logs"}"
        val body = buildError(HttpStatus.BAD_REQUEST, message, request.requestURI)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(MappingInstantiationException::class)
    fun handleMappingInstantiation(ex: MappingInstantiationException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val causeMessage = ex.cause?.message ?: ex.message
        val message = "Failed to map database row to projection: ${causeMessage ?: "see server logs"}"
        val body = buildError(HttpStatus.BAD_REQUEST, message, request.requestURI)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomain(ex: DomainException, request: HttpServletRequest): ResponseEntity<ApiError> {
        val body = buildError(HttpStatus.BAD_REQUEST, ex.message, request.requestURI)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(Throwable::class)
    fun handleUnexpected(ex: Throwable, request: HttpServletRequest): ResponseEntity<ApiError> {
        logger.error("Unexpected error while handling request ${request.requestURI}", ex)
        val body = buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request.requestURI)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
