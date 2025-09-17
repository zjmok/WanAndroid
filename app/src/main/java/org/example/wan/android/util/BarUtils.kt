package org.example.wan.android.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.setOnApplyWindowInsetsListener(
    listener: (
        systemBars: Insets,
        statusBars: Insets,
        navigationBars: Insets,
        displayCutout: Insets,
        systemGestures: Insets
    ) -> Unit
) {

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        // 所有bar
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        // 状态栏
        val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
        // 导航栏
        val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        // 异性屏的特殊区域 Android 9 时引入的 Cutout API
        val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
        // 手势导航区域 Android 10 时引入的，两侧返回和底部 Home 键的区域
        val systemGestures = insets.getInsets(WindowInsetsCompat.Type.systemGestures())

        listener.invoke(systemBars, statusBars, navigationBars, displayCutout, systemGestures)

        insets
    }

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
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    //显示内容要加一个状态栏高度的padding的话 在布局后或者对象加 `android:fitsSystemWindows="true"`
}

/**
 * 沉浸式导航栏，这里就不用 WindowInsetsControllerCompat 实现了，
 * 因为 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 没办法等效替换
 * fixme :有BUG， View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 不仅仅将内容下移到导航栏，还会上移到状态栏
 * 配合 fitsSystemWindows 使用
 */
fun transparentNavigationBar(window: Window) {
    //去掉半透明的可能性
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    //可以设置系统栏的背景色
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.navigationBarColor = Color.TRANSPARENT
    //view的位置下移到导航栏
    window.decorView.systemUiVisibility =
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}

/**
 * 沉浸式状态栏以及导航栏
 * 配合 fitsSystemWindows 使用
 */
fun transparentSystemBar(window: Window) {
    //去掉半透明的可能性
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    //可以设置系统栏的背景色
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
    //view的位置上移到系统栏
    window.decorView.systemUiVisibility =
            //将内容上移到状态栏
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                //将内容下移到状态栏
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
}

/**
 * 沉浸式状态栏以及导航栏
 * 配合 fitsSystemWindows 使用
 */
fun transparentSystemBar2(window: Window) {
    //去掉半透明的可能性
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    //可以设置系统栏的背景色
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    //在不隐藏系统栏的情况下，将内容移动到系统栏
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //该方法会同时作用于状态栏以及导航栏
        window.setDecorFitsSystemWindows(false)
    } else {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}
