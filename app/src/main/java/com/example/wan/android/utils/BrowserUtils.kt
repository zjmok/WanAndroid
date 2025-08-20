package com.example.wan.android.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import com.example.wan.android.App

// 使用系统的浏览器列表
fun Context.startUrl(url: String) {

    loge("startBrowser url = $url")

    val uri = url.toUri()

    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
    }

    when (uri.scheme) {
        // setPackage 设置一个包名 指定打开某个浏览器 只能设置一个

        "weixin" -> {
            // 适配 weixin 的 scheme，跳转到微信 APP 打开
            // 具体位置有 微信文章里点击评论
            intent.setPackage(AppPkg.WeChat.pkg)
        }

        "wanandroid" -> {
            // 自定义 scheme
            intent.setPackage(AppPkg.WanAndroid.pkg)
        }
    }

    // FIXME
    // MIUI 12 测试 `Intent.createChooser` 选择浏览器无效，只会打开 `intent.setPackage` 指定的浏览器或系统设置的默认浏览器
    if (intent.resolveActivity(App.INSTANCE.packageManager) != null) {
        try {
            startActivity(Intent.createChooser(intent, "选择浏览器"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        toast("没有可用的浏览器应用")
    }

}

// 使用自定义的浏览器列表
fun Context.startBrowser(url: String) {

    loge("startUrl url = $url")

    val uri = url.toUri()

    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
    }

    when (uri.scheme) {
        // setPackage 设置一个包名 指定打开某个浏览器 只能设置一个

        "weixin" -> {
            // 适配 weixin 的 scheme，跳转到微信 APP 打开
            // 具体位置有 微信文章里点击评论
            intent.setPackage(AppPkg.WeChat.pkg)
        }

        "wanandroid" -> {
            // 自定义 scheme
            intent.setPackage(AppPkg.WanAndroid.pkg)
        }
    }

    // 可得到匹配的应用列表
    val resolveInfoList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    // 系统默认浏览器，可解析的应用列表
    val resolvePkgList = resolveInfoList.map { it.activityInfo.packageName }
    loge("resolvePkgList:")
    loge(resolvePkgList.joinToString("\n"))

    // 使用指定的应用
    val pkgList = listOf(
        AppPkg.Browser,
        AppPkg.WeChat,
        AppPkg.WanAndroid,
        AppPkg.Chrome,
        AppPkg.Edge,
        AppPkg.Firefox,
        AppPkg.XBrowser,
        AppPkg.Via,
        AppPkg.Quark,
        AppPkg.Oupeng,
        AppPkg.UCMobile,
        AppPkg.UCMobileLite,
        AppPkg.UCMobileIntl,
        AppPkg.QQBrowser,
        AppPkg.Baidu,
        AppPkg.BaiduLite,
        AppPkg.BaiduBrowser,
        AppPkg.QihooBrowser360,
        AppPkg.QihooContents360,
    ).map { it.pkg }.toMutableList()

    // 最终的应用列表
    val specifiedPkgList = mutableListOf<String>()
        .apply {
            addAll(pkgList)
            // 阿猫阿狗都说自己是浏览器（淘宝、京东、WPS...），不要系统默认解析的
//            addAll(resolvePkgList)
        }
        // TODO: 小米 MIUI 12 有 bug，魔改它自己的默认浏览器到推荐位置后导致后面其它方式的索引差了一位
        // TODO: 受不了 过滤掉
        .filter {
            it != AppPkg.Browser.pkg
        }

    // intent 列表
    val intentList = specifiedPkgList.map {
        Intent(intent).apply {
            `package` = it
        }
    }

    // 创建一个 Intent 选择器，并传递符合要求的应用
    val chooserIntent = Intent.createChooser(intentList[0], "选择浏览器")

    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.drop(1).toTypedArray())
    // 使用 intent 打开 activity
    try {
        startActivity(chooserIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }

}
