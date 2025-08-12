package com.example.wan.android.index.setting

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.transition.TransitionInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ZipUtils
import com.bumptech.glide.Glide
import com.example.wan.android.R
import com.example.wan.android.base.activity.VVMBaseActivity
import com.example.wan.android.constant.AppConst
import com.example.wan.android.databinding.ActivitySettingBinding
import com.example.wan.android.index.web.WebActivity
import com.example.wan.android.network.RetrofitClient
import com.example.wan.android.ui.dialog.AppDetailDialog
import com.example.wan.android.utils.AppPkg
import com.example.wan.android.utils.MyAppUtils
import com.example.wan.android.utils.UserUtils
import com.example.wan.android.utils.ext.alert
import com.example.wan.android.utils.ext.cancel
import com.example.wan.android.utils.ext.neutral
import com.example.wan.android.utils.ext.ok
import com.example.wan.android.utils.ext.visible
import com.example.wan.android.utils.getUri
import com.example.wan.android.utils.getViewModel
import com.example.wan.android.utils.toast
import com.example.wan.android.utils.toastLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import splitties.views.onClick
import java.io.File

class SettingActivity : VVMBaseActivity<SettingViewModel, ActivitySettingBinding>() {

    override val viewModel: SettingViewModel by lazy { getViewModel() }
    override val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initStatusBarColor() = R.color.status_bar

    @SuppressLint("SetTextI18n", "IntentReset")
    private fun initView() {
        val cachePath = PathUtils.getExternalAppCachePath()
        val len = FileUtils.getLength(cachePath)
        val size = ConvertUtils.byte2FitMemorySize(len, 2)
        binding.tvCache.text = size
        binding.llCache.onClick {
            alert("提示", "将会清理图片、网络请求等缓存，是否继续？") {
                cancel {}
                ok {
                    lifecycleScope.launch {
                        showLoading()
                        withContext(Dispatchers.IO) {
                            Glide.get(activity).clearDiskCache() // glide api 清除缓存
                            RetrofitClient.clearCache() // okhttp api 清除缓存
                            FileUtils.deleteAllInDir(cachePath) // 删除 APP 私有目录 的 cache 目录
                        }
                        delay(200) // 假装正在努力清除缓存, 清理得太快就连 loading 都看不到
                        toast("清理完成")
                        val lenNew = FileUtils.getLength(cachePath)
                        val sizeNew = ConvertUtils.byte2FitMemorySize(lenNew, 2)
                        binding.tvCache.text = sizeNew
                        dismissLoading()
                    }
                }
            }.show()
        }
        initViewLightModel()
        binding.llNightModel.onClick {
            val currentMode = binding.tvNightModel.text
            val nightModeArray = arrayOf("跟随系统", "普通模式", "深色模式")
            val index = try {
                nightModeArray.toList().indexOf(currentMode)
            } catch (e: Exception) {
                0
            }
            var selectModel = SPUtils.getInstance().getInt("night_model")
            alert("深色模式") {
                setSingleChoiceItems(nightModeArray, index) { dialog, which ->
                    selectModel = when (which) {
                        1 -> {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }

                        2 -> {
                            AppCompatDelegate.MODE_NIGHT_YES
                        }

                        else -> {
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }
                    }
                }
                cancel {}
                ok {
                    SPUtils.getInstance().put("night_model", selectModel)
                    initViewLightModel()
                    AppCompatDelegate.setDefaultNightMode(selectModel)
                }
            }.show()
        }
        binding.llWebSite.onClick {
            val url = "https://wanandroid.com/"
            WebActivity.start(url)
        }
        binding.llReport.onClick {
            val email = getString(R.string.email)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // 网易邮箱 未能识别。 Gmail、Mail.ru、Outlook、厂商自带邮件APP 都能正常识别
                putExtra(Intent.EXTRA_SUBJECT, "${getString(R.string.app_name)}-反馈/建议")
//                putExtra(Intent.EXTRA_TEXT, "")
            }
            sendEmail(intent)
        }
        binding.llLog.onClick {
            lifecycleScope.launch(Dispatchers.IO) {
                val zipFilePath =
                    PathUtils.getCachePathExternalFirst() + File.separator + "crash.zip"
                try {
                    ZipUtils.zipFile(
                        AppConst.crashPath,
                        zipFilePath
                    )
                } catch (e: Exception) {
                    // Caused by: java.io.FileNotFoundException: /storage/emulated/0/Android/data/com.example.wan.android/files/crash: open failed: ENOENT (No such file or directory)
                    // 没有报错文件
                    toast("应用似乎正在稳定运行")
                    return@launch
                }
                launch(Dispatchers.Main) {
                    val email = getString(R.string.email)
                    // ACTION_SENDTO 无附件
                    // ACTION_SEND 一个附件
                    // ACTION_SEND_MULTIPLE 多个附件
                    /*
                    https://developer.android.com/guide/components/intents-common#Email
                    ACTION_SENDTO (for no attachment) or
                    ACTION_SEND (for one attachment) or
                    ACTION_SEND_MULTIPLE (for multiple attachments)
                    */
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        // 网易邮箱 未能识别 EXTRA_EMAIL。
                        // Gmail、Mail.ru、Outlook、厂商自带邮件APP 都能正常识别。
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                        putExtra(Intent.EXTRA_SUBJECT, "${getString(R.string.app_name)}-崩溃日志上报")
                        putExtra(
                            Intent.EXTRA_TEXT, MyAppUtils.getAppInfo(
                                "null",
                                UserUtils.getSuperUserInfo()?.userInfo?.username ?: "null"
                            )
                        )
                        putExtra(Intent.EXTRA_STREAM, File(zipFilePath).getUri())
                        type = "application/octet-stream"
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        alert("提示", "请选择一款邮件App") {
                            ok {
                                sendEmail(intent)
                            }
                        }.show()
                    } else {
                        toast("您未安装邮件APP")
                    }
                }
            }
        }
        binding.llSource.onClick {
            val url = getString(R.string.repo_url)
            alert("APP 源码", "打开 URL: \n$url") {
                ok {
                    WebActivity.start(url)
                }
                cancel {

                }
                neutral("复制 URL") {
                    ClipboardUtils.copyText(url)
                    toastLong("复制成功:\n${url}")
                }
            }.show()
        }
        binding.llVersion.onClick {
            onMultiClick({ i ->
                toast("快速再按 $i 次 查看更多")
            }) {
                AppDetailDialog(
                    context = activity,
                    env = AppConst.BASE_URL,
                    uid = "${UserUtils.getSuperUserInfo()?.userInfo?.id ?: "null"}",
                ).show()
            }
        }
        binding.tvVersion.text = "Version ${AppUtils.getAppVersionName()}"
        binding.llLogout.onClick {
            alert("提示", "要注销登录吗?") {
                cancel {}
                ok {
                    viewModel.logout()
                }
            }.show()
        }
        binding.llLogout.visible(UserUtils.isLogin)

        // 过度动画 共享元素 test
        binding.llLogout.transitionName = "shared_element"

    }

    // 提供自定义应用列表 若系统可解析的列表中的应用不包含在内 则过滤掉
    private fun sendEmail(intent: Intent) {
        // 可得到匹配的应用列表
        val resolveInfoList = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
//        log(resolveInfoList.toJson())

        // 使用指定的应用
        val specifiedPackages = listOf(
            AppPkg.Email.pkg,
            AppPkg.Gmail.pkg,
            AppPkg.MailRu.pkg,
            AppPkg.Outlook.pkg,
            AppPkg.YahooMail.pkg,
            AppPkg.Spark.pkg,
            AppPkg.SamsungEmail.pkg,
            AppPkg.NetEaseMail.pkg,
            AppPkg.NetEaseMail2.pkg,
            AppPkg.QQMail.pkg,
            AppPkg.AliyunMail.pkg,
            AppPkg.SinaMail.pkg,
            AppPkg.Mail139.pkg,
        )
        // 过滤 包含
        val specifiedResolveInfoList = resolveInfoList.filter { resolveInfo ->
            specifiedPackages.contains(resolveInfo.activityInfo.packageName)
        }

        if (specifiedResolveInfoList.isNotEmpty()) {
            val initialIntents = specifiedResolveInfoList.map { resolveInfo ->
                Intent(intent).apply {
                    // 由于 Intent.createChooser 中的 initialIntents 列表中的 Intent 对象没有设置正确的 ComponentName 或 PackageName，导致系统无法找到对应的应用程序图标
//                    setPackage(resolveInfo.activityInfo.packageName)
                    // 解决上述问题：component 可指定 className 入口
                    component = ComponentName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name
                    )
                }
            }.toTypedArray()
            // 使用过滤后的列表创建全新的应用列表
            // 需要确保 createChooser 使用有效值，故取列表的[0]
            val intentChooser =
                Intent.createChooser(initialIntents[0], "选择邮件APP").apply {
                    putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        initialIntents.drop(1).toTypedArray()
                    )
                }
            startActivity(intentChooser)
        } else {
            toast("没有可用的应用")
        }
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        // 过度动画 共享元素 test
        window.sharedElementEnterTransition =
            TransitionInflater.from(activity).inflateTransition(android.R.transition.move)
    }

    private fun initViewLightModel() {
        binding.tvNightModel.text = when (SPUtils.getInstance().getInt("night_model")) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                "普通模式"
            }

            AppCompatDelegate.MODE_NIGHT_YES -> {
                "深色模式"
            }

            else -> {
                "跟随系统"
            }
        }
    }

    override fun viewModelObserve() {
        super.viewModelObserve()
        viewModel.logStatus.observe(this) {
            if (it.not()) {
                toast("已注销登录")
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private val size = 5 // 定义需要点击的次数
    private val interval = 200
    private var mHints: Array<Long?> = arrayOfNulls(size) // 记录最近几次点击的时间戳

    // 1. 点击时间记录：
    // 用 mHints 数组来存储最近几次点击的时间戳
    // 通过 System.arraycopy 将数组元素向前移动一位，确保最新的点击时间放在最后一位
    // 2. 时间间隔判断：
    // 计算连续点击是否在有效时间间隔内，如果满足条件则调用 listener()
    // 否则，会遍历数组，从最可能不符合的点击开始读取，并调用 continueListener，参数代表达到连点成功还需要点击的次数
    private fun onMultiClick(
        continueListener: ((Int) -> Unit)? = null,
        listener: () -> Unit
    ) {
        val currentTime = SystemClock.uptimeMillis()
        // 将数组元素向前移动一位，并把最新的点击时间放在最后一位
        System.arraycopy(mHints, 1, mHints, 0, mHints.size - 1)
        mHints[mHints.size - 1] = currentTime
        val time = interval * size
        // 检查是否满足连续点击条件
        if (currentTime - (mHints[0] ?: 0L) <= time) { // 连续点击之间有效间隔
            mHints = arrayOfNulls(size) // 重置（也许不是必要的）
            listener() // 调用有效的点击事件
        } else {
            // 检查每次点击的时间
            for (i in 0 until size) {
                if (currentTime - (mHints[i] ?: 0L) <= time) {
                    // 回调 达到连点成功还需要点击的次数
                    continueListener?.invoke(i)
                    break
                }
            }
        }
    }

}