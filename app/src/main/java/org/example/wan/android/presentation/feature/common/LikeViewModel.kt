package org.example.wan.android.presentation.feature.common

import androidx.lifecycle.MutableLiveData
import org.example.wan.android.presentation.feature.base.BaseViewModel
import org.example.wan.android.data.model.LikeData
import org.example.wan.android.data.repository.WanRepository

open class LikeViewModel : BaseViewModel() {

    val likeStatus = MutableLiveData<LikeData>()

    open fun like(likeData: LikeData, isMyLike: Boolean = false) {
        launch {
            if (likeData.like) {
                WanRepository.likeArticle(likeData.id)
                likeStatus.postValue(likeData)
            } else {
                if (isMyLike) {
                    WanRepository.unlikeMyLike(likeData.id, likeData.originId)
                } else {
                    WanRepository.unlikeArticle(likeData.id)
                }
                likeStatus.postValue(likeData)
            }
        }
    }

}