package com.example.wan.android.presentation.feature.square.fragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.wan.android.data.model.DataX
import com.example.wan.android.data.repository.WanRepository
import com.example.wan.android.presentation.feature.common.ArticleListDataSource
import com.example.wan.android.presentation.feature.common.LikeViewModel

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