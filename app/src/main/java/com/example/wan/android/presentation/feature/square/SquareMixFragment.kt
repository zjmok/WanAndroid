package com.example.wan.android.presentation.feature.square

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.BarUtils
import com.example.wan.android.presentation.feature.base.fragment.VBaseFragment
import com.example.wan.android.constant.EventBus
import com.example.wan.android.databinding.FragmentSquareMixBinding
import com.example.wan.android.presentation.feature.common.VpFragmentAdapter
import com.example.wan.android.presentation.feature.qa.fragment.QaFragment
import com.example.wan.android.presentation.feature.search.SearchActivity
import com.example.wan.android.presentation.feature.square.fragment.SquareFragment
import com.example.wan.android.util.observeEvent
import com.google.android.material.tabs.TabLayoutMediator
import splitties.bundle.put
import splitties.fragments.start
import splitties.views.onClick
import splitties.views.topPadding

class SquareMixFragment : VBaseFragment<FragmentSquareMixBinding>() {

    override val binding: FragmentSquareMixBinding by viewBinding(CreateMethod.INFLATE)

    companion object {
        fun getInstance(isPaddingTop: Boolean) =
            SquareMixFragment().apply {
                arguments = Bundle().apply {
                    put("isPaddingTop", isPaddingTop)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersion()
        initView()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initImmersion() {
        val isPaddingTop = arguments?.getBoolean("isPaddingTop")
        binding.appBarLayout.topPadding =
            if (isPaddingTop == true) {
                BarUtils.getStatusBarHeight()
            } else {
                0
            }
    }

    private fun initView() {
        val nameList = listOf(
//            "搜索",
            "广场",
            "问答"
        )

        // ViewPager
        val list = listOf(
//            SearchFragment.getInstance(false),
            SquareFragment.getInstance(false),
            QaFragment.getInstance(false),
        )
        val vpAdapter = VpFragmentAdapter(this, list)
        binding.viewpager.adapter = vpAdapter
        binding.viewpager.setCurrentItem(0, false)
        binding.viewpager.offscreenPageLimit = 1

        binding.viewpager.isUserInputEnabled = false // 禁止手动左右滑动

        // TabLayout
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager,
            true,
            false
        ) { tab, position ->
            tab.text = nameList[position]
        }.attach()

        binding.layoutSearch.onClick {
            start<SearchActivity> {}
        }
    }

    override fun observeBus() {
        observeEvent<Int>(EventBus.HOME_TAB_CHANGED) {

        }
    }

}