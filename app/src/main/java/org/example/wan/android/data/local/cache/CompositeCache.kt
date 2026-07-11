package org.example.wan.android.data.local.cache

/**
 * 混合缓存 (L1 + L2)
 * 实现三级缓存中的两层: Memory(一级) + Disk(二级)
 *
 * 获取策略: Memory -> Disk
 * 存储策略: Memory && Disk (同时写入)
 *
 * 组合模式: CompositeCache 将多个 RequestCache 组合成一个统一接口, 对外表现得像单个缓存, 内部按策略 (内存 -> 磁盘) 协调多个实现
 */
class CompositeCache(
    private val memoryCache: RequestCache,
    private val diskCache: RequestCache
): RequestCache {

    /**
     * 获取缓存
     * 优先从内存获取，内存没有再从磁盘获取
     * 磁盘命中时同步到内存
     *
     * @param cacheKey 缓存键
     * @return 缓存数据
     */
    override suspend fun get(cacheKey: String): String? {
        // 1. 先从内存缓存 (L1) 获取
        val memoryData = memoryCache.get(cacheKey)
        if (memoryData != null) return memoryData

        // 2. 内存没有再从磁盘 (L2) 获取
        val diskData = diskCache.get(cacheKey)
        if (diskData != null) {
            // 3. 磁盘命中，同步到内存
            memoryCache.put(cacheKey, diskData, 0)
            return diskData
        }

        // 没有缓存
        return null
    }

    /**
     * 保存缓存
     * 同时写入内存和磁盘
     *
     * @param cacheKey 缓存键
     * @param data 缓存数据
     * @param expireTime 过期时间
     */
    override suspend fun put(cacheKey: String, data: String, expireTime: Long) {
        // 同时写入两层缓存
        memoryCache.put(cacheKey, data, expireTime)
        diskCache.put(cacheKey, data, expireTime)
    }

    /**
     * 清除缓存
     * 同时清除两层缓存
     *
     * @param cacheKey null 表示清除所有
     */
    override suspend fun clear(cacheKey: String?) {
        memoryCache.clear(cacheKey)
        diskCache.clear(cacheKey)
    }

    /**
     * 清除过期缓存
     * 同时清理两层缓存
     */
    override suspend fun clearExpired() {
        memoryCache.clearExpired()
        diskCache.clearExpired()
    }

}