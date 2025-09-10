package com.example.wan.android

import com.example.wan.android.constant.AppConst
import com.example.wan.android.util.log
import com.example.wan.android.util.loge
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val baseUrl = try {
            FloatButtonApp.debugBaseUrl.toHttpUrl()
        } catch (e: Exception) {
            loge("无效的 BaseUrl, 使用默认的 BaseUrl", "BaseUrlInterceptor")
            AppConst.BASE_URL.toHttpUrl()
        }

        val oldRequest = chain.request()
        val oldHttpUrl = oldRequest.url

        if (oldHttpUrl.scheme == baseUrl.scheme &&
            oldHttpUrl.host == baseUrl.host &&
            oldHttpUrl.port == baseUrl.port
        ) {
            loge("baseUrl: no change", "BaseUrlInterceptor")
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
//
//        log(
//            "baseUrl: ${oldHttpUrl.scheme}://${oldHttpUrl.host}:${oldHttpUrl.port}" +
//                    " --> " +
//                    "${baseUrl.scheme}://${baseUrl.host}:${baseUrl.port}",
//            "BaseUrlInterceptor"
//        )

        return chain.proceed(newRequest)
    }

}
