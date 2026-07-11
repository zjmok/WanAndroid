package com.zjmok.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.graphics.Insets

object BarUtils {

    /**
     * 获取实时系统栏（状态栏、导航栏）高度
     */
    fun setBarListener(view: View, listener: (height: Insets) -> Unit) {
        return view.windowInsetsListener { systemBars, statusBars, navigationBars, displayCutout, systemGestures ->
            listener.invoke(systemBars)
        }
    }

    fun setNavBarColor(window: Window, @ColorInt color: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.navigationBarColor = color
    }

    // https://blog.csdn.net/qq_32664007/article/details/126279919

    /**
     * 沉浸式状态栏
     * 配合 fitsSystemWindows 使用
     */
    fun transparentStatusBar(window: Window) {
        //去掉半透明的可能性
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //可以设置系统栏的背景色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //设置状态栏颜色为透明
        window.statusBarColor = Color.TRANSPARENT
        //view的位置上移到系统栏
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //显示内容要加一个状态栏高度的padding的话 在布局后或者对象加 `android:fitsSystemWindows="true"`
    }

    /**
     * 沉浸式导航栏
     * 这里不用 WindowInsetsControllerCompat 实现，
     * 因为 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 没办法等效替换
     * fixme :有 BUG， View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 不仅仅将内容下移到导航栏，还会上移到状态栏
     * 配合 fitsSystemWindows 使用
     */
    fun transparentNavigationBar(window: Window) {
        // 去掉半透明的可能性
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // 可以设置系统栏的背景色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // 状态栏透明
        window.navigationBarColor = Color.TRANSPARENT

        window.decorView.systemUiVisibility =
                // 将内容下移到导航栏
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    // 全屏模式下避免内容跳动，通过预占系统 UI 的最大空间实现平滑过渡
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    /**
     * 沉浸式状态栏 + 沉浸式导航栏
     * 配合 fitsSystemWindows 使用
     */
    fun transparentSystemBar(window: Window) {
        // 去掉半透明的可能性
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // 可以设置系统栏的背景色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // 状态栏透明
        window.statusBarColor = Color.TRANSPARENT
        // 导航栏透明
        window.navigationBarColor = Color.TRANSPARENT

        // 在不隐藏系统栏的情况下，将内容移动到系统栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 该方法会同时作用于状态栏以及导航栏
            // 等同布局的 android:fitsSystemWindows="false"
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility =
                    // 将内容上移到状态栏
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        // 将内容下移到导航栏
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        // 全屏模式下避免内容跳动，通过预占系统 UI 的最大空间实现平滑过渡
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

}
