package com.example.wan.android.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.wan.android.BuildConfig
import com.example.wan.android.R
import com.example.wan.android.utils.dp2pxInt
import com.example.wan.android.utils.ext.setTypeface
import com.example.wan.android.utils.getSigningCertificateCN
import java.text.SimpleDateFormat
import java.util.Locale

class AppDetailDialog(context: Context?, val env: String = "", val uid: String = "") :
    AlertDialog(context) {

    override fun show() {
        setView(getCustomView())
        super.show()
    }

    private fun getCustomView(): View {
        return LinearLayout(context).also { outLL ->
            outLL.orientation = LinearLayout.VERTICAL
            outLL.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            outLL.gravity = Gravity.CENTER
            outLL.setPadding(10.dp2pxInt, 0, 10.dp2pxInt, 0)
            outLL.addView(ScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    weight = 1f
                }
                addView(LinearLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                    addView(TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        gravity = Gravity.START
                        setPadding(16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt)
                        text = getInfo()
                        // https://www.jetbrains.com/lp/mono/
                        // https://github.com/JetBrains/JetBrainsMono
                        setTypeface("fonts/JetBrainsMono-Light.ttf")
                    })
                })
            })
            outLL.addView(LinearLayout(context).also { btnLL ->
                btnLL.orientation = LinearLayout.VERTICAL
                btnLL.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                btnLL.gravity = Gravity.CENTER
                btnLL.addView(View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1,
                    )
                    setBackgroundColor(Color.GRAY)
                })
                btnLL.addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                    gravity = Gravity.CENTER
                    setPadding(16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt, 16.dp2pxInt)
                    text = "确定"
                    setOnClickListener {
                        dismiss()
                    }
                })
            })
        }
    }

    private fun getInfo(): String {
        // 请打开设置 --> Editor --> Code Style --> Formatter --> 勾上 `Turn formatter on/off ...`
        // @formatter:off
        return """
            - App -
            [id    ] ${BuildConfig.APPLICATION_ID}
            [ver   ] ${BuildConfig.VERSION_NAME}_${BuildConfig.VERSION_CODE}
            [flavor] ${BuildConfig.FLAVOR}${if (BuildConfig.DEBUG) "Debug" else "Release"}
            [env   ] $env
            [uid   ] $uid
            [lang  ] ${(context.getString(R.string.values))}
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
            [signer] ${context.getSigningCertificateCN()}
            [time  ] ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(BuildConfig.BUILD_TIME.toLong())}
            - System -
            [abi   ] ${Build.SUPPORTED_ABIS.joinToString(", ")}
            [brand ] ${Build.BRAND}
            [model ] ${Build.MODEL}
            [ver   ] Android ${Build.VERSION.RELEASE}
            [api   ] ${Build.VERSION.SDK_INT}
            [lang_t] ${Locale.getDefault().toLanguageTag()}
            [lang_n] ${Locale.getDefault().displayName}
            - System.Build -
            [host  ] ${Build.HOST}
            [by    ] ${Build.USER}
            [time  ] ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(Build.TIME)}
        """.trimIndent()
        // @formatter:on

        // 语言-文字-地区
        // language-script-country
//            [lang  ] ${Locale.getDefault().language}
//            [script] ${Locale.getDefault().script}
//            [area  ] ${Locale.getDefault().country}
    }

}
