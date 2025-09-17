package org.example.wan.android.data.model

import android.os.Parcelable
import org.example.wan.android.util.htmlDecode
import kotlinx.parcelize.Parcelize

@Deprecated("直接用 ArrayList<ArticlesTreeItem>")
class ArticlesTree : ArrayList<ArticlesTreeItem>()

@Parcelize
data class ArticlesTreeItem(
    val articleList: List<Articles>,
    val author: String,
    val children: List<String>, // 返回的 list 是空的 类型是我瞎猜的
    val courseId: Int,
    val cover: String,
    val desc: String,
    val id: Int,
    val lisense: String,
    val lisenseLink: String,
    @Deprecated("使用 nameDecoded 获取")
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val type: Int,
    val userControlSetTop: Boolean,
    val visible: Int
) : Parcelable {
    @Suppress("DEPRECATION")
    val nameDecoded: String get() = name.htmlDecode()
}
