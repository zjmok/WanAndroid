package com.example.wan.android.index.subscribe

import androidx.lifecycle.MutableLiveData
import com.example.wan.android.base.BaseViewModel
import com.example.wan.android.data.model.ArticlesTreeItem
import com.example.wan.android.data.repository.WanRepository

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
            articlesTree.postValue(null)
        }) {
            val result = WanRepository.getWxArticleTree()
            articlesTree.postValue(result)
        }
    }

}