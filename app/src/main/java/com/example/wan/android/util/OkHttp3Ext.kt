package com.example.wan.android.util

import okhttp3.Headers
import okhttp3.internal.isSensitiveHeader

/**
 * okhttp3 的 4.x 版本, 敏感请求头的值被 ██ 替换了
 * 源码位置
 * okhttp3.Headers.toString()
 * okhttp3.internal.Util.isSensitiveHeader()
 */
fun Headers.toStringCustom(hideSensitiveHeaders: Boolean = false): String {
    return buildString {
        for (i in 0 until size) {
            val name = name(i)
            val value = value(i)
            append(name)
            append(": ")
            append(if (hideSensitiveHeaders && isSensitiveHeader(name)) "██" else value)
            append("\n")
        }
    }
}
