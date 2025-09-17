package org.example.wan.android.data.model

import androidx.annotation.StringDef

@StringDef(
    *[
        ArticlePage.PERSON,
        ArticlePage.HOME,
        ArticlePage.STUDY,
        ArticlePage.SQUARE,
        ArticlePage.NAV,
        ArticlePage.TUTORIALS,
        ArticlePage.QA,
        ArticlePage.PROJECT_HOT,
        ArticlePage.SUBSCRIBE,
        ArticlePage.PROJECT,
        ArticlePage.TOOLS,
        ArticlePage.ARTICLE_LIST,
    ]
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArticlePage {

    companion object {

        const val PERSON = "我的"
        const val HOME = "推荐"
        const val STUDY = "学习路线"
        const val SQUARE = "广场"
        const val NAV = "导航"
        const val TUTORIALS = "教程"
        const val QA = "问答"
        const val PROJECT_HOT = "最新项目"
        const val SUBSCRIBE = "公众号"
        const val PROJECT = "项目分类"
        const val TOOLS = "工具"
        const val ARTICLE_LIST = "文章列表"

    }

}
