package com.example.wan.android.utils

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import java.util.Locale

var Context.userLocale
    get(): Pair<Boolean, Locale> {
        val context = this
        // 从 SharedPreferences 中获取用户选择的语言
        val prefs = context.getSharedPreferences("Settings", MODE_PRIVATE)
        val langDefault = prefs.getBoolean("app_language_default", true)
        val langCode = prefs.getString("app_language", null)
        val locale = langCode?.let { Locale.forLanguageTag(it) } ?: Locale.getDefault()
        return langDefault to locale
    }
    set(pair) {
        val context = this
        // 保存用户选择的语言到 SharedPreferences
        val prefs = context.getSharedPreferences("Settings", MODE_PRIVATE)
        // 如果设置为系统默认语言，则将 app_language_default 设置为 true，并删除 app_language
        // 否则将 app_language_default 设置为 false，并保存 app_language
        prefs.edit { putBoolean("app_language_default", pair.first) }
        if (pair.first) {
            // 如果设置为系统默认语言，则删除 SharedPreferences 中的设置
            prefs.edit { remove("app_language") }
        } else {
            prefs.edit { putString("app_language", pair.second.toLanguageTag()) }
        }
    }

// 当前 Locale，用户选择的，resources.configuration.setLocale 的
fun Context.getCurrentLocale(): Locale {
    val context = this
    @Suppress("DEPRECATION")
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        context.resources.configuration.locale
    }
    return locale
}

// 默认 Locale，跟随系统
fun getDefaultLocale(): Locale {
    val locale = Locale.getDefault()
    return locale
}

fun Locale.getCustomDisplay(): String {
    var name = this.getCustomDisplayLanguage()
    this.displayScript.takeIf { it.isNullOrBlank().not() }?.let { name += "-$it" }
    this.displayCountry.takeIf { it.isNullOrBlank().not() }?.let { name += "-$it" }
    return name
}

fun Locale.getCustomDisplayName(): String {
    return when (this.language) {
//        "zh" -> when (this.country) {
//            "HK" -> "中文 (香港)"
//            "MO" -> "中文 (澳門)"
//            "TW" -> "中文 (台灣)"
//            "CN" -> "中文 (中国)"
//            "SG" -> "中文 (新加坡)"
//            else -> "中文"
//        }
//        "zh" -> when (this.country) {
//            "HK" -> "傳統中文 (香港)"
//            "MO" -> "傳統中文 (澳門)"
//            "TW" -> "傳統中文 (台灣)"
//            "CN" -> "简体中文 (中国)"
//            "SG" -> "简体中文 (新加坡)"
//            else -> "简体中文"
//        }
//
        "yue" -> when (this.country) {
            "CN" -> "粤语"
            else -> "粵語"
        }

        "" -> {
            val defaultLocale = LocaleListCompat.getDefault().get(0)
            var name = defaultLocale?.getDisplayLanguage(defaultLocale)
            defaultLocale?.getCustomDisplayCountry().takeIf { it.isNullOrBlank().not() }?.let { name += ", $it" }
            "跟随系统 ($name)"
        }

//        else -> this.displayName
        // 参数是显示语言，例如当 this 是 Locale("zh")，参数是 Locale("en") 时显示为 "Chinese"，参数是 Locale("zh") 时显示为 "中文"
        else -> this.getDisplayName(this)
    }
}

fun Locale.getCustomDisplayLanguage(): String {
    return when (this.language) {
//        "zh" -> "中文"
        "zh" -> when (this.country) {
            "HK" -> "傳統中文"
            "MO" -> "傳統中文"
            "TW" -> "傳統中文"
            "CN" -> "简体中文"
            "SG" -> "简体中文"
            else -> "简体中文"
        }

        "yue" -> when (this.country) {
            "CN" -> "粤语"
            else -> "粵語"
        }

        "" -> "跟随系统"
//        else -> this.displayLanguage
        else -> this.getDisplayLanguage(this)
    }
}

fun Locale.getCustomDisplayScript(): String {
    return when (this.script) {
        "Hant" -> "漢字"
        "hans" -> "汉字"
//        else -> this.displayScript
        else -> this.getDisplayScript(this)
    }
}

// 用于显示国家或地区名称（方法名是对应 Java API 的 getDisplayCountry）
fun Locale.getCustomDisplayCountry(): String {
    return when (this.country) {
        "HK" -> "香港"
        "MO" -> "澳門"
        "TW" -> "台灣"
        "CN" -> "中国"
        "SG" -> "新加坡"
        "US" -> "US"
//        else -> this.displayCountry
        else -> this.getDisplayCountry(this)
    }
}
