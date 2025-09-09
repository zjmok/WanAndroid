package com.example.wan.android.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * 默认 不进行 Http 转义
 * 默认 格式化 Json 字符串
 */
fun Any.toJson(escape: Boolean = false, format: Boolean = true): String? {
    return try {
        val gson = GsonBuilder().apply {
            if (escape.not()) {
                disableHtmlEscaping()
            }
            if (format) {
                setPrettyPrinting()
            }
        }.create()
//        GsonUtils.toJson(gson, this)
        gson.toJson(this) // Gson 是通过反射获取 this 的实际类型，这里不需要使用 reified T
    } catch (e: Exception) {
        loge(e)
        null
    }
}

inline fun <reified T> fromJson(json: String?): T? {
    return try {
//        GsonUtils.fromJson(GsonUtils.getGson(), json, T::class.java)
        Gson().fromJson(json, T::class.java)
    } catch (e: Exception) {
        loge(e)
        null
    }
}

inline fun <reified T> json2Instance(json: String?) = fromJson<T>(json)

inline fun <reified T> json2Object(json: String?) = fromJson<T>(json)
