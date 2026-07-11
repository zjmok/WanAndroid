package org.example.wan.android.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.blankj.utilcode.util.LogUtils
import org.example.wan.android.data.local.cache.CompositeCache
import org.example.wan.android.data.local.cache.DiskCache
import org.example.wan.android.data.local.cache.MemoryCache
import org.example.wan.android.data.local.cache.RequestCache
import org.example.wan.android.data.remote.RetrofitClient
import org.example.wan.android.data.remote.api.LikeService
import org.example.wan.android.data.remote.api.SquareService
import org.example.wan.android.data.remote.api.UserService
import org.example.wan.android.data.remote.api.WanService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "WanRepository"

private const val CACHE_ENABLE = true

private const val CACHE_TIME_BANNER = 30 * 60 * 1000L
private const val CACHE_TIME_TOP_LIST = 30 * 60 * 1000L
private const val CACHE_TIME_TREE = 24 * 60 * 60 * 1000L
private const val CACHE_TIME_LIST = 10 * 60 * 1000L

/**
 * WanAndroid 数据仓库
 * 单一数据源，给 ViewModel 提供数据
 * 支持三级缓存：Memory -> Disk -> Network
 */
object WanRepository {

    // API 服务
    private val apiService by lazy { RetrofitClient.create(WanService::class.java) }
    private val userService by lazy { RetrofitClient.create(UserService::class.java) }
    private val squareService by lazy { RetrofitClient.create(SquareService::class.java) }
    private val likeService by lazy { RetrofitClient.create(LikeService::class.java) }

    // JSON 序列化
    private val gson = Gson()

    // 三级缓存 (Memory + Disk)
    private lateinit var cache: CompositeCache
    private var initialized = false

    // 异步更新缓存用的协程作用域
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    /**
     * 初始化仓库，需在 Application 中调用
     * @param context Application Context
     */
    fun init(context: Context) {
        if (!initialized) {
            DiskCache.init(context)
            cache = CompositeCache(MemoryCache(), DiskCache)
            initialized = true
            LogUtils.dTag(TAG, "Cache initialized")
        }
    }

    /**
     * 带缓存的请求方法
     * 策略：有缓存立即返回，同时异步请求网络更新缓存
     *
     * @param cacheKey 缓存键
     * @param expireTime 缓存过期时间 (毫秒)
     * @param fetch 网络请求 lambda
     * @return 请求结果
     */
    private suspend inline fun <reified T> getWithCache(
        cacheKey: String,
        expireTime: Long,
        crossinline fetch: suspend () -> T
    ): T where T : Any {
        // 缓存禁用时直接请求网络
        if (!CACHE_ENABLE) {
            return fetch()
        }

        // 保留泛型类型，避免 Gson 擦除 T（否则 data 会被反序列化成 LinkedTreeMap）
        val cacheType = object : TypeToken<CacheResult<T>>() {}.type

        // 1. 先从缓存获取 (三级: Memory -> Disk)
        val cachedData = cache.get(cacheKey)
        if (cachedData != null) {
            // 2. 解析缓存数据
            val cached = try {
                gson.fromJson<CacheResult<T>>(cachedData, cacheType)
            } catch (e: Exception) {
                null
            }
            if (cached != null) {
                // 3. 有缓存，立即返回
                // 4. 同时发起异步网络请求更新缓存
                repositoryScope.launch {
                    try {
                        val freshData = fetch()
                        val json = gson.toJson(CacheResult(data = freshData), cacheType)
                        cache.put(cacheKey, json, expireTime)
                    } catch (e: Exception) {
                        LogUtils.eTag(TAG, "refresh cache error: ${e.message}")
                    }
                }
                return cached.data
            }
        }

        // 5. 无缓存，请求网络并缓存结果
        val data = fetch()
        if (data != null) {
            val json = gson.toJson(CacheResult(data = data), cacheType)
            cache.put(cacheKey, json, expireTime)
        }
        return data
    }

    // 缓存结果包装类
    private class CacheResult<T>(val data: T)

    suspend fun register(username: String, password: String) = withContext(Dispatchers.IO) {
        userService.register(username, password, password).apiData()!!
    }

    suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        userService.login(username, password).apiData()!!
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userService.logout().apiData()
    }

    suspend fun getUserInfo() = withContext(Dispatchers.IO) {
        userService.getUserInfo().apiData()!!
    }

    suspend fun getBanner() = withContext(Dispatchers.IO) {
        getWithCache("banner", CACHE_TIME_BANNER) {
            apiService.getBanner().apiData()!!
        }
    }

    suspend fun getHomeTopList() = withContext(Dispatchers.IO) {
        getWithCache("home_top_list", CACHE_TIME_TOP_LIST) {
            apiService.getHomeTopList().apiData()!!
        }
    }

    suspend fun getHomeList(page: Int) = withContext(Dispatchers.IO) {
        apiService.getHomeList(page = page).apiData()!!
    }

    suspend fun getProjectTree() = withContext(Dispatchers.IO) {
        getWithCache("project_tree", CACHE_TIME_TREE) {
            apiService.getProjectTree().apiData()!!
        }
    }

    suspend fun getProjectList(id: Int, page: Int) = withContext(Dispatchers.IO) {
        val cacheKey = "project_list_${id}_$page"
        getWithCache(cacheKey, CACHE_TIME_LIST) {
            apiService.getProjectList(id = id, page = page).apiData()!!
        }
    }

    suspend fun getNewProjectList(page: Int) = withContext(Dispatchers.IO) {
        apiService.getNewProjectList(page = page).apiData()!!
    }

    suspend fun getSquareList(page: Int) = withContext(Dispatchers.IO) {
        squareService.getSquareList(page = page).apiData()!!
    }

    suspend fun getWxArticleTree() = withContext(Dispatchers.IO) {
        getWithCache("wx_article_tree", CACHE_TIME_TREE) {
            apiService.getWxArticleTree().apiData()!!
        }
    }

    suspend fun getWxArticleList(id: Int, page: Int) = withContext(Dispatchers.IO) {
        val cacheKey = "wx_article_list_${id}_$page"
        getWithCache(cacheKey, CACHE_TIME_LIST) {
            apiService.getWxArticleList(id = id, page = page).apiData()!!
        }
    }

    suspend fun searchWxArticleList(id: Int, key: String, page: Int) = withContext(Dispatchers.IO) {
        apiService.searchWxArticleList(id = id, k = key, page = page).apiData()!!
    }

    suspend fun likeArticle(id: Int) = withContext(Dispatchers.IO) {
        likeService.likeArticle(id).apiData()
    }

    suspend fun unlikeArticle(id: Int) = withContext(Dispatchers.IO) {
        likeService.unlikeArticle(id).apiData()
    }

    suspend fun unlikeMyLike(id: Int, originId: Int) = withContext(Dispatchers.IO) {
        likeService.unlikeMyLike(id = id, originId = originId).apiData()
    }

    suspend fun getLikeList(page: Int) = withContext(Dispatchers.IO) {
        likeService.getLikeList(page).apiData()!!
    }

    suspend fun search(key: String, page: Int) = withContext(Dispatchers.IO) {
        apiService.search(k = key, page = page).apiData()!!
    }

    suspend fun getQAList(page: Int) = withContext(Dispatchers.IO) {
        apiService.getQAList(page = page).apiData()!!
    }

    suspend fun getQACommentList(id: Int) = withContext(Dispatchers.IO) {
        apiService.getQACommentList(id = id).apiData()!!
    }

}