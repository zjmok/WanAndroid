package com.zjmok.debugtools

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import androidx.core.content.edit
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * DebugBaseUrl - 跨项目可复用的 BaseUrl 运行时切换数据层
 *
 * 设计要点：
 * - 通过 [DebugToolsInitializer]（ContentProvider）自动初始化，集成方零侵入
 * - 仅在 ApplicationInfo.FLAG_DEBUGGABLE 时返回有效 BaseUrl；release 直接返回 null（Interceptor 放行）
 * - 持久化方案使用 SharedPreferences（模块独立文件），不依赖项目特定存储
 *
 * 集成方式：
 * 1. `implementation project(":module_debugtools")` 或 maven 坐标
 * 2. 在 OkHttpClient 构造时 `.addInterceptor(DebugBaseUrlInterceptor())`
 *
 * 使用方式（debug 期）：
 * - 通过 [saveUrl] / [clearUrl] / [getUrlParts] 读写自定义 BaseUrl
 * - 通常由调试 UI（如悬浮窗）调用，UI 由集成方提供
 */
object DebugBaseUrl {

    private const val PREFS_NAME = "debug_baseurl_prefs"
    private const val KEY_SCHEME = "scheme"
    private const val KEY_HOST = "host"
    private const val KEY_PORT = "port"

    private var app: Application? = null

    /**
     * 由 [DebugToolsInitializer] 在 Application 创建后调用。
     * 也可由集成方在 Application.onCreate() 显式调用（不依赖 ContentProvider）。
     */
    fun init(app: Application) {
        this.app = app
    }

    /** 是否已初始化 */
    fun isInit(): Boolean = app != null

    /**
     * 当前是否处于可激活状态（debuggable）。
     * release 构建（不可调试）下返回 false。
     */
    val isDebuggable: Boolean
        get() {
            val a = app ?: return false
            return try {
                (a.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            } catch (e: Exception) {
                false
            }
        }

    private val prefs: SharedPreferences?
        get() = app?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 读取当前保存的自定义 BaseUrl 字符串。
     * - 未初始化、release 构建、未保存、保存内容为空：返回 null（Interceptor 会放行使用默认 BaseUrl）
     * - 保存内容无法解析为合法 URL：返回 null
     */
    fun getBaseUrl(): String? {
        if (!isDebuggable) return null
        val p = prefs ?: return null
        val scheme = p.getString(KEY_SCHEME, null) ?: return null
        val host = p.getString(KEY_HOST, null) ?: return null
        if (host.isBlank()) return null
        val port = p.getString(KEY_PORT, "") ?: ""
        val url = buildString {
            append(scheme)
            append("://")
            append(host)
            if (port.isNotBlank()) {
                append(":").append(port)
            }
            append("/")
        }
        return try {
            url.toHttpUrl()
            url
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 读取当前保存的 BaseUrl 各部分，用于 UI 回显。
     * 返回 null 表示未保存或不可激活。
     */
    fun getUrlParts(): UrlParts? {
        if (!isDebuggable) return null
        val p = prefs ?: return null
        val scheme = p.getString(KEY_SCHEME, null) ?: return null
        val host = p.getString(KEY_HOST, null) ?: return null
        val port = p.getString(KEY_PORT, "") ?: ""
        return UrlParts(scheme = scheme, host = host, port = port)
    }

    /**
     * 保存自定义 BaseUrl。
     * 传空字符串表示清空对应字段。
     */
    fun saveUrl(scheme: String, host: String, port: String = "") {
        val p = prefs ?: return
        p.edit {
            putString(KEY_SCHEME, scheme)
            putString(KEY_HOST, host)
            putString(KEY_PORT, port)
        }
    }

    /**
     * 清除自定义 BaseUrl，恢复使用默认 BaseUrl。
     */
    fun clearUrl() {
        val p = prefs ?: return
        p.edit {
            remove(KEY_SCHEME)
            remove(KEY_HOST)
            remove(KEY_PORT)
        }
    }

    /** BaseUrl 各部分（用于 UI 回显） */
    data class UrlParts(val scheme: String, val host: String, val port: String)
}
