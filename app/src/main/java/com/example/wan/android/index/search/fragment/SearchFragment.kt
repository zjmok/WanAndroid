package com.example.wan.android.index.search.fragment

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.liveData
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.example.wan.android.R
import com.example.wan.android.base.fragment.VVMBaseFragment
import com.example.wan.android.constant.EventBus
import com.example.wan.android.data.model.LikeData
import com.example.wan.android.data.model.WebData
import com.example.wan.android.databinding.FragmentSearchBinding
import com.example.wan.android.index.common.ArticleListPagingAdapter
import com.example.wan.android.index.common.ArticleWebActivity
import com.example.wan.android.utils.ext.gone
import com.example.wan.android.utils.ext.visible
import com.example.wan.android.utils.getViewModel
import com.example.wan.android.utils.newIntent
import com.example.wan.android.utils.observeEvent
import com.example.wan.android.utils.registerResultOK
import com.lxj.xpopup.XPopup
import splitties.bundle.put
import splitties.views.onClick

class SearchFragment : VVMBaseFragment<SearchViewModel, FragmentSearchBinding>() {

    override val viewModel: SearchViewModel by lazy { getViewModel() }
    override val binding: FragmentSearchBinding by viewBinding(CreateMethod.INFLATE)

    private val adapter by lazy { ArticleListPagingAdapter() }

    private var isRefreshing = false

    private var key: String = ""
    private var isInitialized = false

    // 控制可见时显示一次对话框
    private var shouldShowDialog = true

    private val popupView by lazy {
        XPopup.Builder(requireContext())
            .asInputConfirm("站内搜索", "", "请输入关键字") {
                if (it.isNotBlank()) {
                    search(it)
                }
            }
    }

    companion object {
        fun getInstance(isPaddingTop: Boolean) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    put("isPaddingTop", isPaddingTop)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observe()

    }

    override fun onResume() {
        super.onResume()

        if (shouldShowDialog) {
            shouldShowDialog = false
            showSearchDialog()
        }
    }

    private fun showSearchDialog() {
        popupView.apply {
            inputContent = key
        }.show()
    }

    private fun search(key: String) {
        this.key = key
        viewModel.getArticlesPager(key).liveData
            .observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
                binding.refresh.isRefreshing = false
            }
    }

    private fun observe() {
        viewModel.likeStatus.observe(viewLifecycleOwner) {
            adapter.notifyLikeChanged(it)
        }
    }

    private fun initView() {
        val recyclerView = binding.rv
        recyclerView.adapter = adapter

        adapter.addLoadStateListener {
            isInitialized = true

            when (it.refresh) {
                is LoadState.Loading -> {
                    if (isRefreshing.not()) {
                        binding.progressBar.visible()
                    }
                }

                is LoadState.NotLoading -> {
                    binding.progressBar.gone()
                    isRefreshing = false
                }

                is LoadState.Error -> {
                    binding.progressBar.gone()
                    isRefreshing = false
                    binding.refresh.isRefreshing = false
                    LogUtils.e(it.toString())
                }
            }

            // 空数据 显示空页面
            val isEmpty = it.refresh is LoadState.NotLoading &&
                    it.append.endOfPaginationReached &&
                    adapter.itemCount < 1

            binding.rv.visible(isEmpty.not())
            binding.viewEmpty.visible(isEmpty)

        }

        adapter.onLikeClick {
            viewModel.like(it, true)
        }

        val launcher = registerResultOK {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it?.getParcelableExtra("result", WebData::class.java)
            } else {
                @Suppress("DEPRECATION")
                it?.getParcelableExtra("result")
            }
            result?.let { data ->
                if (adapter.itemCount == 0) {
                    return@let
                }
                adapter.notifyLikeChanged(
                    LikeData(
                        id = data.id,
                        like = data.like,
                        position = data.position,
                    )
                )
            }
        }
        adapter.onItemClick { position, dataX ->
            launcher.launch(newIntent<ArticleWebActivity> {
                putExtra(
                    "data", WebData(
                        id = dataX.id,
                        url = dataX.link,
                        title = dataX.title,
                        author = dataX.author,
                        like = dataX.collect,
                        position = position,
                    )
                )
            })
        }

        binding.refresh.setOnRefreshListener {
            isRefreshing = true
            adapter.refresh()

            // 页面未加载时 下拉刷新结束动画
            if (isInitialized.not()) {
                binding.refresh.isRefreshing = false
            }
        }

        binding.viewEmpty.onClick {
            showSearchDialog()
        }

        createMenu()
    }

    private fun createMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search_activity, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_item_search) {
                    showSearchDialog()
                }
                return true
            }
        })
    }

    override fun observeBus() {
        observeEvent<Int>(EventBus.HOME_TAB_CHANGED) {

        }
        observeEvent<Int>(EventBus.HOME_TAB_REFRESH) {
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                binding.rv.smoothScrollToPosition(0)
                binding.rv.post {
                    isRefreshing = true
                    binding.refresh.isRefreshing = true
                    adapter.refresh()
                }
            }
        }
    }

}