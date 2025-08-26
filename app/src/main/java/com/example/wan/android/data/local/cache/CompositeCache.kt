package com.example.wan.android.data.local.cache

// 混合缓存
class CompositeCache(
    private val memoryCache: MemoryCache,
    private val diskCache: DiskCache
): RequestCache {

    override suspend fun get(cacheKey: String): String? {
        // 先从内存缓存获取
        val memoryData = memoryCache.get(cacheKey)
        if (memoryData != null) return memoryData

        // 内存没有再从磁盘获取
        val diskData = diskCache.get(cacheKey)
        if (diskData != null) {
            // 将磁盘数据放入内存缓存
            memoryCache.put(cacheKey, diskData)
            return diskData
        }

        // 没有缓存
        return null
    }

    override suspend fun put(cacheKey: String, data: String, expireTime: Long) {
        // 同时处理
        memoryCache.put(cacheKey, data, expireTime)
        diskCache.put(cacheKey, data, expireTime)
    }

    override suspend fun clear(cacheKey: String?) {
        // 同时处理
        memoryCache.clear(cacheKey)
        diskCache.clear(cacheKey)
    }

    override suspend fun clearExpired() {
        // 同时处理
        memoryCache.clearExpired()
        diskCache.clearExpired()
    }

}