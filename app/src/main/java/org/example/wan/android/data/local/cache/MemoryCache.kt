package org.example.wan.android.data.local.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 内存缓存实现
 * 使用 Map 存储，进程内有效，应用退出后失效
 * 线程安全，使用 Mutex 保证并发安全
 */
class MemoryCache : RequestCache {

    // 缓存容器
    private val cache = mutableMapOf<String, CacheItem>()

    // 线程锁
    private val lock = Mutex()

    /**
     * 获取缓存
     * @param cacheKey 缓存键
     * @return 缓存数据，null 表示无缓存或已过期
     */
    override suspend fun get(cacheKey: String): String? {
        return lock.withLock {
            val item = cache[cacheKey] ?: return@withLock null
            // 检查是否过期，过期则移除
            if (item.isExpired) {
                cache.remove(cacheKey)
                return@withLock null
            }
            item.data
        }
    }

    /**
     * 保存缓存
     * @param cacheKey 缓存键
     * @param data 缓存数据 (JSON 字符串)
     * @param expireTime 过期时间 (毫秒)
     */
    override suspend fun put(cacheKey: String, data: String, expireTime: Long) {
        lock.withLock {
            cache[cacheKey] = CacheItem(
                data = data,
                timestamp = System.currentTimeMillis(),
                expireTime = expireTime
            )
        }
    }

    /**
     * 清除缓存
     * @param cacheKey null 表示清除所有，否则清除指定键
     */
    override suspend fun clear(cacheKey: String?) {
        lock.withLock {
            if (cacheKey == null) {
                cache.clear()
            } else {
                cache.remove(cacheKey)
            }
        }
    }

    /**
     * 清除过期缓存
     */
    override suspend fun clearExpired() {
        lock.withLock {
            cache.entries.removeAll { it.value.isExpired }
        }
    }

}
