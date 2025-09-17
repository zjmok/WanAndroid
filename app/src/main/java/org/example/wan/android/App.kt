package org.example.wan.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import coil.Coil
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.SPUtils
import org.example.wan.android.config.CoilConfig
import org.example.wan.android.constant.AppConst
import org.example.wan.android.util.getViewModel
import com.hjq.toast.Toaster
import com.hjq.toast.style.BlackToastStyle
import com.hjq.toast.style.LocationToastStyle
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.litepal.LitePal
import java.text.SimpleDateFormat

open class App : MultiDexApplication() {

    companion object {

        @JvmStatic
        lateinit var INSTANCE: App
            private set

        var launchTime = 0L
        var appCreateTime = 0L
        var splashCreateTime = 0L
        var mainCreateTime = 0L

    }

    val appViewModel: AppViewModel by lazy { getViewModel() }
    val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val dataStore by preferencesDataStore(name = "preferences_datastore")

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        launchTime = System.currentTimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        appCreateTime = System.currentTimeMillis()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleEventObserver())
        initCrashUtils()
        initNightModel()
        initSmartRefreshLayout()
        initLitePal()
        initCoil() // compose
        initToaster()
    }

    private fun initToaster() {
        // 初始化 Toast 框架
        Toaster.init(this)
        // setStyle 会给 sToastStyle 赋值，
        // 若前面设置 setGravity 会失效，但可以放在后面
        Toaster.setStyle(object : BlackToastStyle() {
            override fun getBackgroundDrawable(context: Context): Drawable {
                return (super.getBackgroundDrawable(context) as GradientDrawable).apply {
                    setCornerRadius(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            999f, // 修改样式的圆角半径
                            context.resources.displayMetrics
                        )
                    )
                }
            }
        }.let {
            // 这里跟直接设置 Toaster.setGravity 一样的效果，必须在后面进行包裹
            LocationToastStyle(it, Gravity.CENTER or Gravity.BOTTOM, 0, 200, 0f, 0f)
        })
    }

    private fun initCoil() {
        Coil.setImageLoader(CoilConfig.getImageLoader(this))
    }

    private fun initLitePal() {
        LitePal.initialize(this)
    }

    @SuppressLint("SimpleDateFormat")
    private fun initSmartRefreshLayout() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            ClassicsHeader(context)
                .setTimeFormat(SimpleDateFormat("上次更新 yyyy/MM/dd HH:mm:ss"))
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(context)
        }
    }

    private fun initNightModel() {
        val lightModel = SPUtils.getInstance().getInt("night_model")
        AppCompatDelegate.setDefaultNightMode(lightModel)
    }

    private fun initCrashUtils() {
        CrashUtils.init(AppConst.crashPath)
    }

}