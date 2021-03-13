package com.example.url.shortener.model.dto

import javax.validation.constraints.Size

data class KeywordRequestDto(
    @field:Size(max = 20, message = "Max length 20")
    val keyword: String,
    val url: String
)