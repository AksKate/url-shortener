package com.example.url.shortener.model.error

class NotFoundException(val key: String, message: String) : Throwable(message)

data class EntityNotFound(val key: String, val message: String)
data class FieldError(val fieldName: String, val error: String?)