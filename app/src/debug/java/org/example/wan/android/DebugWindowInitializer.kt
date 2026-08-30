package org.example.wan.android

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ActivityUtils
import com.zjmok.debugtools.DebugActionsContext
import com.zjmok.debugtools.DebugToolsService
import com.zjmok.util.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.wan.android.databinding.DebugActionsBinding
import org.example.wan.android.presentation.compose.ComposeActivity
import org.example.wan.android.presentation.feature.MainActivity
import org.example.wan.android.presentation.feature.dialog.AppDetailDialog
import org.example.wan.android.presentation.feature.setting.SettingActivity
import org.example.wan.android.util.gson.toJson

/**
 * Debug 工具注入器。
 *
 * 由 ContentProvider 自动调用（早于 Application.onCreate）。
 * 负责向模块的 [DebugToolsService] 注入 actions 视图工厂。
 * 模块自身负责启动 Service（通过 [DebugTools.init]）。
 */
@RequiresApi(Build.VERSION_CODES.M)
class DebugWindowInitializer : ContentProvider() {

    override fun onCreate(): Boolean {
        DebugToolsService.actionsViewFactory = { ctx, inflater ->
            buildActionsView(ctx, inflater)
        }
        return true
    }

    private fun buildActionsView(
        ctx: DebugActionsContext,
        inflater: LayoutInflater,
    ) = DebugActionsBinding.inflate(inflater, null, false).apply {
        // feature1 应用详情
        btnFeature1.text = "应用详情"
        btnFeature1.setOnClickListener {
            ctx.hideWindow()
            val activity = ctx.topActivity
            if (activity == null || activity.isFinishing) {
                ActivityUtils.startActivity(MainActivity::class.java)
                ctx.serviceScope.launch {
                    delay(1000)
                    val newActivity = ctx.topActivity
                    if (newActivity != null) {
                        AppDetailDialog(newActivity).show()
                    }
                }
            } else {
                AppDetailDialog(activity).show()
            }
        }

        // feature2 Compose
        btnFeature2.text = "Compose"
        btnFeature2.setOnClickListener {
            ctx.hideWindow()
            ActivityUtils.startActivity(ComposeActivity::class.java)
        }

        // feature3
        btnFeature3.setOnClickListener { }

        // feature4 Setting
        btnFeature4.text = "Setting"
        btnFeature4.setOnClickListener {
            ctx.hideWindow()
            ActivityUtils.startActivity(SettingActivity::class.java)
        }

        // feature5 AtyList
        btnFeature5.text = "AtyList"
        btnFeature5.setOnClickListener {
            val activityList = ActivityUtils.getActivityList()
            val list = activityList.map { it.javaClass.simpleName }.toList()
            toast("activityList size: ${activityList.size}\n${list.toJson()}")
        }

        // feature6
        btnFeature6.setOnClickListener {
            val a = ctx.topActivity
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

        // feature7
        btnFeature7.setOnClickListener {
            val a = ctx.topActivity
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
        btnFeature8.setOnClickListener { }
    }.root

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String?>?): Int = 0
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? = null
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ): Int = 0
}
