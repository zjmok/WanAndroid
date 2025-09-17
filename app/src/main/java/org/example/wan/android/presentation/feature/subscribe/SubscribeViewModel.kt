package org.example.wan.android.presentation.feature.subscribe

import androidx.lifecycle.MutableLiveData
import org.example.wan.android.presentation.feature.base.BaseViewModel
import org.example.wan.android.data.model.ArticlesTreeItem
import org.example.wan.android.data.repository.WanRepository

class SubscribeViewModel : BaseViewModel() {

//    val articlesTree = liveData {
////        startLoading()
//        try {
//            val result = WanRepository.getWxArticleTree()
//            emit(result)
//        } catch (e: Exception) {
//            loge(e)
//        }
//        stopLoading()
//    }

    val articlesTree = MutableLiveData<ArrayList<ArticlesTreeItem>?>()

    fun fetchArticlesTree() {
        launch(onError = {
            Result
            articlesTree.postValue(null)
        }) {
            val result = WanRepository.getWxArticleTree()
            articlesTree.postValue(result)
        }
    }

}