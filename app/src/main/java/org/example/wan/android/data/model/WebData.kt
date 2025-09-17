package org.example.wan.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebData(
    val isMyLike: Boolean = false,
    val id: Int,
    val originId: Int = -1,
    val url: String,
    val title: String? = null,
    val author: String? = null,
    var like: Boolean,
    val position: Int,
    var position2: Int? = null, // 适配二维列表
) : Parcelable
