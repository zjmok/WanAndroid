package com.example.wan.android.presentation.feature.person

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.wan.android.R
import com.example.wan.android.presentation.feature.base.fragment.VVMBaseFragment
import com.example.wan.android.constant.EventBus
import com.example.wan.android.data.model.SuperUserInfo
import com.example.wan.android.databinding.FragmentPersonBinding
import com.example.wan.android.presentation.feature.like.ArticleLikeActivity
import com.example.wan.android.presentation.feature.login.LoginActivity
import com.example.wan.android.presentation.feature.setting.SettingActivity
import com.example.wan.android.util.UserUtils
import com.example.wan.android.util.dp2pxInt
import com.example.wan.android.util.load
import com.example.wan.android.util.getViewModel
import com.example.wan.android.util.observeEvent
import com.example.wan.android.util.toast
import splitties.fragments.start
import splitties.views.onClick

class PersonFragment : VVMBaseFragment<PersonViewModel, FragmentPersonBinding>() {

    override val viewModel: PersonViewModel get() = getViewModel()
    override val binding: FragmentPersonBinding by viewBinding(CreateMethod.INFLATE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observe()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initData() {
        if (UserUtils.isLogin) {
            viewModel.getUserInfo()
        } else {
            binding.refresh.isRefreshing = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.superUserInfo.observe(viewLifecycleOwner) {
            setInfo(it)
            binding.refresh.isRefreshing = false
        }
    }

    private fun resetInfo() {
        val superUserInfo = SuperUserInfo()
        setInfo(superUserInfo)
    }

    @SuppressLint("SetTextI18n")
    private fun setInfo(it: SuperUserInfo) {
        binding.run {
            it.collectArticleInfo?.let {
                tvLikeNum.text = "收藏量: ${it.count} 篇"
            }
            it.coinInfo?.let {
                tvPoints.text = "积分: ${it.coinCount}"
                tvRanking.text = "排行: ${it.rank}"
            }
            it.userInfo.let {
                iv.load(
                    any = it.icon.ifBlank { R.mipmap.ic_launcher },
                    cornerRadius = 10.dp2pxInt,
                    placeholderRes = R.mipmap.ic_launcher,
                )
                tvNickname.text = it.nickname
                tvUsername.text = "用户名: ${it.username}"
                tvId.text = "ID: ${it.id}"
                tvEmail.text = "邮箱: ${it.email}"
            }
        }
    }

    private fun initView() {
        resetInfo()
        val superUserInfo = UserUtils.getSuperUserInfo()
        if (superUserInfo != null) {
            setInfo(superUserInfo)
        }

        binding.refresh.setOnRefreshListener {
            initData()
        }

        val loginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    onLoginSucceed()
                } else {
                    onCancelLogin()
                }
            }
        val settingLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    resetInfo()
                }
            }
        binding.run {
            layoutInfo.onClick {
                if (UserUtils.isLogin.not()) {
                    loginLauncher.launch(Intent(activity, LoginActivity::class.java))
                } else {

                }
            }

            llCoin.onClick {
                if (UserUtils.isLogin.not()) {
                    loginLauncher.launch(Intent(activity, LoginActivity::class.java))
                } else {
                    toast("开发中")
                }
            }

            llArticle.onClick {
                if (UserUtils.isLogin.not()) {
                    loginLauncher.launch(Intent(activity, LoginActivity::class.java))
                } else {
                    start<ArticleLikeActivity> {}
                }
            }
            llShared.onClick {
                if (UserUtils.isLogin.not()) {
                    loginLauncher.launch(Intent(activity, LoginActivity::class.java))
                } else {
                    toast("开发中")
                }
            }
            llSite.onClick {
                if (UserUtils.isLogin.not()) {
                    loginLauncher.launch(Intent(activity, LoginActivity::class.java))
                } else {
                    toast("开发中")
                }
            }

            llHistory.onClick {
                start<HistoryActivity> {}
            }
            llBookmark.onClick {
                start<BookmarkActivity> {}
            }

            llSettings.onClick {
                if (UserUtils.isLogin) {
                    // 过渡动画 共享元素 test
                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(),
                        layoutInfo,
                        "shared_element"
                    )
                    settingLauncher.launch(Intent(activity, SettingActivity::class.java), option)
                } else {
                    settingLauncher.launch(Intent(activity, SettingActivity::class.java))
                }
            }
        }
    }

    override fun observeBus() {
        // 页面返回时
        observeEvent<Any>(EventBus.PERSON_PAGE_BACK) {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                initData()
            }
        }
        // tab 切换时
        observeEvent<Int>(EventBus.HOME_TAB_CHANGED) {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                initData()
            }
        }
        // 再次点击 tab 刷新时
        observeEvent<Int>(EventBus.HOME_TAB_REFRESH) {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                binding.refresh.isRefreshing = true
                initData()
            }
        }
    }

    override fun onLoginSucceed() {
        val superUserInfo = UserUtils.getSuperUserInfo()
        if (superUserInfo != null) {
            setInfo(superUserInfo)
        }
        // 登录注册接口没有返回其它信息 获取完整的信息
        initData()
    }

    override fun onCancelLogin() {

    }

}