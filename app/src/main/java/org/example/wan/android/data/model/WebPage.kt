package org.example.wan.android.data.model

data class WebPage(
    val url: String,
    val title: String,
    val author: String? = null,
    val time: String, // yyyy/MM/dd HH:mm:ss
    val sticky: Boolean = false,
) {
    // data class 重载 构造函数
    constructor(sticky: Boolean) : this("", "", "", "", sticky)
}

data class CacheData(
    val pageList: List<WebPage>,
)