package com.example.wan.android.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.core.content.ContextCompat

val Context.isSystemDarkMode get() = DarkModeUtils.isSystemDarkMode(this)

val Context.isAppDarkMode get() = DarkModeUtils.isAppDarkMode(this)

object DarkModeUtils {

    /**
     * 判断系统是否开启了暗黑模式
     */
    fun isSystemDarkMode(context: Context): Boolean {
        val uiModeManager = ContextCompat.getSystemService(context, UiModeManager::class.java)
            ?: return false
        return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
    }

    /**
     * 判断应用当前是否处于暗黑模式
     */
    fun isAppDarkMode(context: Context): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

}
