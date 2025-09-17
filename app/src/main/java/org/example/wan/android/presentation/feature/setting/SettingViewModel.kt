package org.example.wan.android.presentation.feature.setting

import androidx.lifecycle.MutableLiveData
import org.example.wan.android.presentation.feature.base.BaseViewModel
import org.example.wan.android.data.repository.WanRepository

class SettingViewModel : BaseViewModel() {

    val logStatus = MutableLiveData<Boolean>()

    fun logout() {
        launch {
            WanRepository.logout()
            onLogout()
            logStatus.postValue(false)
        }
    }

}
