package com.example.url.shortener.controller

import com.example.url.shortener.model.dto.KeywordRequestDto
import com.example.url.shortener.model.dto.OriginalRequestDto
import com.example.url.shortener.model.dto.UrlRequestDto
import com.example.url.shortener.service.ShorteningService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/shortening")
@Validated
class ShorteningController(val shorteningService: ShorteningService) {

    @PostMapping("/by-keyword")
    fun shorteningByKeyword(@RequestBody @Valid request: KeywordRequestDto): ResponseEntity<String> {
        val result = shorteningService.shorteningOriginalUrlByKeyword(request.url, request.keyword)
        return ResponseEntity.ok(result)
    }

    @PostMapping
    fun shorteningUrl(@RequestBody request: UrlRequestDto): ResponseEntity<String> {
        val result = shorteningService.shorteningOriginalUrl(request.url)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/get/original")
    fun retrieveOriginalUrlFromShorten(request: OriginalRequestDto): ResponseEntity<String> {
        return ResponseEntity.ok(shorteningService.retrieveOriginalUrlFromShorten(request.shortUrl))
    }
}