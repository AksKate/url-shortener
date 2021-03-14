package com.example.url.shortener.service

import com.example.url.shortener.controller.ShorteningControllerTest
import com.example.url.shortener.model.error.NotFoundException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ShorteningServiceTest {
    @Autowired
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
        val result = shorteningService.shorteningOriginalUrlByKeyword(TEST_URL_ORG, TEST_KEYWORD)
        assertNotNull(result)
        assertTrue(result.contains(ShorteningControllerTest.TEST_KEYWORD))
    }

    @Test
    fun shorteningUrlShouldWorkCorrectly() {
        val result = shorteningService.shorteningOriginalUrl(TEST_URL_ORG)
        assertNotNull(result)
        val newKeyword = result.removeRange(0, result.lastIndexOf("/") + 1)
        assertTrue(newKeyword.length == 5)
        assertTrue(newKeyword.all{ it.isLetterOrDigit() })
    }

    @Test
    fun retrieveOriginalUrlFromShortenShouldWorkCorrectly() {
        shorteningService.shorteningOriginalUrlByKeyword(TEST_URL_ORG, TEST_KEYWORD)
        val result = shorteningService.retrieveOriginalUrlFromShorten("$TEST_SHORT_URL_TEMPLATE$TEST_KEYWORD")
        assertNotNull(result)
        assertTrue(result.equals(TEST_URL_ORG))
    }

    @Test
    fun retrieveOriginalUrlFromShorten() {
        shorteningService.shorteningOriginalUrlByKeyword(TEST_URL_ORG, TEST_KEYWORD)
        assertThrows<NotFoundException> {
            shorteningService.retrieveOriginalUrlFromShorten("$TEST_SHORT_URL_TEMPLATE$TEST_RANDOM_KEYWORD")
        }
    }
}