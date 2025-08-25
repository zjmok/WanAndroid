package com.example.wan.android.index.subscribe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.BarUtils
import com.example.wan.android.base.fragment.VVMBaseFragment
import com.example.wan.android.constant.EventBus
import com.example.wan.android.databinding.FragmentSubscribeBinding
import com.example.wan.android.index.common.VpFragmentAdapter
import com.example.wan.android.index.subscribe.fragment.SubscribeTabFragment
import com.example.wan.android.utils.ext.visible
import com.example.wan.android.utils.getViewModel
import com.example.wan.android.utils.observeEvent
import com.google.android.material.tabs.TabLayoutMediator
import splitties.bundle.put
import splitties.views.onClick
import splitties.views.topPadding

class SubscribeFragment : VVMBaseFragment<SubscribeViewModel, FragmentSubscribeBinding>() {

    override val viewModel: SubscribeViewModel get() = getViewModel()
    override val binding: FragmentSubscribeBinding by viewBinding(CreateMethod.INFLATE)

    companion object {
        fun getInstance(isPaddingTop: Boolean) =
            SubscribeFragment().apply {
                arguments = Bundle().apply {
                    put("isPaddingTop", isPaddingTop)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersion()
        observe()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initImmersion() {
        val isPaddingTop = arguments?.getBoolean("isPaddingTop")
        binding.tabLayout.topPadding = if (isPaddingTop == true) {
            BarUtils.getStatusBarHeight()
        } else {
            0
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        viewModel.articlesTree.observe(viewLifecycleOwner) {
            binding.viewEmpty.visible(it == null)
            if (it == null) {
                return@observe
            }

            // ViewPager
            val list = it.map { item -> SubscribeTabFragment.getInstance(item) }
            val vpAdapter = VpFragmentAdapter(this, list)
            binding.viewpager.adapter = vpAdapter
            binding.viewpager.currentItem = 0
            binding.viewpager.offscreenPageLimit = 1

            // TabLayout
            val nameList = it.map { it.nameDecoded }
            TabLayoutMediator(
                binding.tabLayout,
                binding.viewpager,
                true,
                false
            ) { tab, position ->
                tab.text = nameList[position]
            }.attach()

        }
        viewModel.fetchArticlesTree()

        binding.viewEmpty.onClick {
            viewModel.fetchArticlesTree()
        }
    }

    override fun observeBus() {
        observeEvent<Int>(EventBus.HOME_TAB_CHANGED) {

        }
//        observeEvent<Int>(EventBus.HOME_TAB_REFRESH) {
//            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
//                viewModel.fetchArticlesTree()
//            }
//        }
        // 这里不应该刷新, 应该提供空白页点击刷新
    }

}