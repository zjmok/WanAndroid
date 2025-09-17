package org.example.wan.android.data.remote.api

import org.example.wan.android.data.model.Articles
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 收藏模块
 */
interface LikeService {

    // 收藏文章列表
    @GET("/lg/collect/list/{page}/json")
    suspend fun getLikeList(
        @Path("page") page: Int = 0,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 收藏站内文章
    @POST("/lg/collect/{id}/json")
    suspend fun likeArticle(
        @Path("id") id: Int,
    ): ApiResult<Any>

    // 收藏站外文章
    @FormUrlEncoded
    @POST("/lg/collect/add/json")
    suspend fun likeOutside(
        @Field("title") title: Int,
        @Field("author") author: Int,
        @Field("link") link: Int,
    ): ApiResult<ResponseBody>

    // 编辑收藏的文章，支持站内，站外
    @FormUrlEncoded
    @POST("/lg/collect/user_article/update/{id}/json")
    suspend fun updateLike(
        @Path("id") id: Int,
        @Field("title") title: Int,
        @Field("author") author: Int,
        @Field("link") link: Int,
    ): ApiResult<ResponseBody>

    // 取消收藏 - 文章列表 (首页广场这类文章列表)
    @POST("/lg/uncollect_originId/{id}/json")
    suspend fun unlikeArticle(
        @Path("id") id: Int,
    ): ApiResult<ResponseBody>

    // 取消收藏 - 我的收藏页面（该页面包含自己录入的内容） (上面的 `收藏文章列表` 接口)
    @FormUrlEncoded
    @POST("/lg/uncollect/{id}/json")
    suspend fun unlikeMyLike(
        @Path("id") id: Int,
        @Field("originId") originId: Int,
    ): ApiResult<ResponseBody>

    // 收藏网站列表
    @GET("/lg/collect/usertools/json")
    suspend fun getSiteList(): ApiResult<ResponseBody>

    // 收藏网址
    @FormUrlEncoded
    @POST("/lg/collect/addtool/json")
    suspend fun likeSite(
        @Field("name") name: Int,
        @Field("link") link: Int,
    ): ApiResult<ResponseBody>

    // 编辑收藏网站
    @FormUrlEncoded
    @POST("/lg/collect/updatetool/json")
    suspend fun updateSite(
        @Field("id") id: Int,
        @Field("name") name: Int,
        @Field("link") link: Int,
    ): ApiResult<ResponseBody>

    // 删除收藏网站
    @FormUrlEncoded
    @POST("/lg/collect/deletetool/json")
    suspend fun deleteSite(
        @Field("id") name: Int,
    ): ApiResult<ResponseBody>

}