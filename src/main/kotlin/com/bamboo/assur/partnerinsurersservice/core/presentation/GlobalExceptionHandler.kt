package com.bamboo.assur.partnerinsurersservice.core.presentation

import com.bamboo.assur.partnerinsurersservice.core.domain.DomainException
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityAlreadyExistsException
import com.bamboo.assur.partnerinsurersservice.core.domain.EntityNotFoundException
import com.bamboo.assur.partnerinsurersservice.core.domain.FailedToSaveEntityException
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

    // HTML-escape user-supplied strings to avoid XSS when reflecting them in responses
    private fun escapeHtml(input: String?): String? = input
        ?.replace("&", "&amp;")
        ?.replace("<", "&lt;")
        ?.replace(">", "&gt;")
        ?.replace("\"", "&quot;")
        ?.replace("'", "&#x27;")

    private fun buildApiResponse(status: HttpStatus, message: String?, request: HttpServletRequest): ApiResponse<Any> {
        val reqMeta = RequestMetadata(
            method = escapeHtml(request.method) ?: request.method,
            path = escapeHtml(request.requestURI) ?: request.requestURI,
            query = escapeHtml(request.queryString)
        )

        val respMeta = ResponseMetadata(
            status = status.value(),
            reason = status.reasonPhrase,
            timestamp = OffsetDateTime.now()
        )

        val errorBody = ErrorBody(
            message = escapeHtml(message),
            details = null
        )

        val meta = Meta(request = reqMeta, response = respMeta)

        return ApiResponse(
            success = false,
            meta = meta,
            data = null,
            error = errorBody
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val body = buildApiResponse(HttpStatus.BAD_REQUEST, ex.message, request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }


    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val body = buildApiResponse(HttpStatus.NOT_FOUND, ex.message, request)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(EntityAlreadyExistsException::class)
    fun handleAlreadyExists(ex: EntityAlreadyExistsException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val body = buildApiResponse(HttpStatus.CONFLICT, ex.message, request)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    @ExceptionHandler(
        DuplicateKeyException::class,
        DataIntegrityViolationException::class,
    )
    fun handleDuplicateKey(ex: Throwable, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val message = ex.message ?: "Duplicate key / data integrity violation"
        val body = buildApiResponse(HttpStatus.CONFLICT, message, request)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body)
    }

    // Handle Spring mapping/instantiation failures (e.g. when a DB row has null for a non-null constructor param)
    @ExceptionHandler(BeanInstantiationException::class)
    fun handleBeanInstantiation(ex: BeanInstantiationException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val causeMessage = ex.cause?.message ?: ex.message
        val message = "Failed to instantiate projection / DTO: ${causeMessage ?: "see server logs"}"
        val body = buildApiResponse(HttpStatus.BAD_REQUEST, message, request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(MappingInstantiationException::class)
    fun handleMappingInstantiation(ex: MappingInstantiationException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val causeMessage = ex.cause?.message ?: ex.message
        val message = "Failed to map database row to projection: ${causeMessage ?: "see server logs"}"
        val body = buildApiResponse(HttpStatus.BAD_REQUEST, message, request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(DomainException::class)
    fun handleDomain(ex: DomainException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val body = buildApiResponse(HttpStatus.BAD_REQUEST, ex.message, request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(Throwable::class)
    fun handleUnexpected(ex: Throwable, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val safePath = escapeHtml(request.requestURI) ?: request.requestURI
        logger.error("Unexpected error while handling request $safePath", ex)
        val body = buildApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", request)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }

    @ExceptionHandler(FailedToSaveEntityException::class)
    fun handleFailedToSaveEntity(ex: FailedToSaveEntityException, request: HttpServletRequest): ResponseEntity<ApiResponse<Any>> {
        val body = buildApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message, request)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
    }
}
