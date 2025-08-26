package com.example.wan.android.data.repository

import com.example.wan.android.data.local.cache.MemoryCache
import com.example.wan.android.data.local.cache.RequestCache
import com.example.wan.android.data.remote.RetrofitClient
import com.example.wan.android.data.remote.api.LikeService
import com.example.wan.android.data.remote.api.SquareService
import com.example.wan.android.data.remote.api.UserService
import com.example.wan.android.data.remote.api.WanService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 单一数据源 给 ViewModel 提供数据
 */
object WanRepository {

    private val apiService by lazy { RetrofitClient.create(WanService::class.java) }
    private val userService by lazy { RetrofitClient.create(UserService::class.java) }
    private val squareService by lazy { RetrofitClient.create(SquareService::class.java) }
    private val likeService by lazy { RetrofitClient.create(LikeService::class.java) }

    private val cache: RequestCache = MemoryCache()

    suspend fun register(username: String, password: String) = withContext(Dispatchers.IO) {
        userService.register(username, password, password).apiData()!!
    }

    suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        userService.login(username, password).apiData()!!
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        // null
        userService.logout().apiData()
    }

    suspend fun getUserInfo() = withContext(Dispatchers.IO) {
        userService.getUserInfo().apiData()!!
    }

    suspend fun getBanner() = withContext(Dispatchers.IO) {
        apiService.getBanner().apiData()!!
    }

    suspend fun getHomeTopList() = withContext(Dispatchers.IO) {
        apiService.getHomeTopList().apiData()!!
    }

    suspend fun getHomeList(page: Int) = withContext(Dispatchers.IO) {
        apiService.getHomeList(page = page).apiData()!!
    }

    suspend fun getProjectTree() = withContext(Dispatchers.IO) {
        apiService.getProjectTree().apiData()!!
    }

    suspend fun getProjectList(id: Int, page: Int) = withContext(Dispatchers.IO) {
        apiService.getProjectList(id = id, page = page).apiData()!!
    }

    suspend fun getNewProjectList(page: Int) = withContext(Dispatchers.IO) {
        apiService.getNewProjectList(page = page).apiData()!!
    }

    suspend fun getSquareList(page: Int) = withContext(Dispatchers.IO) {
        squareService.getSquareList(page = page).apiData()!!
    }

    suspend fun getWxArticleTree() = withContext(Dispatchers.IO) {
        apiService.getWxArticleTree().apiData()!!
    }

    suspend fun getWxArticleList(id: Int, page: Int) = withContext(Dispatchers.IO) {
        apiService.getWxArticleList(id = id, page = page).apiData()!!
    }

    suspend fun searchWxArticleList(id: Int, key: String, page: Int) = withContext(Dispatchers.IO) {
        apiService.searchWxArticleList(id = id, k = key, page = page).apiData()!!
    }

    suspend fun likeArticle(id: Int) = withContext(Dispatchers.IO) {
        // 此接口会返回 "" Retrofit 解析为 null
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