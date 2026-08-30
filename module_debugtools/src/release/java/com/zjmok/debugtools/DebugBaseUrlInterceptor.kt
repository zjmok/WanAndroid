package com.zjmok.debugtools

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Release 变体的 BaseUrl 拦截器占位实现。
 *
 * Release 不修改 BaseUrl，直接放行请求，确保不打包任何调试逻辑。
 */
class DebugBaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}
