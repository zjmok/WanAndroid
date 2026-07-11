package com.zjmok.util

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.WindowManager

/**
 * 状态栏高度
 * 始终有值，需要判断是否显示
 * 实时判断使用 BarUtils.setBarListener 或直接使用 ViewCompat.setOnApplyWindowInsetsListener
 */
inline val Activity.statusBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() {
        return if (this.isFullscreen) {
            0
        } else {
            var statusBarHeight = 0
            val resId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) {
                statusBarHeight = this.resources.getDimensionPixelSize(resId)
            }
            statusBarHeight
        }
    }

fun isStatusBarVisible(activity: Activity): Boolean {
    val decorView = activity.window.decorView
    val flags = decorView.systemUiVisibility
    // 如果未设置全屏标志，则认为状态栏可见
    return (flags and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0
}

/**
 * 导航栏高度
 * 始终有值，需要判断是否显示
 * 实时判断使用 BarUtils.setBarListener 或直接使用 ViewCompat.setOnApplyWindowInsetsListener
 */
inline val Activity.navBarHeight: Int
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    get() {
        return if (this.isFullscreen) {
            0
        } else {
            var navBarHeight = 0
            val resId = this.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resId > 0) {
                navBarHeight = this.resources.getDimensionPixelSize(resId)
            }
            navBarHeight
        }
    }

fun isNavBarVisible(activity: Activity): Boolean {
    TODO()
}

inline val Activity.isFullscreen: Boolean
    get() {
        // 检查是否设置了全屏标志
        val isFlagSet =
            (this.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN
        // 检查当前系统 UI 是否处于全屏状态
        val isUiVisible =
            (this.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN
        // 综合考虑：通常认为只要一方满足即可认为是全屏意图或状态
        return isFlagSet || isUiVisible
    }
