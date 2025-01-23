package com.example.wan.android.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SPUtils
import com.example.wan.android.BuildConfig
import java.text.SimpleDateFormat
import java.util.Locale

object MyAppUtils {

    fun isAcceptAgreement(): Boolean {
        return SPUtils.getInstance().getBoolean("ACCEPT_AGREEMENT", false)
    }

    fun acceptAgreement(accept: Boolean) {
        SPUtils.getInstance().put("ACCEPT_AGREEMENT", accept)
    }

    /**
     * 比较 x.xx.x 这类,
     * 字符串可带非数字, 即 v1.4 是合法的
     * 不限制子版本的数字位数, 即 1.10 和 1.4 是可比较的
     * @return 示例: 1.4 > 1.3, 1.3.9 < 1.4, 1.10 > 1.4, 1.4 == 1.4.0
     */
    fun String.versionNewerThen(another: String): Boolean {

        val list = this.split(Regex("\\D+")).mapNotNull { it.toIntOrNull() }
        val list2 = another.split(Regex("\\D+")).mapNotNull { it.toIntOrNull() }

        // 比较相同长度
        for (i in 0 until minOf(list.size, list2.size)) {
            return if (list[i] > list2[i]) {
                // 大
                true
            } else if (list[i] < list2[i]) {
                // 小
                false
            } else {
                // 相等
                continue
            }
        }

        // 相同长度相等 比较超出长度
        if (list.size > list2.size) {
            for (i in list.subList(list2.size, list.size)) {
                if (i > 0) {
                    // 大
                    return true
                }
            }
        } else {
            for (i in list2.subList(list.size, list2.size)) {
                if (i > 0) {
                    // 小
                    return false
                }
            }
        }

        // 相等
        return false
    }

    fun getHeapSize(context: Context): Int {
        val activityManager =
            ContextCompat.getSystemService(context, ActivityManager::class.java) ?: return -1
        return activityManager.largeMemoryClass
    }

    fun getAppInfo(env: String = "", uid: String = ""): String {
        // 请打开设置 --> Editor --> Code Style --> Formatter --> 勾上 `Turn formatter on/off ...`
        // @formatter:off
        return """
            - App -
            [id    ] ${BuildConfig.APPLICATION_ID}
            [ver   ] ${BuildConfig.VERSION_NAME}_${BuildConfig.VERSION_CODE}
            [flavor] ${BuildConfig.FLAVOR}${if (BuildConfig.DEBUG) "Debug" else "Release"}
            [env   ] $env
            [uid   ] $uid
            - App.Build -
            [commit] ${BuildConfig.COMMIT_ID}
            [gradle] ${BuildConfig.GRADLE_VERSION}
            [gradle_jdk] ${BuildConfig.GRADLE_JDK}
            [java_jvm] ${BuildConfig.JAVA_JVM}
            [kotlin_jvm] ${BuildConfig.KOTLIN_JVM}
            [kotlin_ver] ${BuildConfig.KOTLIN_VERSION}
            [compose_ver] ${BuildConfig.COMPOSE_VERSION}
            [arch  ] ${BuildConfig.OS_ARCH}
            [host  ] ${BuildConfig.OS_NAME}
            [by    ] ${BuildConfig.USER_NAME}
            [time  ] ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(BuildConfig.BUILD_TIME.toLong())}
            - System -
            [abi   ] ${Build.SUPPORTED_ABIS.joinToString(", ")}
            [brand ] ${Build.BRAND}
            [model ] ${Build.MODEL}
            [ver   ] Android ${Build.VERSION.RELEASE}
            [api   ] ${Build.VERSION.SDK_INT}
            [lang  ] ${Locale.getDefault().language}
            - System.Build -
            [host  ] ${Build.HOST}
            [by    ] ${Build.USER}
            [time  ] ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Build.TIME)}
        """.trimIndent()
        // @formatter:on
    }

}