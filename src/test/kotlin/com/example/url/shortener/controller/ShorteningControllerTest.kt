package com.example.url.shortener.controller

import com.example.url.shortener.config.TestController
import com.example.url.shortener.model.dto.KeywordRequestDto
import com.example.url.shortener.model.dto.OriginalRequestDto
import com.example.url.shortener.model.dto.UrlRequestDto
import com.example.url.shortener.model.error.NotFoundException
import com.example.url.shortener.service.ShorteningService
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestController
@WebMvcTest(ShorteningController::class)
class ShorteningControllerTest @Autowired constructor(
    val mockMvc: MockMvc
) {
    val mapper = jacksonObjectMapper()

    @MockkBean
    lateinit var shorteningService: ShorteningService

    companion object {
        const val TEST_URL_ORG = "https://blog.mysite.com/cool-article"
        const val TEST_KEYWORD = "BEST-ARTICLE"
        const val TEST_RANDOM_KEYWORD = "BEST1"
        const val TEST_LONG_KEYWORD = "BEST-ARTICLE1231312331313123212"
        const val TEST_SHORT_URL_TEMPLATE = "https://short.en/"
    }

    @Test
    fun shorteningOriginalUrlByKeywordShouldWorkCorrectly() {
        every { shorteningService.shorteningOriginalUrlByKeyword(any(), any()) } returns "$TEST_SHORT_URL_TEMPLATE$TEST_KEYWORD"
        val response = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/shortening/by-keyword")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            KeywordRequestDto(keyword = TEST_KEYWORD, url = TEST_URL_ORG)
                        )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        verify(exactly = 1) { shorteningService.shorteningOriginalUrlByKeyword(any(), any()) }
        assertNotNull(response)
        assertTrue(response.contains(TEST_KEYWORD))
    }

    @Test
    fun shorteningOriginalUrlByKeywordShouldThrowException() {
        every { shorteningService.shorteningOriginalUrlByKeyword(any(), any()) } returns "$TEST_SHORT_URL_TEMPLATE$TEST_LONG_KEYWORD"
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/shortening/by-keyword")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            KeywordRequestDto(keyword = TEST_LONG_KEYWORD, url = TEST_URL_ORG)
                        )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

        verify(exactly = 0) { shorteningService.shorteningOriginalUrlByKeyword(any(), any()) }
    }

    @Test
    fun shorteningUrlShouldWorkCorrectly() {
        every { shorteningService.shorteningOriginalUrl(any()) } returns "$TEST_SHORT_URL_TEMPLATE$TEST_RANDOM_KEYWORD"
        val response = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/shortening")
                    .accept(MediaType.APPLICATION_JSON)
                    .content(
                        mapper.writeValueAsString(
                            UrlRequestDto(url = TEST_URL_ORG)
                        )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        verify(exactly = 1) { shorteningService.shorteningOriginalUrl(any()) }
        assertNotNull(response)
        val newKeyword = response.removeRange(0, response.lastIndexOf("/") + 1)
        assertTrue(newKeyword.length == 5)
        assertTrue(newKeyword.all{ it.isLetterOrDigit() })
    }

    @Test
    fun retrieveOriginalUrlFromShortenShouldWorkCorrectly() {
        every { shorteningService.retrieveOriginalUrlFromShorten(any()) } returns TEST_URL_ORG
        val response = mockMvc
            .perform(
                MockMvcRequestBuilders.get("/shortening/get/original")
                    .param("shortUrl", "$TEST_SHORT_URL_TEMPLATE$TEST_RANDOM_KEYWORD")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn().response.contentAsString

        verify(exactly = 1) { shorteningService.retrieveOriginalUrlFromShorten(any()) }
        assertNotNull(response)
        assertTrue(response.equals(TEST_URL_ORG))
    }

    @Test
    fun retrieveOriginalUrlFromShortenShouldThrowException() {
        every { shorteningService.retrieveOriginalUrlFromShorten(any()) } throws NotFoundException(TEST_RANDOM_KEYWORD, "")
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/shortening/get/original")
                    .param("shortUrl", "$TEST_SHORT_URL_TEMPLATE$TEST_RANDOM_KEYWORD")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

        verify(exactly = 1) { shorteningService.retrieveOriginalUrlFromShorten(any()) }
    }
}