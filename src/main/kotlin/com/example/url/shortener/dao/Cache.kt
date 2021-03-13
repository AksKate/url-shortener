package com.example.url.shortener.dao

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface CacheValue<V> {
    val value: V
    val createdAt: LocalDateTime
}

interface Cache<K, V> {
    fun containsKey(key: K): Boolean
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun remove(key: K)
    fun cleanExpired()
    fun clearAll()
}

@Component
class CacheImpl<K, V> : Cache<K,V> {
    @Value("\${default.cache.timeout:60000}")
    private val cacheTimeout: Long? = null

    private val cacheMap = mutableMapOf<K, CacheValue<V>>()

    override fun containsKey(key: K): Boolean {
        return cacheMap.containsKey(key)
    }

    override fun get(key: K): V? {
        return cacheMap[key]?.value
    }

    override fun put(key: K, value: V) {
        cacheMap.put(key, createCacheValue(value))
    }

    private fun createCacheValue(value: V): CacheValue<V> {
        val now = LocalDateTime.now()
        return object : CacheValue<V> {
            override val value: V
                get() = value
            override val createdAt: LocalDateTime
                get() = now
        }
    }

    override fun remove(key: K) {
        cacheMap.remove(key)
    }

    override fun cleanExpired() {
        getExpiredKeys().forEach { key ->
            cacheMap.remove(key)
        }
    }

    private fun getExpiredKeys(): List<K> {
        return cacheMap.filter { isExpired(it.value) }.map { it.key }
    }

    private fun isExpired(value: CacheValue<V>): Boolean =
        LocalDateTime.now().isAfter(value.createdAt.plus(cacheTimeout!!, ChronoUnit.MILLIS))

    override fun clearAll() {
        cacheMap.clear()
    }
}