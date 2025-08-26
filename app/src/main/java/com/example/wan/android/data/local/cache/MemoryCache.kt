package com.example.wan.android.data.local.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// 内存缓存
class MemoryCache : RequestCache {

    // 内存缓存实现
    private val cache = mutableMapOf<String, CacheItem>()
    private val lock = Mutex()

    override suspend fun get(cacheKey: String): String? {
        return lock.withLock {
            val item = cache[cacheKey] ?: return@withLock null
            if (item.isExpired) {
                cache.remove(cacheKey)
                return@withLock null
            }
            item.data
        }
    }

    override suspend fun put(cacheKey: String, data: String, expireTime: Long) {
        lock.withLock {
            cache[cacheKey] = CacheItem(
                data = data,
                timestamp = System.currentTimeMillis(),
                expireTime = expireTime
            )
        }
    }

    override suspend fun clear(cacheKey: String?) {
        lock.withLock {
            if (cacheKey == null) {
                cache.clear()
            } else {
                cache.remove(cacheKey)
            }
        }
    }

    override suspend fun clearExpired() {
        lock.withLock {
            cache.entries.removeAll { it.value.isExpired }
        }
    }

}
