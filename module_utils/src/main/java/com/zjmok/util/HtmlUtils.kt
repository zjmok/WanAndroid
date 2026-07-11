package com.zjmok.util

import android.text.Spanned
import androidx.core.text.HtmlCompat

// 传统样式，会保留换行。块级元素转换为两个换行符分隔
fun fromHtmlLegacy(source: String?): Spanned {
    return HtmlCompat.fromHtml("$source", HtmlCompat.FROM_HTML_MODE_LEGACY)
}

// 紧凑样式，忽略多余的换行。块级元素转换为一个换行符分隔
fun fromHtmlCompact(source: String?): Spanned {
    return HtmlCompat.fromHtml("$source", HtmlCompat.FROM_HTML_MODE_COMPACT)
}
