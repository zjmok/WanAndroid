package org.example.wan.android.data.model

data class BannerItem(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String,
    var collect: Boolean, // 接口没有返回这个字段的
)