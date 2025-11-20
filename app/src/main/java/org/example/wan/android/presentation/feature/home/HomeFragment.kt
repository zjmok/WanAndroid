package org.example.wan.android.presentation.feature.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.paging.LoadState
import androidx.paging.liveData
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.BarUtils
import org.example.wan.android.presentation.feature.base.fragment.VVMBaseFragment
import org.example.wan.android.constant.EventBus
import org.example.wan.android.data.model.LikeData
import org.example.wan.android.data.model.WebData
import org.example.wan.android.databinding.FragmentHomeBinding
import org.example.wan.android.presentation.feature.common.ArticleWebActivity
import org.example.wan.android.presentation.feature.home.paging.HomePagingAdapter
import com.zjmok.util.gone
import com.zjmok.util.visible
import com.zjmok.util.getViewModel
import com.zjmok.util.loge
import com.zjmok.util.logi
import com.zjmok.util.newIntent
import org.example.wan.android.util.liveeventbus.observeEvent
import com.zjmok.util.registerResultOK
import splitties.bundle.put
import splitties.views.onClick
import splitties.views.topPadding

class HomeFragment : VVMBaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel get() = getViewModel()
    override val binding: FragmentHomeBinding by viewBinding(CreateMethod.INFLATE)

    private val adapter = HomePagingAdapter(this, listOf())

    private var isRefreshing = false

    companion object {
        fun getInstance(isPaddingTop: Boolean) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    put("isPaddingTop", isPaddingTop)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImmersion()
        initView()
        observe()
    }

    override fun onLazyLoad() {
        viewModel.getArticlesPager().liveData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
            binding.refresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initImmersion() {
        val isPaddingTop = arguments?.getBoolean("isPaddingTop")
        binding.rv.topPadding = if (isPaddingTop == true) {
            BarUtils.getStatusBarHeight()
        } else {
            0
        }
    }

    private fun observe() {
        viewModel.banner.observe(viewLifecycleOwner) {
            adapter.setBanner(it)
        }
        viewModel.fetchBanner()

        viewModel.likeStatus.observe(viewLifecycleOwner) {
            adapter.notifyLikeChanged(it)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initView() {
        val recyclerView = binding.rv
        recyclerView.adapter = adapter

        adapter.addLoadStateListener {
            logi("it.prepend = ${it.prepend}")
            logi("it.refresh = ${it.refresh}")
            logi("it.append = ${it.append}")
            // 预加载
            when (it.prepend) {
                LoadState.Loading -> {
                    logi("预加载中...")
                }

                is LoadState.NotLoading -> {

                }

                is LoadState.Error -> {
                    loge(it)
                }
            }
            // 首次加载或刷新
            when (it.refresh) {
                LoadState.Loading -> {
                    logi("加载中...")
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
                    loge(it)
                }
            }
            // 加载更多
            when (it.append) {
                LoadState.Loading -> {
                    logi("分页加载中...")
                }

                is LoadState.NotLoading -> {

                }

                is LoadState.Error -> {
                    loge(it)
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
            viewModel.fetchBanner()
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
                        position2 = data.position2,
                    )
                )
            }
        }
        adapter.onBannerClick { position, position2, bannerItem ->
            launcher.launch(newIntent<ArticleWebActivity> {
                putExtra(
                    "data", WebData(
                        id = bannerItem.id,
                        url = bannerItem.url,
                        title = bannerItem.title,
                        like = bannerItem.collect,
                        position = position,
                        position2 = position2,
                    )
                )
            })
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
            viewModel.fetchBanner()
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
