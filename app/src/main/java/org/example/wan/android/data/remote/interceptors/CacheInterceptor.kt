package org.example.wan.android.data.remote.interceptors

import android.util.Log
import com.blankj.utilcode.util.NetworkUtils
import org.example.wan.android.util.formatSecondsToDHMS
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/*
通过一个具体的示例来说明 `max-age`、`max-stale` 和 `min-fresh` 同时使用时的效果。

假设以下缓存策略：
```http
Cache-Control: max-age=3600, max-stale=7200, min-fresh=600
```

- **`max-age=3600`**：缓存的响应在 3600 秒（1 小时）内是新鲜的。
- **`max-stale=7200`**：即使缓存的响应已经过期，但在 7200 秒（2 小时）内，客户端仍然可以接受这些过期的响应。
- **`min-fresh=600`**：客户端希望缓存的响应在未来 600 秒（10 分钟）内仍然有效。

### 示例场景

1. **请求发生在缓存生成后的 30 分钟内**：
   - 缓存的响应仍然在 `max-age` 时间内（3600 秒），所以客户端会直接使用缓存的响应。

2. **请求发生在缓存生成后的 1 小时 30 分钟内**：
   - 缓存的响应已经超过了 `max-age` 时间（3600 秒），但在 `max-stale` 时间内（7200 秒），所以客户端仍然可以接受这个过期的响应。

3. **请求发生在缓存生成后的 2 小时 30 分钟内**：
   - 缓存的响应已经超过了 `max-stale` 时间（7200 秒），所以客户端不会使用这个过期的响应，会重新请求服务器获取新的响应。

4. **请求发生在缓存生成后的 50 分钟内，并且客户端希望响应在未来 10 分钟内仍然有效**：
   - 缓存的响应在 `max-age` 时间内（3600 秒），但客户端希望响应在未来 10 分钟内（600 秒）仍然有效。因为 50 分钟 + 10 分钟 = 60 分钟，仍然在 `max-age` 时间内，所以客户端会直接使用缓存的响应。

### 总结

- **`max-age`**：决定缓存的响应在多长时间内是新鲜的。
- **`max-stale`**：决定客户端可以接受的过期响应的最大时间。
- **`min-fresh`**：决定客户端希望响应在未来多长时间内仍然有效。
*/
class CacheInterceptor : Interceptor {

    companion object {
        private const val MAX_AGE = 60 * 60 * 24 * 1
        private const val MAX_STALE = 60 * 60 * 24 * 30
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        return if (NetworkUtils.isAvailable()) {

            Log.i("CacheInterceptor", "network is available")

            val response = chain.proceed(request)

            val cacheControl = request.cacheControl.toString()
            Log.i("CacheInterceptor", "max-age=${formatSecondsToDHMS(MAX_AGE)} (${cacheControl})")

            response.newBuilder()
                .removeHeader("Pragma")
                .header(
                    "Cache-Control",
                    "public, max-age=$MAX_AGE"
                )
                .build()
        } else {

            Log.i("CacheInterceptor", "network is unavailable")

            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()

            val response = chain.proceed(request)

            val cacheControl = request.cacheControl.toString()
            Log.i("CacheInterceptor", "max-stale=${formatSecondsToDHMS(MAX_STALE)} (${cacheControl})")

            response.newBuilder()
                .removeHeader("Pragma")
                .header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=$MAX_STALE"
                )
                .build()
        }
    }

}