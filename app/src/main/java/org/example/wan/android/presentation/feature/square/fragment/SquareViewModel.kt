package org.example.wan.android.presentation.feature.square.fragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import org.example.wan.android.data.model.DataX
import org.example.wan.android.data.repository.WanRepository
import org.example.wan.android.presentation.feature.common.ArticleListDataSource
import org.example.wan.android.presentation.feature.common.LikeViewModel

class SquareViewModel : LikeViewModel() {

    fun getArticlesPager(): Pager<Int, DataX> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                ArticleListDataSource(
                    firstPage = 0,
                    {

                    },
                    {

                    },
                ) {
                    WanRepository.getSquareList(page = it)
                }
            },
        )
    }

}