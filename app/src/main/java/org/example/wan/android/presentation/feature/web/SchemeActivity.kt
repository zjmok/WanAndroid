package org.example.wan.android.presentation.feature.web

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ClipboardUtils
import org.example.wan.android.presentation.feature.base.activity.VBaseActivity
import org.example.wan.android.databinding.ActivitySchemeBinding
import org.example.wan.android.presentation.feature.MainActivity
import org.example.wan.android.util.toast
import org.example.wan.android.util.toastLong
import splitties.activities.start
import splitties.views.onClick

class SchemeActivity : VBaseActivity<ActivitySchemeBinding>() {

    override val binding by lazy { ActivitySchemeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        val uri = intent.data

        if (Intent.ACTION_VIEW == action && uri != null) {
            // 处理自定义 scheme 的数据
            val url = uri.toString()
            val scheme = uri.scheme
            val host = uri.host
            val path = uri.path
            val query = uri.query

            // 根据 host、path 和 query 处理不同的逻辑
            // 例如：跳转到不同的页面或执行不同的操作

            when (scheme) {
                "http", "https" -> {
                    start<MainActivity>{
                        putExtra("from_scheme", true)
                        putExtra("url", url)
                    }
                    finish()
                }

                "wanandroid" -> {
                    val text = "测试自定义 Scheme 成功，\n点击复制你的链接\n${url}"
                    binding.tv.let {
                        it.text = text
                        it.onClick {
                            ClipboardUtils.copyText(url)
                            toastLong("复制成功:\n${url}")
                        }
                    }
                }

                "market" -> {
                    val text = "其实我是个假的应用商店，\n点击复制你的链接\n${url}"
                    binding.tv.let {
                        it.text = text
                        it.onClick {
                            ClipboardUtils.copyText(url)
                            toastLong("复制成功:\n${url}")
                        }
                    }
                }

                else -> {
                    toast("未适配")
                }
            }
        }
    }
}