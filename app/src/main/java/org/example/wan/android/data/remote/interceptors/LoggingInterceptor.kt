package org.example.wan.android.data.remote.interceptors

import com.zjmok.util.loge
import com.zjmok.util.logeLong
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.example.wan.android.BuildConfig
import java.io.IOException
import java.nio.charset.Charset

class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (BuildConfig.DEBUG.not()) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()
        val t1 = System.nanoTime()

        val msgRequest = "request(${request.method}): ${request.url}\n" +
//                "${request.headers.toStringCustom().trim()}\n" +
                getRequestInfo(request)
        loge(any = msgRequest, tag = TAG)

        val response = chain.proceed(request)
        val t2 = System.nanoTime()

        // 判断数据来源
        val isFromNetwork = response.networkResponse != null
        val isFromCache = response.cacheResponse != null
        val from = if (isFromNetwork) {
            "network"
        } else if (isFromCache) {
            "cache"
        } else {
            "???"
        }

        val msgResponse = "$from response for ${response.request.url} in ${(t2 - t1) / 1e6} ms\n" +
//                "${response.headers.toStringCustom().trim()}\n" +
                getResponseInfo(response)
        // 长内容会被截断，需要分块
        logeLong(msgResponse, TAG)

        return response
    }

    /**
     * 打印返回消息
     * @param response 返回的对象
     */
    private fun getResponseInfo(response: Response?): String {
        if (response == null) {
            return ""
        }
        if (!response.isSuccessful) {
            return response.body?.string() ?: ""
        }
        val responseBody = response.body ?: return ""
        val source = responseBody.source()
        try {
            source.request(Long.MAX_VALUE) // Buffer the entire body.
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val buffer: Buffer = source.buffer
        val charset: Charset = Charset.forName("utf-8")
        val str = buffer.clone().readString(charset)
        return str
    }

    /**
     * 打印请求体
     * @param request 请求的对象
     */
    private fun getRequestInfo(request: Request): String {
        val requestBody = request.body
        val buffer = Buffer()
        try {
            if (requestBody != null) {
                requestBody.writeTo(buffer)
            } else {
                return ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        var charset = Charset.forName("UTF-8")
        val contentType = requestBody.contentType()
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"))
        }
        val str = buffer.readString(charset)
        return str
    }

    companion object {
        const val TAG = "LoggingInterceptor"
    }

}
