package org.example.wan.android.presentation.feature.base.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import org.example.wan.android.presentation.feature.base.BaseViewModel
import org.example.wan.android.presentation.feature.login.LoginActivity
import com.zjmok.util.toast

abstract class VVMBaseFragment<VM : BaseViewModel, VB : ViewBinding> : VBaseFragment<VB>() {

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelObserve()
    }

    protected open fun viewModelObserve() {
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onLoginSucceed()
            } else {
                onCancelLogin()
            }
        }
        viewModel.loginStatus.observe(viewLifecycleOwner) {
            launcher.launch(Intent(activity, LoginActivity::class.java))
        }
    }

    protected open fun onLoginSucceed() {
        toast("登录成功! 您继续表演!")
    }

    protected open fun onCancelLogin() {

    }

}