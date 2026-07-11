package org.example.wan.android

import android.content.Intent

/**
 * debug 专用 Application
 *
 * 职责：启动 [FloatButtonService]。
 *
 * BaseUrl 运行时切换由 [com.zjmok.debugtools.DebugBaseUrl] 提供（独立模块），
 * 此处不再持有 BaseUrl 状态。
 */
class FloatButtonApp : App() {

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, FloatButtonService::class.java))
    }

}
