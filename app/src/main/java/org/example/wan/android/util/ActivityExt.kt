package org.example.wan.android.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager

inline val Context.touchSlop: Int get() = ViewConfiguration.get(this).scaledTouchSlop

inline val Context.screenWidth get() = this.resources.displayMetrics.widthPixels

inline val Context.screenHeight get() = this.resources.displayMetrics.heightPixels

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

inline val Activity.isFullscreen: Boolean
    get() = this.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN

fun Activity.isFullscreen2(): Boolean {
    if (this.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN == View.SYSTEM_UI_FLAG_FULLSCREEN) {
        return true
    }
    return false
}

fun Context.isFullscreen3(): Boolean {
    val typedValue = TypedValue()
    this.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowFullscreen))
        .getValue(0, typedValue)
    if (typedValue.type == TypedValue.TYPE_INT_BOOLEAN) {
        if (typedValue.data != 0) {
            return true
        }
    }
    return false
}
