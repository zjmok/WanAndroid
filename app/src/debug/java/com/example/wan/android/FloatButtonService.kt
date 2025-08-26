package com.example.wan.android

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.ArrayMap
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.core.net.toUri
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.example.wan.android.constant.AppConst
import com.example.wan.android.databinding.DebugWindowBinding
import com.example.wan.android.presentation.compose.ComposeActivity
import com.example.wan.android.presentation.feature.MainActivity
import com.example.wan.android.presentation.feature.dialog.AppDetailDialog
import com.example.wan.android.presentation.feature.setting.SettingActivity
import com.example.wan.android.util.toJson
import com.example.wan.android.util.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@RequiresApi(api = Build.VERSION_CODES.M)
class FloatButtonService : Service() {

    companion object {
        private const val POLL_INTERVAL = 3000L // 3秒轮询间隔
        private const val MAX_RETRY_COUNT = 20 // 最大重试次数
    }

    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val floatButton: View by lazy {
        // 创建悬浮按钮
        ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_preferences)
            setBackgroundResource(android.R.color.holo_blue_bright)
        }
    }
    private var isDebugWindowShown = false
    private val debugWindowBinding: DebugWindowBinding by lazy { DebugWindowBinding.inflate(LayoutInflater.from(this)) }
    private val debugWindow: View by lazy { debugWindowBinding.root }

    private val prefs: SharedPreferences by lazy { getSharedPreferences("float_window_prefs", MODE_PRIVATE) }

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private fun saveUrl(scheme: String, host: String, port: String = "") {
        prefs.edit { putString("scheme", scheme) }
        prefs.edit { putString("host", host) }
        prefs.edit { putString("port", port) }
    }

    @SuppressLint("SetTextI18n")
    private fun setDebugWindow() {
        val defaultUri = AppConst.BASE_URL.toUri()
        val scheme = prefs.getString("scheme", defaultUri.scheme)
        val host = prefs.getString("host", defaultUri.host)
        val port = prefs.getString("port", "${defaultUri.port.takeIf { it > 0 } ?: ""}")

        debugWindowBinding.etScheme.setText(scheme)
        debugWindowBinding.etHost.setText(host)
        debugWindowBinding.etPort.setText(port)

        debugWindowBinding.btnSave.setOnClickListener {
            val resultScheme = debugWindowBinding.etScheme.text.toString().trim()
            val resultIp = debugWindowBinding.etHost.text.toString().trim()
            val resultPort = debugWindowBinding.etPort.text.toString().trim()

            saveUrl(resultScheme, resultIp, resultPort)

            hideKeyboard()
            toast("已保存")
        }

        // Clear
        debugWindowBinding.btnClear.setOnClickListener {
            saveUrl("", "")
            hideKeyboard()
            setDebugWindow()
            toast("已清除修改")
        }

        // profile1
        debugWindowBinding.btnProfile1.setOnClickListener {
            saveUrl("https", "www.wanandroid.com", "")
            hideKeyboard()
            setDebugWindow()
            toast("已切换到 Profile1")
        }

        // profile2
        debugWindowBinding.btnProfile2.setOnClickListener {
            saveUrl("http", "192.168.1.1", "8080")
            hideKeyboard()
            setDebugWindow()
            toast("已切换到 Profile2")
        }

        // profile3
        debugWindowBinding.btnProfile3.setOnClickListener {
            saveUrl("https", "192.168.1.254", "8443")
            hideKeyboard()
            setDebugWindow()
            toast("已切换到 Profile3")
        }

        // feature1
        debugWindowBinding.btnFeature1.text = "应用详情"
        debugWindowBinding.btnFeature1.setOnClickListener {
            hideDebugWindow()
            val activity = topActivity
            if (activity == null || activity.isFinishing) {
                // 如果当前应用在后台，直接跳转到 MainActivity
                // 并在 1 秒后显示 AppDetailDialog

                ActivityUtils.startActivity(MainActivity::class.java)
                serviceScope.launch {
                    delay(1000)
                    val newActivity = topActivity
                    if (newActivity != null) {
                        AppDetailDialog(newActivity).show()
                    }
                }
            } else {
                AppDetailDialog(activity).show()
            }
        }

        // feature2
        debugWindowBinding.btnFeature2.text = "Compose"
        debugWindowBinding.btnFeature2.setOnClickListener {
            hideDebugWindow()
            ActivityUtils.startActivity(ComposeActivity::class.java)
        }

        // feature3
        debugWindowBinding.btnFeature3.setOnClickListener {

        }

        // feature4
        debugWindowBinding.btnFeature4.text = "Setting"
        debugWindowBinding.btnFeature4.setOnClickListener {
            hideDebugWindow()
            ActivityUtils.startActivity(SettingActivity::class.java)
        }

        // feature5
        debugWindowBinding.btnFeature5.text = "AtyList"
        debugWindowBinding.btnFeature5.setOnClickListener {
            val activityList = ActivityUtils.getActivityList()
            val list = activityList.map {
                it.javaClass.simpleName
            }.toList()
            toast("activityList size: ${activityList.size}\n${list.toJson()}")
        }

        // feature6
        debugWindowBinding.btnFeature6.setOnClickListener {
            // 在后台时，
            val a = topActivity // null
            val b = ActivityUtils.getTopActivity() // 有值
            toast(
                """
                    TopActivity: 
                    $a
                    ActivityUtils.getTopActivity: 
                    $b
                    isActivityAlive = ${ActivityUtils.isActivityAlive(b)}
                    """.trimIndent()
            )
            AppDetailDialog(b).show()
        }

        // feature7
        debugWindowBinding.btnFeature7.setOnClickListener {
            val a = topActivity
            val b = ActivityUtils.getTopActivity()
            toast(
                """
                    TopActivity: 
                    $a
                    ActivityUtils.getTopActivity: 
                    $b
                    isActivityAlive = ${ActivityUtils.isActivityAlive(b)}
                    """.trimIndent()
            )
            AppDetailDialog(b).show()
        }

        // feature8
        debugWindowBinding.btnFeature8.setOnClickListener { }
    }

    private fun hideKeyboard() {
        val focusedView = debugWindow.findFocus() ?: return
        // 清除焦点
        focusedView.clearFocus()
        // 关闭软键盘
        if (focusedView is EditText) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        checkOverlayPermission() // 启动时检查权限
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            // 用户选择不再提示
            if (prefs.getBoolean("never_ask_again", false)) {
                return
            }

            // 延迟5秒后显示提示对话框（等待应用进入主页）
            serviceScope.launch {
                delay(5000L)
                showPermissionDialog()
            }
        } else {
            initFloatButton() // 已有权限，直接初始化
        }
    }

    private fun showPermissionDialog() {
        val topActivity = topActivity
        if (topActivity == null || topActivity.isFinishing) {
            return
        }

        // 弹出请求前往授权
        AlertDialog.Builder(topActivity) // 关键点：使用 Activity 而非 Service 的 Context
            .setTitle("调试工具需要悬浮窗权限")
            .setMessage("请允许显示在其他应用上方")
            .setCancelable(false)
            .setPositiveButton("前往授权") { _, _ ->
                requestOverlayPermission()
                startPermissionPolling()
            }
            .setNegativeButton("取消") { _, _ ->
                stopSelf()
            }
            .setNeutralButton("不再提示") { _, _ ->
                prefs.edit { putBoolean("never_ask_again", true) }
                stopSelf()
            }
            .show()
    }

    private fun startPermissionPolling() {
        serviceScope.launch {
            var currentRetryCount = 0
            while (currentRetryCount < MAX_RETRY_COUNT) {
                if (Settings.canDrawOverlays(this@FloatButtonService)) {
                    // 已授权
                    initFloatButton()
                    break
                } else {
                    // 未授权，等待后继续检查
                    currentRetryCount++
                    delay(POLL_INTERVAL)
                }
            }
            // 超出最大重试次数，结束 Service
            stopSelf()
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:$packageName".toUri()
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFloatButton() {

        // 设置悬浮按钮参数
        @Suppress("DEPRECATION")
        val params = WindowManager.LayoutParams(
            dpToPx(60),  // 宽度
            dpToPx(60),  // 高度
            // TYPE_APPLICATION_OVERLAY 显示在其它应用上方 需要权限。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.END
        params.x = prefs.getInt("pos_x", dpToPx(20))
        params.y = prefs.getInt("pos_y", dpToPx(100))

        windowManager.addView(floatButton, params)

        // 设置拖动和点击事件
        (floatButton as? ImageView)?.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (initialTouchX - event.rawX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatButton, params)
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        // 如果移动距离很小，则认为是点击事件
                        if (abs((event.rawX - initialTouchX).toDouble()) < 5 &&
                            abs((event.rawY - initialTouchY).toDouble()) < 5
                        ) {
                            toggleDebugWindow()
                        }
                        savePosition(params.x, params.y)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun savePosition(x: Int, y: Int) {
        prefs.edit {
            putInt("pos_x", x)
            putInt("pos_y", y)
        }
    }

    private fun toggleDebugWindow() {
        if (isDebugWindowShown) {
            hideDebugWindow()
        } else {
            showDebugWindow()
        }
    }

    private fun showDebugWindow() {

        @Suppress("DEPRECATION")
        val debugParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // 允许窗口内接收触摸事件
            PixelFormat.TRANSLUCENT
        )

        debugParams.gravity = Gravity.TOP
        debugParams.y = dpToPx(100)

        // 设置调试窗口内容
        debugWindowBinding.tvAppInfo.text = this.appInfo

        debugWindowBinding.btnRestart.setOnClickListener {
            hideDebugWindow()
            AppUtils.relaunchApp()
        }

        debugWindowBinding.btnCloseTool.setOnClickListener { stopSelf() }

        debugWindowBinding.btnCloseDialog.setOnClickListener { hideDebugWindow() }

        setDebugWindow()

        windowManager.addView(debugWindow, debugParams)
        isDebugWindowShown = true
    }

    private fun hideDebugWindow() {
        windowManager.removeView(debugWindow)
        isDebugWindowShown = false
    }

    private val appInfo: String
        get() {
            try {
                val packageInfo =
                    packageManager.getPackageInfo(packageName, 0)
                @Suppress("DEPRECATION")
                return """
                    App: ${packageInfo.packageName}
                    Version: ${packageInfo.versionName} (${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode})
                    Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
                    Device: ${Build.MANUFACTURER} ${Build.MODEL}
                """.trimIndent()
            } catch (e: PackageManager.NameNotFoundException) {
                return "App info not available"
            }
        }

    override fun onDestroy() {
        // 取消所有协程
        serviceJob.cancel()

        if (floatButton.isAttachedToWindow) {
            windowManager.removeView(floatButton)
        }
        if (debugWindow.isAttachedToWindow) {
            windowManager.removeView(debugWindow)
        }

        super.onDestroy()
    }

    private val topActivity: Activity?
        /**
         * 获取栈顶 Activity 的工具方法
         * AndroidUtilCode 的 ActivityUtils.getTopActivity 应用无论是在前台还是后台时都会返回 topActivity
         * getTopActivity 应用在后台返回 null，应用在前台返回 topActivity
         */
        @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
        get() {
            try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
                val activitiesField = activityThreadClass.getDeclaredField("mActivities")
                activitiesField.isAccessible = true

                val activities =
                    activitiesField[activityThread] as ArrayMap<*, *>
                for (activityRecord in activities.values) {
                    val activityRecordClass: Class<*> = activityRecord.javaClass
                    val pausedField = activityRecordClass.getDeclaredField("paused")
                    pausedField.isAccessible = true
                    if (!pausedField.getBoolean(activityRecord)) {
                        val activityField = activityRecordClass.getDeclaredField("activity")
                        activityField.isAccessible = true
                        return activityField[activityRecord] as Activity
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    /**
     * dp转px工具方法
     *
     * @noinspection SameParameterValue
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

}
