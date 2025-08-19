package com.example.wan.android

import android.content.Intent
import androidx.core.net.toUri
import com.example.wan.android.constant.AppConst
import okhttp3.HttpUrl.Companion.toHttpUrl

class FloatButtonApp : App() {

    companion object {
        val debugBaseUrl: String
            get() {
                val prefs = INSTANCE.getSharedPreferences("float_window_prefs", MODE_PRIVATE)
                val defaultUri = AppConst.BASE_URL.toUri()
                val scheme = prefs.getString("scheme", defaultUri.scheme)
                val host = prefs.getString("host", defaultUri.host)
                val port = prefs.getString("port", "${defaultUri.port.takeIf { it > 0 } ?: ""}")
                val url = "${scheme}://${host}:${port}/"
                try {
                    url.toHttpUrl()
                } catch (e: Exception) {
                    // 如果解析失败，返回默认的 BASE_URL
                    return AppConst.BASE_URL
                }
                return url
            }
    }

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, FloatButtonService::class.java))
    }

}
