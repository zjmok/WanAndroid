package com.example.wan.android.presentation.feature.project

import androidx.lifecycle.MutableLiveData
import com.example.wan.android.presentation.feature.base.BaseViewModel
import com.example.wan.android.data.model.ArticlesTreeItem
import com.example.wan.android.data.repository.WanRepository

class ProjectViewModel : BaseViewModel() {

//    val articlesTree = liveData {
////        startLoading()
//        try {
//            val result = WanRepository.getProjectTree()
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
            val result = WanRepository.getProjectTree()
            articlesTree.postValue(result)
        }
    }

}