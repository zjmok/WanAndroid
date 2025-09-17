package org.example.wan.android.presentation.feature.like

import androidx.paging.Pager
import androidx.paging.PagingConfig
import org.example.wan.android.data.model.DataX
import org.example.wan.android.data.repository.WanRepository
import org.example.wan.android.presentation.feature.common.ArticleListDataSource
import org.example.wan.android.presentation.feature.common.LikeViewModel

class ArticleLikeViewModel : LikeViewModel() {

    fun getArticlesPager(): Pager<Int, DataX> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                ArticleListDataSource(firstPage = 0) {
                    WanRepository.getLikeList(page = it).apply {
                        datas.forEach { data ->
                            // 收藏列表全是已收藏 接口没有返回
                            data.collect = true
                        }
                    }
                }
            },
        )
    }

}