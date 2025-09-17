package org.example.wan.android.data.remote.api

import org.example.wan.android.data.model.SuperUserInfo
import org.example.wan.android.data.model.UserInfo
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 用户模块
 */
interface UserService {

    // 注册
    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
    ): ApiResult<UserInfo>

    // 登录
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): ApiResult<UserInfo>

    // 登出
    @GET("/user/logout/json")
    suspend fun logout(): ApiResult<ResponseBody>

    // 个人信息接口
    @GET("/user/lg/userinfo/json")
    suspend fun getUserInfo(): ApiResult<SuperUserInfo>

    // 未读消息数量
    @GET("/message/lg/count_unread/json")
    suspend fun getUnreadMessage(): ApiResult<ResponseBody>

    // 已读消息列表
    @GET("/message/lg/readed_list/{page}/json")
    suspend fun getReadMessage(
        @Path("page") page: Int = 1,
    ): ApiResult<ResponseBody>

    // 未读消息列表
    @GET("/message/lg/unread_list/{page}/json")
    suspend fun getUnreadMessageList(
        @Path("page") page: Int = 1,
    ): ApiResult<ResponseBody>

}