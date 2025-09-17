package org.example.wan.android.presentation.feature.login

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import org.example.wan.android.presentation.feature.base.BaseViewModel
import org.example.wan.android.data.model.LoginFormState
import org.example.wan.android.data.model.SuperUserInfo
import org.example.wan.android.data.repository.WanRepository
import org.example.wan.android.util.UserUtils

class LoginViewModel : BaseViewModel() {

    val loginFormState = MutableLiveData<LoginFormState>()

    val result = MutableLiveData<Boolean>()

    fun login(username: String, password: String) {
        launch {
            val result = WanRepository.login(username, password)
            // 保存用户信息
            // 获取用户信息接口返回的数据包含 UserInfo 和其它信息, 而登录注册只返回 UserInfo
            UserUtils.saveSuperUserInfo(
                SuperUserInfo(
                    userInfo = result,
                    coinInfo = null,
                    collectArticleInfo = null,
                )
            )
            this.result.postValue(true)
        }
    }

    fun register(username: String, password: String) {
        launch {
            val result = WanRepository.register(username, password)
            UserUtils.saveSuperUserInfo(
                SuperUserInfo(
                    userInfo = result,
                    coinInfo = null,
                    collectArticleInfo = null,
                )
            )
            this.result.postValue(true)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            loginFormState.value = LoginFormState(usernameError = "用户名格式有误")
        } else if (!isPasswordValid(password)) {
            loginFormState.value = LoginFormState(passwordError = "密码格式有误")
        } else {
            loginFormState.value = LoginFormState(isDataValid = true)
        }
    }

    // 用户名校验
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // 密码校验
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}