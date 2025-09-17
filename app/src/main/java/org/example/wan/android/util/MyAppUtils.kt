package org.example.wan.android.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SPUtils
import org.example.wan.android.BuildConfig
import org.example.wan.android.R
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

    fun getMyAppInfo(context: Context): String {
        val currentLocale = context.getCurrentLocale()
        val currentLocaleName = currentLocale.getDisplayName(currentLocale)
        val languageName =
            if (currentLocaleName != "" && currentLocale.toLanguageTag() != Locale.getDefault().toLanguageTag()) {
                // 若当前语言不是默认语言，追加以当前语言方式显示，如 "英文 | English"
                " | $currentLocaleName"
            } else {
                ""
            }

        val format = SimpleDateFormat(
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                "yyyy/MM/dd HH:mm:ss"
            } else {
                "yyyy/MM/dd HH:mm:ss XXX"
            }, Locale.getDefault()
        )
        // 请打开设置 --> Editor --> Code Style --> Formatter --> 勾上 `Turn formatter on/off ...`
        // @formatter:off
        return """
            - App -
            [id    ] ${BuildConfig.APPLICATION_ID}
            [ver   ] ${BuildConfig.VERSION_NAME}_${BuildConfig.VERSION_CODE}
            [var   ] ${BuildConfig.FLAVOR}${if (BuildConfig.DEBUG) "Debug" else "Release"}
            [target] API ${context.getAppTargetSdk()}
            [res   ] ${(context.getString(R.string.values))}
            [lang  ] ${currentLocale.toLanguageTag()}
            [locate] ${currentLocale.displayName}${languageName}
            [signer] ${context.getAppSigningCertificateCN()}
            - App.Build -
            [commit] ${BuildConfig.COMMIT_ID}
            [gradle] Gradle ${BuildConfig.GRADLE_VERSION}
            [kotlin] Kotlin ${BuildConfig.KOTLIN_VERSION}
            [tchain] JDK ${BuildConfig.JVM_TOOLCHAIN}
            [target] Java ${BuildConfig.JVM_TARGET}
            [compos] Compose ${BuildConfig.COMPOSE_VERSION}
            [arch  ] ${BuildConfig.OS_ARCH}
            [host  ] ${BuildConfig.OS_NAME}
            [by    ] ${BuildConfig.USER_NAME}
            [time  ] ${format.format(BuildConfig.BUILD_TIME.toLong())}
            - System -
            [abi   ] ${Build.SUPPORTED_ABIS.joinToString(", ")}
            [brand ] ${DeviceUtils.brand}
            [model ] ${DeviceUtils.model}
            [os    ] Android ${DeviceUtils.androidVersion}
            [api   ] API ${DeviceUtils.androidApi}
            [skin_t] ${DeviceUtils.skinType}
            [skin_v] ${DeviceUtils.skinVersion}
            [skin_n] ${DeviceUtils.skinName}
            [lang  ] ${Locale.getDefault().toLanguageTag()}
            [locale] ${Locale.getDefault().displayName}
            - System.Build -
            [host  ] ${Build.HOST}
            [by    ] ${Build.USER}
            [time  ] ${format.format(Build.TIME)}
        """.trimIndent()
        // @formatter:on

        // 语言-文字-地区
        // language-script-country
        // zh-Hans-CN
//            [lang  ] ${Locale.getDefault().language}
//            [script] ${Locale.getDefault().script}
//            [area  ] ${Locale.getDefault().country}
    }

}