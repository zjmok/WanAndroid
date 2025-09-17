package org.example.wan.android.presentation.feature.project.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.liveData
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import org.example.wan.android.presentation.feature.base.fragment.VVMBaseFragment
import org.example.wan.android.constant.EventBus
import org.example.wan.android.data.model.ArticlesTreeItem
import org.example.wan.android.data.model.LikeData
import org.example.wan.android.data.model.WebData
import org.example.wan.android.databinding.FragmentProjectTabBinding
import org.example.wan.android.presentation.feature.common.ArticleListPagingAdapter
import org.example.wan.android.presentation.feature.common.ArticleWebActivity
import org.example.wan.android.util.gone
import org.example.wan.android.util.visible
import org.example.wan.android.util.getViewModel
import org.example.wan.android.util.newIntent
import org.example.wan.android.util.observeEvent
import org.example.wan.android.util.registerResultOK
import splitties.views.onClick

class ProjectTabFragment : VVMBaseFragment<ProjectTabViewModel, FragmentProjectTabBinding>() {

    override val viewModel: ProjectTabViewModel get() = getViewModel()
    override val binding: FragmentProjectTabBinding by viewBinding(CreateMethod.INFLATE)

    private val adapter by lazy { ArticleListPagingAdapter() }

    private var isRefreshing = false

    private var item: ArticlesTreeItem? = null

    companion object {
        fun getInstance(data: ArticlesTreeItem?) = ProjectTabFragment().apply {
            arguments = Bundle().apply {
                putParcelable("data", data)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("data", ArticlesTreeItem::class.java)
        } else {
            arguments?.getParcelable("data")
        }

        initView()
        observe()
    }

    override fun onLazyLoad() {
        viewModel.getArticlesPager(id = item?.id).liveData
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
            val isEmpty = ((it.refresh is LoadState.NotLoading && it.append.endOfPaginationReached) ||
                    it.refresh is LoadState.Error) &&
                    adapter.itemCount < 1

            binding.rv.visible(isEmpty.not())
            binding.viewEmpty.visible(isEmpty)

        }

        binding.viewEmpty.onClick {
            adapter.refresh()
        }

        adapter.onLikeClick {
            viewModel.like(it)
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
        }

        binding.ivTop.onClick {
            recyclerView.smoothScrollToPosition(0)
        }

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