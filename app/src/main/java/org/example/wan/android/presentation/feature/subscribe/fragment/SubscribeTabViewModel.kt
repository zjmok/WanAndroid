package org.example.wan.android.presentation.feature.subscribe.fragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import org.example.wan.android.data.model.DataX
import org.example.wan.android.data.repository.WanRepository
import org.example.wan.android.presentation.feature.common.ArticleListDataSource
import org.example.wan.android.presentation.feature.common.LikeViewModel

class SubscribeTabViewModel : LikeViewModel() {

    fun getArticlesPager(
        id: Int,
    ): Pager<Int, DataX> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                ArticleListDataSource(firstPage = 1) {
                    WanRepository.getWxArticleList(id = id, page = it)
                }
            },
        )
    }

    fun searchWxArticleList(
        id: Int,
        key: String,
    ): Pager<Int, DataX> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                ArticleListDataSource(firstPage = 1) {
                    WanRepository.searchWxArticleList(id = id, key = key, page = it)
                }
            },
        )
    }

}