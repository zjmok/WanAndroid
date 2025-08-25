package com.example.wan.android.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.wan.android.base.dialog.LoadingDialog

abstract class BaseFragment(@LayoutRes layoutID: Int = 0) : Fragment(layoutID) {

    protected val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private var isLoaded = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 使用 androidx.appcompat:appcompat:1.1.0 或更高版本，可以使用 OnBackPressedCallback 来处理 onBackPressed 事件
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 在这里处理返回键事件
                    if (shouldInterceptBackPress()) {
                        // 拦截处理返回事件
                        val consumed = onBackPress()
                        if (consumed.not()) {
                            // 不消费掉事件，activity 将继续响应
                            isEnabled = false // 禁用当前的回调
                            requireActivity().onBackPressed() // 调用默认的返回操作
                        }
                    } else {
                        // 不拦截
                        isEnabled = false // 禁用当前的回调
                        requireActivity().onBackPressed() // 调用默认的返回操作
                    }
                }
            })
    }

    protected open fun shouldInterceptBackPress(): Boolean {
        // 返回 true 表示拦截返回事件
        return true
    }

    protected open fun onBackPress(): Boolean {
        // 返回 true 表示不消费掉事件，activity 将继续响应
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeBus()
    }

    override fun onResume() {
        super.onResume()
        // ViewPager2 默认可见时才执行 onResume,
        // 其它情况请使用 FragmentTransaction.setMaxLifecycle 控制 Fragment 的生命周期
        if (isLoaded.not()) {
            onLazyLoad()
            isLoaded = true
        }
    }

    protected open fun onLazyLoad() {

    }

    protected open fun observeBus() {

    }

    protected open fun showLoading() {
        loadingDialog.show()
    }

    protected open fun dismissLoading() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    val fragment get() = this

}