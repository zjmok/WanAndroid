package com.zjmok.util

import android.app.Application
import android.content.pm.ApplicationInfo

object UtilLib {

    private var app: Application? = null

    fun init(app: Application) {
        this.app = app
    }

    fun isInit(): Boolean {
        return app != null
    }

    internal fun checkInit() {
        app ?: throw IllegalStateException("请使用 UtilLib.init() 初始化")
    }

    internal val App
        get(): Application {
            checkInit()
            return app!!
        }

    internal val isDebug
        get(): Boolean {
            return try {
                ApplicationInfo.FLAG_DEBUGGABLE
                (App.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            } catch (e: Exception) {
                false
            }
        }

}
