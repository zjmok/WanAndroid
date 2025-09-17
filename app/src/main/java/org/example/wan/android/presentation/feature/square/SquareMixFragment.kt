package org.example.wan.android.presentation.feature.square

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.tabs.TabLayoutMediator
import org.example.wan.android.constant.EventBus
import org.example.wan.android.databinding.FragmentSquareMixBinding
import org.example.wan.android.presentation.feature.base.fragment.VBaseFragment
import org.example.wan.android.presentation.feature.common.VpFragmentAdapter
import org.example.wan.android.presentation.feature.qa.fragment.QaFragment
import org.example.wan.android.presentation.feature.search.SearchActivity
import org.example.wan.android.presentation.feature.square.fragment.SquareFragment
import org.example.wan.android.util.observeEvent
import splitties.bundle.put
import splitties.fragments.start
import splitties.views.onClick
import splitties.views.topPadding

class SquareMixFragment : VBaseFragment<FragmentSquareMixBinding>() {

    override val binding: FragmentSquareMixBinding by viewBinding(CreateMethod.INFLATE)

    private val atyViewModel: ScrollViewModel by viewModels()

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

        binding.ivTop.onClick {
            val fragment = list[binding.viewpager.currentItem]
            if (fragment is SquareFragment) {
                atyViewModel.scrollList(0, SquareFragment::class.java.simpleName)
            } else if (fragment is QaFragment) {
                atyViewModel.scrollList(0, QaFragment::class.java.simpleName)
            }
        }

    }

    override fun observeBus() {
        observeEvent<Int>(EventBus.HOME_TAB_CHANGED) {

        }
    }

}