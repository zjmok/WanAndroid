package com.example.wan.android.data.local.cache

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

// 磁盘缓存
class DiskCache(
    private val context: Context,
    private val gson: Gson
) : RequestCache {

    // 磁盘缓存实现
    private val cacheDir by lazy { File(context.cacheDir, "network_cache").apply { mkdirs() } }
    private val lock = Mutex()

    override suspend fun get(cacheKey: String): String? {
        return lock.withLock {
            withContext(Dispatchers.IO) {
                val file = File(cacheDir, cacheKey)
                if (!file.exists()) return@withContext null

                val cacheItem = try {
                    file.readText().let { gson.fromJson(it, CacheItem::class.java) }
                } catch (e: Exception) {
                    null
                }

                if (cacheItem?.isExpired == true) {
                    file.delete()
                    return@withContext null
                }

                cacheItem?.data
            }
        }
    }

    override suspend fun put(cacheKey: String, data: String, expireTime: Long) {
        lock.withLock {
            withContext(Dispatchers.IO) {
                try {
                    val cacheItem = CacheItem(
                        data = data,
                        timestamp = System.currentTimeMillis(),
                        expireTime = expireTime
                    )
                    val file = File(cacheDir, cacheKey)
                    file.writeText(gson.toJson(cacheItem))
                } catch (ignore: Exception) {

                }
            }
        }
    }

    override suspend fun clear(cacheKey: String?) {
        lock.withLock {
            try {
                if (cacheKey == null) {
                    cacheDir.delete()
                } else {
                    val file = File(cacheDir, cacheKey)
                    file.delete()
                }
            } catch (ignore: Exception) {

            }
        }
    }

    override suspend fun clearExpired() {
        lock.withLock {
            try {
                cacheDir.listFiles()!!.forEach { file ->
                    val cacheItem = file.readText().let { gson.fromJson(it, CacheItem::class.java) }
                    if (cacheItem?.isExpired == true) {
                        file.delete()
                    }
                }
            } catch (ignore: Exception) {

            }
        }
    }

}