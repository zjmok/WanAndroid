package com.zjmok.debugtools

import android.content.Context
import android.content.Intent

object DebugTools {

    fun init(context: Context, defaultBaseUrl: String) {
        val appContext = context.applicationContext
        DebugBaseUrl.init(appContext, defaultBaseUrl)
        appContext.startService(Intent(appContext, DebugToolsService::class.java))
    }

}
