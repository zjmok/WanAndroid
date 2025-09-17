package org.example.wan.android.presentation.feature.base.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.SPUtils
import org.example.wan.android.R
import org.example.wan.android.constant.EventBus
import org.example.wan.android.databinding.CustomActionbarBinding
import org.example.wan.android.presentation.feature.dialog.LoadingDialog
import org.example.wan.android.util.hideSoftInput
import org.example.wan.android.util.observeEvent
import org.example.wan.android.util.userLocale
import com.gyf.immersionbar.ktx.immersionBar
import splitties.views.onClick
import java.util.Locale

abstract class BaseActivity(@LayoutRes layoutId: Int = 0) : AppCompatActivity(layoutId) {

    protected val loadingDialog by lazy { LoadingDialog(this) }

    private val actionbarBinding by lazy {
        CustomActionbarBinding.inflate(layoutInflater).apply {
            ivBack.onClick {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    protected val backView by lazy {
        actionbarBinding.ivBack
    }

    protected val titleView by lazy {
        actionbarBinding.tvTitle.apply {
            isSelected = true // 开启跑马灯
        }
    }

    val isInMultiWindow: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                isInMultiWindowMode
            } else {
                false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 状态栏导航栏颜色
        immersionBar {
            statusBarColor(initStatusBarColor())
            navigationBarColor(initNavBarColor())
            // 状态栏字体颜色 (系统自动变色优先, 即对低版本系统不会自动变色有效)
            statusBarDarkFont(initStatusBarDarkFont())
        }

        observeBus()

        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayShowCustomEnabled(true)
            customView = actionbarBinding.root
            setBackgroundDrawable(actionbarBinding.root.background)
        }
    }

    @ColorRes
    protected open fun initStatusBarColor(): Int = R.color.transparent

    @ColorRes
    protected open fun initNavBarColor(): Int = R.color.black

    protected open fun initStatusBarDarkFont(): Boolean {
        // 非深色模式都默认设置黑色字体
        val lightModel = SPUtils.getInstance().getInt("night_model")
        return lightModel != AppCompatDelegate.MODE_NIGHT_YES
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        immersionBar {
            // 低版本系统状态栏字体不会自动变色
            statusBarDarkFont(initStatusBarDarkFont())
        }
    }

    override fun finish() {
        currentFocus?.hideSoftInput()
        super.finish()
    }

    protected open fun observeBus() {
        observeEvent<Locale>(EventBus.REFRESH_LANGUAGE) {
            recreate()
        }
    }

    protected open fun showLoading() {
        loadingDialog.show()
    }

    protected open fun dismissLoading() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    val activity get() = this

    override fun attachBaseContext(newBase: Context) {
        val config = newBase.resources.configuration.apply { setLocale(newBase.userLocale.second) }
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    // 触摸 EditText 区域外是否关闭软键盘 配置方法
    open val isHideKeyboardWhenTouchOutside = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isHideKeyboardWhenTouchOutside) {
            // 触摸 EditText 区域外关闭软键盘
            if (ev?.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (!isInArea(v, ev)) {
                    v?.hideSoftInput()
                    v?.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 触摸点是否在 EditText 区域
    private fun isInArea(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return event.x > left && event.x < right && event.y > top && event.y < bottom
        }
        return false
    }

}
