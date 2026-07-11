package com.zjmok.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity

tailrec fun getCompatActivity(context: Context?): AppCompatActivity? {
    return when (context) {
        is AppCompatActivity -> context
        is androidx.appcompat.view.ContextThemeWrapper -> getCompatActivity(context.baseContext)
        is android.view.ContextThemeWrapper -> getCompatActivity(context.baseContext)
        else -> null
    }
}

inline val Context.touchSlop: Int get() = ViewConfiguration.get(this).scaledTouchSlop

inline val Context.screenWidth get() = this.resources.displayMetrics.widthPixels

inline val Context.screenHeight get() = this.resources.displayMetrics.heightPixels

// 始终有值，需要判断是否显示
inline val Context.statusBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() {
        var statusBarHeight = 0
        val resId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            statusBarHeight = this.resources.getDimensionPixelSize(resId)
        }
        return statusBarHeight
    }

// 始终有值，需要判断是否显示
inline val Context.navBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() {
        var navBarHeight = 0
        val resId = this.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resId > 0) {
            navBarHeight = this.resources.getDimensionPixelSize(resId)
        }
        return navBarHeight
    }
