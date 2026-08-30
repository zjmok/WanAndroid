package org.example.wan.android.data.local.cache

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 磁盘缓存实现
 * 使用文件存储，缓存目录: context.cacheDir/network_cache
 * 线程安全，使用 Mutex 保证并发安全
 * diskcache 和 context 都是与进程生命周期同步，不存在内存泄露，故忽略 StaticFieldLeak
 */
@SuppressLint("StaticFieldLeak")
object DiskCache : RequestCache {

    // 单例初始化标志
    private var context: Context? = null

    // JSON 序列化
    private val gson = Gson()

    // 缓存目录
    private var cacheDir: File? = null

    // 线程锁
    private val lock = Mutex()

    /**
     * 初始化磁盘缓存
     * @param ctx Application Context
     */
    fun init(ctx: Context) {
        if (context == null) {
            context = ctx.applicationContext
            // 缓存目录: /data/data/{package}/cache/network_cache
            cacheDir = File(ctx.cacheDir, "network_cache").apply { mkdirs() }
        }
    }

    // 确保已初始化
    private fun ensureInitialized() {
        if (context == null) {
            throw IllegalStateException("DiskCache not initialized, call init() first")
        }
    }

    /**
     * 获取缓存
     * @param cacheKey 缓存键
     * @return 缓存数据，null 表示无缓存或已过期
     */
    override suspend fun get(cacheKey: String): String? {
        ensureInitialized()
        return lock.withLock {
            withContext(Dispatchers.IO) {
                val dir = cacheDir ?: return@withContext null
                val file = File(dir, cacheKey)
                if (!file.exists()) return@withContext null

                // 读取并反序列化缓存
                val cacheItem = try {
                    file.readText().let { gson.fromJson(it, CacheItem::class.java) }
                } catch (e: Exception) {
                    null
                }

                // 检查是否过期，过期则删除
                if (cacheItem?.isExpired == true) {
                    file.delete()
                    return@withContext null
                }

                cacheItem?.data
            }
        }
    }

    /**
     * 保存缓存
     * @param cacheKey 缓存键
     * @param data 缓存数据 (JSON 字符串)
     * @param expireTime 过期时间 (毫秒)
     */
    override suspend fun put(cacheKey: String, data: String, expireTime: Long) {
        ensureInitialized()
        lock.withLock {
            withContext(Dispatchers.IO) {
                val dir = cacheDir ?: return@withContext
                try {
                    val cacheItem = CacheItem(
                        data = data,
                        timestamp = System.currentTimeMillis(),
                        expireTime = expireTime
                    )
                    val file = File(dir, cacheKey)
                    file.writeText(gson.toJson(cacheItem))
                } catch (ignore: Exception) {

                }
            }
        }
    }

    /**
     * 清除缓存
     * @param cacheKey null 表示清除所有，否则清除指定键
     */
    override suspend fun clear(cacheKey: String?) {
        ensureInitialized()
        lock.withLock {
            val dir = cacheDir ?: return@withLock
            try {
                if (cacheKey == null) {
                    // 清除所有缓存
                    dir.delete()
                } else {
                    val file = File(dir, cacheKey)
                    file.delete()
                }
            } catch (ignore: Exception) {

            }
        }
    }

    /**
     * 清除过期缓存
     */
    override suspend fun clearExpired() {
        ensureInitialized()
        lock.withLock {
            val dir = cacheDir ?: return@withLock
            try {
                dir.listFiles()?.forEach { file ->
                    try {
                        val cacheItem = file.readText().let { gson.fromJson(it, CacheItem::class.java) }
                        if (cacheItem?.isExpired == true) {
                            file.delete()
                        }
                    } catch (ignore: Exception) {
                        file.delete()
                    }
                }
            } catch (ignore: Exception) {

            }
        }
    }

}