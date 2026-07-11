package com.zjmok.debugtools

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * DebugToolsInitializer - ContentProvider 自动初始化入口
 *
 * - 由系统在 Application 实例创建后、Application.onCreate() 之前调用 onCreate()
 * - 自动初始化 [DebugBaseUrl]，集成方无需修改任何代码
 *
 * 此初始化器仅完成 BaseUrl 数据层的初始化，不启动任何 UI。
 * 调试 UI（如悬浮窗）由集成方在自己的 Application 或 Activity 中按需启动。
 */
class DebugToolsInitializer : ContentProvider() {

    override fun onCreate(): Boolean {
        val app = context?.applicationContext as? Application
        if (app != null) {
            DebugBaseUrl.init(app)
        }
        // 返回 false 表示此 ContentProvider 不需要对外提供数据查询能力
        // 但系统会照常创建它，onCreate 仍会被调用
        return true
    }

    // 以下方法均不会被实际调用，ContentProvider 仅为自动初始化而声明

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}
