package com.zjmok.debugtools

import com.zjmok.util.loge
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Debug 变体的 BaseUrl 拦截器。
 *
 * 读取 [DebugBaseUrl.currentUrl] 替换请求的 scheme/host/port。
 * 若保存的值无效则使用默认 BaseUrl。
 */
class DebugBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val baseUrl = try {
            DebugBaseUrl.currentUrl.toHttpUrl()
        } catch (e: Exception) {
            loge("无效的 BaseUrl, 使用默认的 BaseUrl", "BaseUrlInterceptor")
            return chain.proceed(chain.request())
        }

        val oldRequest = chain.request()
        val oldHttpUrl = oldRequest.url

        if (oldHttpUrl.scheme == baseUrl.scheme &&
            oldHttpUrl.host == baseUrl.host &&
            oldHttpUrl.port == baseUrl.port
        ) {
            return chain.proceed(oldRequest)
        }

        val newHttpUrl = oldHttpUrl.newBuilder()
            .scheme(baseUrl.scheme)
            .host(baseUrl.host)
            .port(baseUrl.port)
            .build()
        val newRequest = oldRequest.newBuilder()
            .url(newHttpUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
