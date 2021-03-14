package com.example.url.shortener.config

import com.example.url.shortener.model.error.EntityNotFound
import com.example.url.shortener.model.error.FieldError
import com.example.url.shortener.model.error.NotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.io.IOException
import java.time.LocalDateTime
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(NotFoundException::class)
    @Throws(IOException::class)
    fun notFound(ex: NotFoundException, response: HttpServletResponse): ResponseEntity<Any> {
        val errors = EntityNotFound(ex.key, ex.message!!)
        val status = HttpStatus.BAD_REQUEST
        val body = buildBody(status, errors)
        return ResponseEntity(body, status)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult
            .fieldErrors
            .map { FieldError(it.field, it.defaultMessage) }
        val body = buildBody(status, errors)
        return ResponseEntity(body, headers, status)
    }
}

fun buildBody(status: HttpStatus, errors: Any): LinkedHashMap<String, Any> {
    val body = LinkedHashMap<String, Any>()
    body["timestamp"] = LocalDateTime.now()
    body["status"] = status.value()
    body["errors"] = errors
    return body
}