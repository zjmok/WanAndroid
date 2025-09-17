package org.example.wan.android.data.remote.api

import org.example.wan.android.data.model.Articles
import org.example.wan.android.data.model.ArticlesTreeItem
import org.example.wan.android.data.model.BannerItem
import org.example.wan.android.data.model.CommentList
import org.example.wan.android.data.model.DataX
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WanService {

    /*
        // 会在 请求体 以 json 提交
        @POST("/login")
        suspend fun postTest(@Body user: User): ApiResult<Any>

        // 请求头指定内容为 json 类型, 可直接传 String 类型的 json
        @Headers("Content-Type: application/json")
        @POST("/login")
        suspend fun postTest2(@Body json: String): ApiResult<Any>

        // 以表单方式提交
        @FormUrlEncoded
        @POST("/login")
        suspend fun postTest3(
            @Field("username") username: String,
            @Field("password") password : String,
        ): ApiResult<Any>
    */

        // [@Part] 后面支持三种类型，[RequestBody]、[MultipartBody.Part] 、任意类型
        // 除 [MultipartBody.Part] 以外，其它类型都必须带上表单字段
        // ([MultipartBody.Part] 中已经包含了表单字段的信息)

        // 可
        // @Part("info") info: RequestBody,
        // @Part downloadUrl: MultipartBody.Part,

        // 可
        // @Part partList: List<MultipartBody.Part>
/*
        // 第一种 单个文件 需要写明字段名`@Part("file")`
        @Multipart
        @POST("/upload/path")
        suspend fun uploadTest(
            @Part("name") reqBody: RequestBody
        ): ApiResult<ResponseBody>

        // 第一种 多个文件 需要在 Map 的 key 写明字段名
        @Multipart
        @POST("/upload/path")
        suspend fun uploadTest2(
            @PartMap reqBodyMap: Map<String, RequestBody>
        ): ApiResult<ResponseBody>

        // 第二种 单文件 注解不用写字段名 Part里面已经包含字段名
        @Multipart
        @POST("/upload/path")
        suspend fun uploadTest3(
            @Part part: MultipartBody.Part
        ): ApiResult<ResponseBody>

        // 第二种 多文件 注解不用写字段名 Part里面已经包含字段名
        @Multipart
        @POST("/upload/path")
        suspend fun uploadTest4(
            @Part partList: List<MultipartBody.Part>
        ): ApiResult<ResponseBody>

        // 第三种 单文件 需要写明字段名`@Part("file") file: File` ? 未实践
        @Multipart
        @POST("/upload/path")
        suspend fun uploadTest5(
            @Part("file") file: File
        ): ApiResult<ResponseBody>

        // 第三种 多文件 `@Part fileMap: Map<String, File>` ? 未实践
        @Multipart
        @POST("/upload/path")
        suspend fun uploadTest5(
            @PartMap fileMap: Map<String, File>
        ): ApiResult<ResponseBody>
*/

    // banner
    @GET("/banner/json")
    suspend fun getBanner(): ApiResult<List<BannerItem>>

    // 置顶文章
    @GET("/article/top/json")
    suspend fun getHomeTopList(): ApiResult<List<DataX>>

    // 首页文章列表
    @GET("/article/list/{page}/json")
    suspend fun getHomeList(
        @Path("page") page: Int = 0,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 项目分类
    @GET("/project/tree/json")
    suspend fun getProjectTree(): ApiResult<ArrayList<ArticlesTreeItem>>

    // 项目列表数据
    @GET("/project/list/{page}/json")
    suspend fun getProjectList(
        @Path("page") page: Int = 1,
        @Query("cid") id: Int,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 最新项目 Tab (可以作为一个分类 插在'项目分类'最前面)
    @GET("/article/listproject/{page}/json")
    suspend fun getNewProjectList(
        @Path("page") page: Int = 0,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    @GET("/wxarticle/chapters/json")
    suspend fun getWxArticleTree(): ApiResult<ArrayList<ArticlesTreeItem>>

    // 获取公众号列表
    // 查看某个公众号历史数据
    @GET("/wxarticle/list/{id}/{page}/json")
    suspend fun getWxArticleList(
        @Path("id") id: Int,
        @Path("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 在某个公众号中搜索历史文章
    @GET("/wxarticle/list/{id}/{page}/json")
    suspend fun searchWxArticleList(
        @Path("id") id: Int,
        @Path("page") page: Int = 1,
        @Query("k") k: String,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 搜索 (支持多个关键词，用空格隔开)
    @FormUrlEncoded
    @POST("/article/query/{page}/json")
    suspend fun search(
        @Field("k") k: String,
        @Path("page") page: Int = 0,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 问答
    @GET("/wenda/list/{page}/json")
    suspend fun getQAList(
        @Path("page") page: Int = 1,
        // 接口 bug, 传了 page_size 返回列表没有置顶数据, 不传是正常的
//        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<Articles>

    // 问答评论列表
    @GET("/wenda/comments/{id}/json")
    suspend fun getQACommentList(
        @Path("id") id: Int = 1,
        @Query("page_size") pageSize: Int = 10,
    ): ApiResult<CommentList>

}