package com.example.wan.android.data.remote.api

import com.example.wan.android.data.model.Articles
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 广场模块
 */
interface SquareService {

    // 广场列表数据
    @GET("/user_article/list/{page}/json")
    suspend fun getSquareList(
        @Path("page") page: Int = 0,
        @Query("page_size") pageSize: Int = 10, // fixme 此接口返回的条数有问题
    ): ApiResult<Articles>

    // 分享人对应列表数据
    @GET("/user/{id}/share_articles/{page}/json")
    suspend fun getUserShareList(
        @Path("id") id: Int,
        @Path("page") page: Int = 1,
    ): ApiResult<Articles>

    // 自己的分享的文章列表
    @GET("/user/lg/private_articles/{page}/json")
    suspend fun getMyShareList(
        @Path("id") id: Int,
        @Path("page") page: Int = 1,
    ): ApiResult<Articles>

    // 删除自己分享的文章
    @POST("/lg/user_article/delete/{id}/json")
    suspend fun deleteMyShare(
        @Path("id") id: Int,
    ): ApiResult<Articles>

    // 分享文章
    @POST("/lg/user_article/add/json")
    suspend fun shareArticle(
        @Field("title") title: Int,
        @Field("link") link: Int,
    ): ApiResult<Articles>

}