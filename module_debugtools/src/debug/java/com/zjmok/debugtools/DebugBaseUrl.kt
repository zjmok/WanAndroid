package com.zjmok.debugtools

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.core.net.toUri
import com.zjmok.debugtools.DebugBaseUrl.defaultBaseUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * BaseUrl 调试数据层。
 *
 * 持久化保存调试期间修改的 scheme / host / port，供 [DebugBaseUrlInterceptor] 读取。
 */
internal object DebugBaseUrl {

    private const val PREFS_NAME = "float_window_prefs"
    private const val KEY_SCHEME = "scheme"
    private const val KEY_HOST = "host"
    private const val KEY_PORT = "port"

    private lateinit var appContext: Context
    private var defaultBaseUrl: String = ""

    /**
     * 必须在 Application.onCreate 中调用，传入应用上下文与默认 BaseUrl。
     */
    fun init(context: Context, defaultBaseUrl: String) {
        this.appContext = context.applicationContext
        this.defaultBaseUrl = defaultBaseUrl
    }

    private val prefs: SharedPreferences
        get() = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val defaultUri
        get() = defaultBaseUrl.toUri()

    val scheme: String?
        get() = prefs.getString(KEY_SCHEME, defaultUri.scheme)

    val host: String?
        get() = prefs.getString(KEY_HOST, defaultUri.host)

    val port: String
        get() = prefs.getString(KEY_PORT, "${defaultUri.port.takeIf { it > 0 } ?: ""}") ?: ""

    fun saveUrl(scheme: String, host: String, port: String = "") {
        prefs.edit {
            putString(KEY_SCHEME, scheme)
            putString(KEY_HOST, host)
            putString(KEY_PORT, port)
        }
    }

    fun clearUrl() {
        prefs.edit {
            remove(KEY_SCHEME)
            remove(KEY_HOST)
            remove(KEY_PORT)
        }
    }

    /**
     * 当前生效的 BaseUrl。若保存的值无效则返回 [defaultBaseUrl]。
     */
    val currentUrl: String
        get() {
            if (defaultBaseUrl.isEmpty()) return ""
            val url = "${scheme}://${host}:${port}/"
            return try {
                url.toHttpUrl()
                url
            } catch (e: Exception) {
                defaultBaseUrl
            }
        }
}
