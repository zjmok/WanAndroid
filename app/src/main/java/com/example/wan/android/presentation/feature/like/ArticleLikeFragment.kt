package com.example.wan.android.presentation.feature.like

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.paging.LoadState
import androidx.paging.liveData
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import com.example.wan.android.presentation.feature.base.fragment.VVMBaseFragment
import com.example.wan.android.constant.EventBus
import com.example.wan.android.data.model.WebData
import com.example.wan.android.databinding.FragmentArticleLikeBinding
import com.example.wan.android.presentation.feature.common.ArticleListPagingAdapter
import com.example.wan.android.presentation.feature.common.ArticleWebActivity
import com.example.wan.android.util.gone
import com.example.wan.android.util.visible
import com.example.wan.android.util.getViewModel
import com.example.wan.android.util.newIntent
import com.example.wan.android.util.postEvent
import com.example.wan.android.util.registerResultOK

class ArticleLikeFragment : VVMBaseFragment<ArticleLikeViewModel, FragmentArticleLikeBinding>() {

    override val viewModel: ArticleLikeViewModel by lazy { getViewModel() }
    override val binding: FragmentArticleLikeBinding by viewBinding(CreateMethod.INFLATE)

    private val adapter by lazy { ArticleListPagingAdapter() }

    private var isRefreshing = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observe()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onBackPress(): Boolean {
        postEvent(EventBus.PERSON_PAGE_BACK, 0, 500)
        return false
    }

    private fun observe() {
        viewModel.getArticlesPager().liveData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
            binding.refresh.isRefreshing = false
        }
        viewModel.likeStatus.observe(viewLifecycleOwner) {
            // paging 不能直接删除 adapter 的数据源
            // 更新数据
            adapter.refresh()
        }
    }

    private fun initView() {
        val recyclerView = binding.rv
        recyclerView.adapter = adapter

        adapter.addLoadStateListener {
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
            result?.let {
                // paging 不能直接删除 adapter 的数据源
                // 更新数据
                adapter.refresh()
            }
        }
        adapter.onItemClick { position, dataX ->
            launcher.launch(newIntent<ArticleWebActivity> {
                putExtra(
                    "data", WebData(
                        isMyLike = true,
                        id = dataX.id,
                        originId = dataX.originId,
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
        }
    }

}