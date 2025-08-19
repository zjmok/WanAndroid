package com.example.wan.android.index.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.example.wan.android.App
import com.example.wan.android.base.activity.VBaseActivity
import com.example.wan.android.data.repository.WanRepository
import com.example.wan.android.databinding.ActivitySplashBinding
import com.example.wan.android.index.MainActivity
import com.example.wan.android.utils.MyAppUtils
import com.example.wan.android.utils.ext.alert
import com.example.wan.android.utils.ext.cancel
import com.example.wan.android.utils.ext.ok
import com.example.wan.android.utils.loge
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import splitties.activities.start
import splitties.views.onClick

@SuppressLint("CustomSplashScreen")
class SplashActivity : VBaseActivity<ActivitySplashBinding>() {

    companion object {
        const val COUNTDOWN_TIME = 1
        const val SPLASH_TIME = 1000 * 1L
    }

    override val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    private var mCountDown: Job? = null

    override fun initStatusBarDarkFont() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        App.splashCreateTime = System.currentTimeMillis()
        setSplashScreen(splashScreen)
    }

    private fun preRequest() {
        // todo 网络请求 第一次需要较长时间（一般要 1+ 秒） 为了进入 MainActivity 后能快速加载完成 在这里尝试提前随便请求一个接口
        lifecycleScope.launch {
            try {
                WanRepository.getBanner()
            } catch (e: Exception) {
                loge(e.message, "SplashActivity")
            }
        }
    }

    private fun setSplashScreen(splashScreen: SplashScreen) {
        var isShowSplash = true

        // 每次 UI 绘制前，会判断 SplashScreen 是否继续展示在屏幕上，直到不再满足条件时，展示完毕并执行 setOnExitAnimationListener
        splashScreen.setKeepOnScreenCondition {
            isShowSplash
        }

        lifecycleScope.launch {
            // SplashScreen 展示时长
            delay(SPLASH_TIME) // SplashScreen 仅用于过渡启动, 这里设置 0, 后续展示 AD 或倒计时使用自己的页面
            // SplashScreen 展示完毕
            isShowSplash = false
        }

        // SplashScreen 展示完毕的监听方法
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.view.animate().alpha(0f).setDuration(250L).start()

            initPrivacyDialog {
                initSDKWithPrivacy {
                    preRequest()
                    initView {
                        start<MainActivity> {}
                        finish()
                    }
                }
            }
        }
    }

    private fun initPrivacyDialog(afterAction: () -> Unit) {
        val isAccept = MyAppUtils.isAcceptAgreement()
        if (isAccept.not()) {
            alert("提示", "请阅读《用户协议》和《隐私政策》,\n您是否同意? (假如有的话)", false) {
                cancel("拒绝") {
                    AppUtils.exitApp()
                }
                ok("同意") {
                    MyAppUtils.acceptAgreement(true)
                    afterAction.invoke()
                }
            }.show()
        } else {
            afterAction.invoke()
        }
    }

    private fun initSDKWithPrivacy(afterAction: () -> Unit) {
        val agreed = MyAppUtils.isAcceptAgreement()
        if (agreed.not()) return

        // 在此初始化访问设备信息的SDK

        afterAction.invoke()
    }

    private fun initView(afterAction: () -> Unit) {
        initCountDown {
            afterAction.invoke()
        }
        binding.tvSkip.onClick {
            mCountDown?.cancel()
            afterAction.invoke()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCountDown(block: () -> Unit) {
        mCountDown = countDownByFlow(COUNTDOWN_TIME, lifecycleScope, {
            if (it == 0) mCountDown?.cancel()
//            binding.tvSkip.text = "跳过(${it})"
        }) {
            block.invoke()
        }
    }

    /**
     * 使用Flow实现一个倒计时功能
     */
    @Suppress("SameParameterValue")
    private fun countDownByFlow(
        max: Int,
        scope: CoroutineScope,
        onTick: (Int) -> Unit,
        onFinish: (() -> Unit)? = null,
    ): Job = flow {
        for (i in max downTo 0) {
            emit(i)
            if (i != 0) delay(1000)
        }
    }.flowOn(Dispatchers.Main)
        .onEach { onTick.invoke(it) }
        // Kotlin 2.x 的 Flow 正常取消时 onCompletion 的 cause 返回 JobCancellationException，而 1.9 返回 null
        .onCompletion { cause -> if (cause == null || cause is CancellationException) onFinish?.invoke() }
        .launchIn(scope) //保证在一个协程中执行

}