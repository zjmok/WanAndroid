package com.example.wan.android.data.local.cache

// 缓存接口
interface RequestCache {

    suspend fun get(cacheKey: String): String?

    suspend fun put(cacheKey: String, data: String, expireTime: Long = 30 * 24 * 60 * 60 * 1000L)

    suspend fun clear(cacheKey: String? = null)

    suspend fun clearExpired()

}
