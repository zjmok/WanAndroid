package org.example.wan.android.constant

import com.blankj.utilcode.util.PathUtils
import java.io.File
import java.util.Locale

object AppConst {

    val crashPath by lazy { PathUtils.getFilesPathExternalFirst() + File.separator + "crash" }
    const val BASE_URL = "https://www.wanandroid.com/"
    val okhttpCachePath by lazy { PathUtils.getCachePathExternalFirst() + File.separator + "okhttp" }
    const val OKHTTP_CACHE_SIZE: Long = 1024 * 1024 * 64 // 64 MB
    val glidePath by lazy { PathUtils.getCachePathExternalFirst() + File.separator + "glide" }
    val coilPath by lazy { PathUtils.getCachePathExternalFirst() + File.separator + "coil" }
    const val IMAGE_CACHE_SIZE: Long = 1024 * 1024 * 256 // 256 MB

    val SUPPORTED_LOCALE_LIST = listOf(
        Locale(""), // default
        // 简体中文
        Locale("zh"),
//        Locale("zh", "CN"),
        // 繁體中文 传统中文
        Locale("zh", "HK"),
        Locale("zh", "MO"),
        Locale("zh", "TW"),
        //
        Locale("en"), // English
        //
        Locale("yue"), // 粵語
        Locale("yue", "CN"), // 粤语
        //
        Locale("ug"), // 维吾尔语
        Locale("bo"), // 藏语
        Locale("hmn"), // 苗语
        Locale("ii"), // 彝语
        Locale("za"), // 壮语
        Locale("ko"), // 韩语
        //
        Locale("ja"), // 日语
        Locale("ru"), // 俄语
        Locale("fr"), // 法语
        Locale("de"), // 德语
        Locale("it"), // 意大利语
        Locale("ar"), // 阿拉伯语
        Locale("es"), // 西班牙语
        Locale("pt", "BR"), // 葡萄牙语 巴西
        Locale("fa"), // 波斯语
        Locale("ur"), // 乌尔都语
    )

}