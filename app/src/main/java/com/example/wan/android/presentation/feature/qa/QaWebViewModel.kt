package com.example.wan.android.presentation.feature.qa

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.example.wan.android.data.model.CommentList
import com.example.wan.android.data.repository.WanRepository
import com.example.wan.android.presentation.feature.common.ArticleWebViewModel

class QaWebViewModel : ArticleWebViewModel() {

    val commentList = MutableLiveData<CommentList>()

    fun getQACommentList(id: Int) {
        launch {
            val result = WanRepository.getQACommentList(id)
            LogUtils.e(result)
            commentList.postValue(result)
        }
    }

}