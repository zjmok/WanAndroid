package org.example.wan.android.data.local.cache

// 缓存数据类
data class CacheItem(
    val data: String,
    val timestamp: Long,
    val expireTime: Long
){
    val isExpired: Boolean
        get() = System.currentTimeMillis() - timestamp > expireTime
}
