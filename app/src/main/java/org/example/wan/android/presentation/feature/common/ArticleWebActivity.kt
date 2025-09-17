package org.example.wan.android.presentation.feature.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ClipboardUtils
import org.example.wan.android.App
import org.example.wan.android.R
import org.example.wan.android.data.model.WebData
import org.example.wan.android.data.model.WebPage
import org.example.wan.android.databinding.ActivityWebBinding
import org.example.wan.android.presentation.feature.base.activity.VVMBaseActivity
import org.example.wan.android.util.fromHtmlLegacy
import org.example.wan.android.util.getViewModel
import org.example.wan.android.util.logd
import org.example.wan.android.util.startBrowser
import org.example.wan.android.util.toast
import org.example.wan.android.util.toastLong
import org.example.wan.android.util.visible
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.launch

class ArticleWebActivity : VVMBaseActivity<ArticleWebViewModel, ActivityWebBinding>() {

    private lateinit var webData: WebData

    override val binding by lazy { ActivityWebBinding.inflate(layoutInflater) }

    override val viewModel: ArticleWebViewModel get() = getViewModel()

    lateinit var repository: org.example.wan.android.presentation.feature.web.WebPageRepository

    override fun onDestroy() {
        // 解决 WebView 内存泄漏 2/2
        webView.stopLoading()
        webView.destroy()
        binding.layoutWebViewContainer.removeAllViews()
        super.onDestroy()
    }

    private val webView by lazy {
        // 解决 WebView 内存泄漏 1/2
        WebView(App.INSTANCE).also {
            binding.layoutWebViewContainer.addView(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("data", WebData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("data")
        }
        if (data == null) {
            toast("数据有误")
            finish()
            return
        }
        this.webData = data

//        supportActionBar?.title = fromHtmlLegacy(webData.title ?: "文章")
        titleView.text = fromHtmlLegacy(webData.title ?: "文章")

        repository =
            org.example.wan.android.presentation.feature.web.WebPageRepository(dataStore = (application as App).dataStore)

        webView.run {
            settings.run {
                javaScriptEnabled = true
//                javaScriptCanOpenWindowsAutomatically = true
                useWideViewPort = true  // 将图片调整到适合webview的大小

                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                loadWithOverviewMode = true // 缩放至屏幕的大小
                setSupportZoom(true) // 支持缩放，默认为true。是setBuiltInZoomControls(true)的前提。
                builtInZoomControls = true // 设置内置的缩放控件。若为false=不可缩放
                displayZoomControls = false // 隐藏原生的缩放控件

                /*
                - **`LOAD_DEFAULT`**：默认的缓存使用模式。
                - **`LOAD_CACHE_ELSE_NETWORK`**：如果缓存可用，则使用缓存；否则从网络加载。
                - **`LOAD_NO_CACHE`**：不使用缓存，每次都从网络加载。
                - **`LOAD_CACHE_ONLY`**：只使用缓存，不从网络加载。
                */
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

//                allowFileAccess = true // 可以访问文件
                domStorageEnabled = true // 开启 DOM 存储 例如 微信文章 需要
                databaseEnabled = true // 开启 database 数据库存储
//                loadsImagesAutomatically = true // 自动加载图片
                defaultTextEncodingName = "UTF-8" // 设置编码格式

            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    binding.progress.run {
                        progress = newProgress
                        visible(newProgress < 100)
                    }
                }

                override fun onReceivedTitle(view: WebView, title: String?) {
                    titleView.text = fromHtmlLegacy(/*webData.title ?: */title ?: "文章")

                    logd("onReceivedTitle: url = ${view.url}, title = $title")
                    lifecycleScope.launch {
                        repository.updateWebPage(
                            url = view.url!!,
                            title = title ?: "No Title",
                            author = webData.author,
                            isBookmark = false,
                        )
                    }

                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    // Return `true` to cancel the current load
                    when (request.url?.scheme) {
                        "http", "https" -> {
                            // 页面跳转 使用 webView 打开 防止自动跳转到浏览器
                            view.loadUrl(request.url.toString())
                            return true
                        }

                        else -> {
                            startBrowser(request.url.toString())
                            return true
                        }
                    }
                }
            }
            loadUrl(webData.url)
        }

        onBackPressedDispatcher.addCallback {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                setResult(RESULT_OK, Intent().apply {
                    putExtra("result", webData)
                })
                finish()
                isEnabled = false // 禁用当前的回调
                onBackPressedDispatcher.onBackPressed()
            }
        }

    }

    override fun initStatusBarColor() = R.color.status_bar

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("menu", true)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.getBoolean("menu", false)) {
            invalidateMenu() // activity 重新创建 重绘菜单
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article_web_activity, menu)

        if (webData.like) {
            menu.findItem(R.id.menu_item_like).setIcon(R.drawable.icon_like_selected)
        } else {
            menu.findItem(R.id.menu_item_like).setIcon(R.drawable.icon_like)
        }

        viewModel.like.observe(this) {
            if (it) {
                menu.findItem(R.id.menu_item_like).setIcon(R.drawable.icon_like_selected)
            } else {
                menu.findItem(R.id.menu_item_like).setIcon(R.drawable.icon_like)
            }
            webData.like = it
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val url = webView.url ?: webData.url
        val title = webView.title ?: webData.title

        when (item.itemId) {
            R.id.menu_item_like -> {
                // todo:
                // web 页面跳转到新的 url 页面后 原始页面数据无法更新。目前跳转后只能获取到新的 url，需要从接口层面解决
                if (webView.url != webData.url) {
                    XPopup.Builder(this)
                        .asConfirm(
                            "操作受限",
                            "因接口限制，\n跳转后的页面无法获取文章ID\n\n仅能对页面跳转前的原始文章《${webData.title}》进行收藏/取消\n\n但您可尝试将本页面保存为本地书签~"
                        ) {

                        }.show()
                    return true
                }

                if (webData.like) {
                    XPopup.Builder(this)
                        .asConfirm("移除收藏", "《${fromHtmlLegacy(webData.title)}》") {
                            viewModel.unlikeArticle(webData.id, webData.originId, webData.isMyLike)
                        }.show()
                } else {
                    viewModel.likeArticle(webData.id, webData.originId, webData.isMyLike)
                }
            }

            R.id.menu_item_refresh -> {
                webView.reload()
            }

            R.id.menu_item_bookmark -> {
                // 保存书签
                lifecycleScope.launch {
                    repository.searchWebPage(url, true) {
                        if (it.isNotEmpty()) {
                            confirmUpdateBookmark(it, url, title)
                        } else {
                            updateBookmark(url, title)
                        }
                    }
                }
            }

            R.id.menu_item_copy -> {
                ClipboardUtils.copyText(url)
                toastLong("复制成功:\n${url}")
            }

            R.id.menu_item_share -> {
                startActivity(Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    putExtra(Intent.EXTRA_TITLE, title)
                    type = "text/plain"
                })
            }

            R.id.menu_item_browser -> {
                startBrowser(url)
            }

            else -> {

            }
        }
        return true
    }

    private fun confirmUpdateBookmark(
        it: List<WebPage>,
        url: String,
        title: String?
    ) {
        val joinToString = it.map {
            "- ${it.title}"
        }.joinToString("\n")
        XPopup.Builder(activity)
            .asConfirm(
                "提示",
                "此页面已存在书签\n$joinToString",
                "取消",
                "更新",
                {
                    // 更新书签
                    updateBookmark(url, title)
                },
                {},
                false
            ).show()
    }

    private fun updateBookmark(url: String, title: String?) {
        XPopup.Builder(activity)
            .asInputConfirm(
                "保存书签",
                url,
                title!!
            ) {
                val bookmarkName =
                    if (it.isNullOrEmpty() || it.isBlank()) {
                        // 无输入内容
                        title
                    } else {
                        it
                    }.trim()
                lifecycleScope.launch {
                    repository.updateWebPage(
                        url = url,
                        title = bookmarkName,
                        author = webData.author,
                        isBookmark = true,
                    )
                    toast("更新成功")
                }
            }.show()
    }

}