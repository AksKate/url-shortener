package com.example.url.shortener.service

import com.example.url.shortener.dao.CacheImpl
import com.example.url.shortener.model.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.random.Random

interface ShorteningService {
    fun shorteningOriginalUrlByKeyword(url: String, keyword: String): String
    fun shorteningOriginalUrl(url: String): String
    fun retrieveOriginalUrlFromShorten(shortUrl: String): String
}

@Service
class ShorteningServiceImpl(val cache: CacheImpl<String, String>): ShorteningService {

    companion object {
        val ALPHA_NUMERIC = ('0'..'9') + ('A'..'Z')

        const val LENGTH = 5
    }

    @Value("\${default.short-url:\"https://short.en/\"}")
    lateinit var shortUrlTemplate: String

    override fun shorteningOriginalUrlByKeyword(url: String, keyword: String): String {
        val shortUrl = "$shortUrlTemplate$keyword"
        val findValue = cache.get(shortUrl)
        findValue?.let {
            return it
        } ?:
        cache.put(keyword, url)
        return shortUrl
    }

    override fun shorteningOriginalUrl(url: String): String {
        val keyword = generateKeyword()
        val shortUrl = "$shortUrlTemplate$keyword"
        cache.put(keyword, url)
        return shortUrl
    }

    private fun generateKeyword(): String = (1..LENGTH)
        .map { Random.nextInt(0, ALPHA_NUMERIC.size) }
        .map(ALPHA_NUMERIC::get)
        .joinToString("")

    override fun retrieveOriginalUrlFromShorten(shortUrl: String): String {
        val keyword = shortUrl.removeRange(0, shortUrl.lastIndexOf("/") + 1)
        return cache.get(keyword) ?: throw NotFoundException("Url by short url $shortUrl is not found.")
    }
}