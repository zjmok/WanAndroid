package com.zjmok.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.view.View.GONE
import android.view.View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.graphics.Insets
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import java.lang.reflect.Field

/**
 * 可以准确获取到各个 Inset 的数值
 */
fun View.windowInsetsListener(
    listener: (
        systemBars: Insets,
        statusBars: Insets,
        navigationBars: Insets,
        displayCutout: Insets,
        systemGestures: Insets,
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

val View.activity: AppCompatActivity?
    get() = getCompatActivity(this.context)

fun View.hideSoftInput() = run {
    val imm = UtilLib.App.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.let {
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

fun View.disableAutoFill() = run {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.importantForAutofill = IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }
}

fun View.gone() {
    if (visibility != GONE) {
        visibility = GONE
    }
}

fun View.invisible() {
    if (visibility != INVISIBLE) {
        visibility = INVISIBLE
    }
}

fun View.visible() {
    if (visibility != VISIBLE) {
        visibility = VISIBLE
    }
}

fun View.visible(visible: Boolean) {
    if (visible && visibility != VISIBLE) {
        visibility = VISIBLE
    } else if (!visible && visibility == VISIBLE) {
        visibility = GONE
    }
}

fun View.screenshot(): Bitmap? {
    return runCatching {
        val screenshot = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(screenshot)
        c.translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(c)
        screenshot
    }.getOrNull()
}

fun SeekBar.progressAdd(int: Int) {
    progress += int
}

fun RadioGroup.getIndexById(id: Int): Int {
    for (i in 0 until this.childCount) {
        if (id == get(i).id) {
            return i
        }
    }
    return 0
}

fun RadioGroup.getCheckedIndex(): Int {
    for (i in 0 until this.childCount) {
        if (checkedRadioButtonId == get(i).id) {
            return i
        }
    }
    return 0
}

fun RadioGroup.checkByIndex(index: Int) {
    check(get(index).id)
}

@SuppressLint("RestrictedApi")
fun PopupMenu.show(x: Int, y: Int) {
    try {
        val field: Field = this.javaClass.getDeclaredField("mPopup")
        field.isAccessible = true
        (field.get(this) as MenuPopupHelper).show(x, y)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}