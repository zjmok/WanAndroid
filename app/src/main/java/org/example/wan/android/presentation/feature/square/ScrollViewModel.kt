package org.example.wan.android.presentation.feature.square

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.example.wan.android.presentation.feature.base.BaseViewModel

/**
 * 用于在父 Fragment 更新子 Fragment 的列表滚动
 */
class ScrollViewModel : BaseViewModel() {

    /**
     * 包装一个对象，保证数据唯一，每次都能接收更新
     * 1 滚动 index
     * 2 目标标识，使用 class simpleName
     * 3 唯一标识，使用的是时间戳
     */
    private val _scrollEvent = MutableLiveData<Triple<Int, String, Long>>()

    // 被观察数据，用于更新 UI
    // lifecycleScope + repeatOnLifecycle + StateFlow 同样可以
    val scrollEvent: LiveData<Triple<Int, String, Long>> = _scrollEvent

    fun scrollList(int: Int, target: String) {
        _scrollEvent.value = Triple(int, target, System.currentTimeMillis())
    }

}