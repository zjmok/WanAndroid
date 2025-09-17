package org.example.wan.android

import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}
