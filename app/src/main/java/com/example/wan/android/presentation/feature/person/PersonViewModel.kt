package com.example.wan.android.presentation.feature.person

import androidx.lifecycle.MutableLiveData
import com.example.wan.android.presentation.feature.base.BaseViewModel
import com.example.wan.android.data.model.SuperUserInfo
import com.example.wan.android.data.repository.WanRepository
import com.example.wan.android.util.UserUtils

class PersonViewModel : BaseViewModel() {

    val superUserInfo = MutableLiveData<SuperUserInfo>()

    fun getUserInfo() {
        launch {
            val result = WanRepository.getUserInfo()
            // 保存用户信息
            // 获取用户信息接口返回的数据包含 UserInfo 和其它信息, 而登录注册只返回 UserInfo
            UserUtils.saveSuperUserInfo(result)
            superUserInfo.postValue(result)
        }
    }

}