package com.example.wan.android.base.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.SPUtils
import com.example.wan.android.R
import com.example.wan.android.base.dialog.LoadingDialog
import com.example.wan.android.constant.EventBus
import com.example.wan.android.databinding.CustomActionbarTitleBinding
import com.example.wan.android.utils.ext.hideSoftInput
import com.example.wan.android.utils.observeEvent
import com.example.wan.android.utils.toast
import com.example.wan.android.utils.userLocale
import com.gyf.immersionbar.ktx.immersionBar
import splitties.views.onClick
import java.util.Locale

abstract class BaseActivity(@LayoutRes layoutId: Int = 0) : AppCompatActivity(layoutId) {

    protected val loadingDialog by lazy { LoadingDialog(this) }

    // 自定义的 ActionBarTitleView
    protected val titleView by lazy {
        CustomActionbarTitleBinding.inflate(layoutInflater).actionBarTitle.apply {
            onClick {
                toast(this.text.toString())
            }
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
            customView = titleView
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
        val config = newBase.resources.configuration.apply { setLocale(newBase.userLocale) }
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

}