package com.example.wan.android.presentation.feature.project.fragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.wan.android.data.model.DataX
import com.example.wan.android.data.repository.WanRepository
import com.example.wan.android.presentation.feature.common.ArticleListDataSource
import com.example.wan.android.presentation.feature.common.LikeViewModel

class ProjectTabViewModel : LikeViewModel() {

    fun getArticlesPager(
        id: Int?,
    ): Pager<Int, DataX> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                if (id == null) { // 前面手动加在最前面的null 最新项目
                    ArticleListDataSource(firstPage = 0) {
                        WanRepository.getNewProjectList(page = it)
                    }
                } else {
                    ArticleListDataSource(firstPage = 1) {
                        WanRepository.getProjectList(id = id, page = it)
                    }
                }
            },
        )
    }

}