package com.example.url.shortener.config

import com.example.url.shortener.dao.Cache
import com.example.url.shortener.dao.CacheImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CacheConfig {

    @Bean
    fun <K, V> getCache(): Cache<K, V> {
        return CacheImpl()
    }
}