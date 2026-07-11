package com.zjmok.debugtools

import android.util.Log
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * DebugBaseUrlInterceptor - OkHttp 拦截器
 *
 * 集成方在 OkHttpClient 构造时 `.addInterceptor(DebugBaseUrlInterceptor())` 即可启用。
 *
 * 行为：
 * - [DebugBaseUrl.getBaseUrl] 返回 null（release / 未初始化 / 未配置）：直接放行原请求
 * - 返回非 null：解析为 HttpUrl，重写请求的 scheme/host/port，path/query/fragment 保持不变
 *
 * release 构建中此拦截器仍存在于 OkHttp 链中，但 [DebugBaseUrl.getBaseUrl] 会因
 * ApplicationInfo.FLAG_DEBUGGABLE=false 而立即返回 null，几乎无开销。
 */
class DebugBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val baseUrlString = DebugBaseUrl.getBaseUrl() ?: return chain.proceed(chain.request())

        val baseUrl = try {
            baseUrlString.toHttpUrl()
        } catch (e: Exception) {
            Log.w(TAG, "无效的 BaseUrl, 使用默认的 BaseUrl: $baseUrlString")
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
            .port(baseUrl.port) // toHttpUrl 会把默认 port 加上, http 默认 80, https 默认 443
            .build()
        val newRequest = oldRequest.newBuilder()
            .url(newHttpUrl)
            .build()

        return chain.proceed(newRequest)
    }

    private companion object {
        private const val TAG = "DebugBaseUrlInterceptor"
    }
}
