package org.example.wan.android.data.model

data class SuperUserInfo(
    val coinInfo: CoinInfo?,
    val collectArticleInfo: CollectArticleInfo?,
    val userInfo: UserInfo
) {
    constructor() : this(
        CoinInfo(),
        CollectArticleInfo(),
        UserInfo(),
    )
}

//@Parcelize
data class UserInfo(
    val admin: Boolean,
    val chapterTops: List<Any>,
    val coinCount: Int,
    val collectIds: List<Int>,
    val email: String,
    val icon: String,
    val id: Int,
    val nickname: String,
    val password: String,
    val publicName: String,
    val token: String,
    val type: Int,
    val username: String
)// : Parcelable
{
    constructor() : this(
        false,
        listOf(),
        -1,
        listOf(),
        "null",
        "",
        -1,
        "未登录",
        "",
        "",
        "",
        -1,
        "null",
    )
}

data class CoinInfo(
    val coinCount: Int,
    val level: Int,
    val nickname: String,
    val rank: Int,
    val userId: Int,
    val username: String
) {
    constructor() : this(
        -1,
        -1,
        "",
        -1,
        -1,
        "",
    )
}

data class CollectArticleInfo(
    val count: Int
) {
    constructor() : this(
        -1,
    )
}
