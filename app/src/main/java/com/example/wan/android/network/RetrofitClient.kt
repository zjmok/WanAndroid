package com.example.wan.android.network

import com.example.wan.android.App
import com.example.wan.android.constant.AppConst
import com.example.wan.android.network.interceptors.CacheInterceptor
import com.example.wan.android.network.interceptors.LoggingInterceptor
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val cookieJar =
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(App.INSTANCE))

    /*
    ### `addInterceptor`
    - **调用时机**：无论网络是否可用，`addInterceptor` 都会被调用。
    - **调用次数**：只会被调用一次，即使 HTTP 响应是从缓存中获取的。
    - **用途**：适用于应用层的拦截，可以在请求发送前或响应接收后进行处理。常用于添加通用的请求头、日志记录、重试机制等。
    - **特点**：
      - 可以短路请求，不调用 `chain.proceed()`，直接返回自定义的响应。
      - 不关心 OkHttp 注入的头信息，如 `If-None-Match`。

    ### `addNetworkInterceptor`
    - **调用时机**：只有在网络请求实际发送到服务器时才会被调用。如果请求从缓存中获取，则不会调用。
    - **调用次数**：可以多次调用，特别是在重定向和重试的情况下。
    - **用途**：适用于网络层的拦截，可以在请求发送到服务器前或响应接收后进行处理。常用于处理网络层的细节，如压缩、解压缩、网络日志等。
    - **特点**：
      - 可以操作中间过程的响应，如重定向和重试。
      - 可以访问连接信息，通过 `chain.connection()` 获取连接的详细信息，如服务器的 IP 地址和 TLS 配置信息。
    */
    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true) // 默认重试一次，若需要重试N次，则要实现拦截器。
        .cache(Cache(File(AppConst.okhttpCachePath), AppConst.OKHTTP_CACHE_SIZE))
//        .addNetworkInterceptor(CacheInterceptor())
        // 先添加的拦截器，会先处理请求，最后处理响应
        // 日志拦截器通常放在最前面，以便记录所有请求和响应的详细信息。
        // 缓存拦截器通常放在靠前的位置，以便在其它拦截器之前处理缓存逻辑。
        .addInterceptor(LoggingInterceptor())
        .addInterceptor(CacheInterceptor())
//        .addInterceptor(HeaderInterceptor())
//        .addInterceptor(CookiesInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(AppConst.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .disableHtmlEscaping() // 禁止 Html 转义
                    .create()
            )
        )
        .build()

    fun clearCookie() {
        cookieJar.clear()
    }

    fun clearCache() {
        okHttpClient.cache?.evictAll()
    }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

}
