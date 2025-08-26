package com.example.wan.android.presentation.feature.common

import androidx.lifecycle.MutableLiveData
import com.example.wan.android.presentation.feature.base.BaseViewModel
import com.example.wan.android.constant.AppConst
import com.example.wan.android.data.repository.WanRepository

open class ArticleWebViewModel : BaseViewModel() {

    val like = MutableLiveData<Boolean>()

    fun likeArticle(id: Int, originId: Int, isMyLike: Boolean) {
        launch {
            if (isMyLike) {
                WanRepository.likeArticle(originId)
            } else {
                WanRepository.likeArticle(id)
            }
            like.postValue(true)
        }
    }

    fun unlikeArticle(id: Int, originId: Int, isMyLike: Boolean) {
        launch {
            if (isMyLike) {
                WanRepository.unlikeMyLike(id, originId)
            } else {
                WanRepository.unlikeArticle(id)
            }
            like.postValue(false)
        }
    }

}