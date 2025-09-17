package org.example.wan.android.data.model

data class CommentList(
    val curPage: Int,
    val datas: List<Comment>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class Comment(
    val anonymous: Int,
    val appendForContent: Int,
    val articleId: Int,
    val canEdit: Boolean,
    val content: String,
    val contentMd: String,
    val id: Int,
    val niceDate: String,
    val publishDate: Long,
    val replyCommentId: Int,
    val replyComments: List<ReplyComment>,
    val rootCommentId: Int,
    val status: Int,
    val toUserId: Int,
    val toUserName: String,
    val userId: Int,
    val userName: String,
    val zan: Int
)

data class ReplyComment(
    val anonymous: Int,
    val appendForContent: Int,
    val articleId: Int,
    val canEdit: Boolean,
    val content: String,
    val contentMd: String,
    val id: Int,
    val niceDate: String,
    val publishDate: Long,
    val replyCommentId: Int,
    val replyComments: List<Any>,
    val rootCommentId: Int,
    val status: Int,
    val toUserId: Int,
    val toUserName: String,
    val userId: Int,
    val userName: String,
    val zan: Int
)