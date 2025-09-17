package org.example.wan.android.presentation.feature.project

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.paging.LoadState
import androidx.paging.liveData
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.LogUtils
import org.example.wan.android.presentation.feature.base.fragment.VVMBaseFragment
import org.example.wan.android.data.model.LikeData
import org.example.wan.android.data.model.WebData
import org.example.wan.android.databinding.FragmentArticleListBinding
import org.example.wan.android.presentation.feature.common.ArticleListPagingAdapter
import org.example.wan.android.presentation.feature.common.ArticleWebActivity
import org.example.wan.android.util.gone
import org.example.wan.android.util.visible
import org.example.wan.android.util.getViewModel
import org.example.wan.android.util.newIntent
import org.example.wan.android.util.registerResultOK

class NewProjectFragment : VVMBaseFragment<NewProjectViewModel, FragmentArticleListBinding>() {

    override val viewModel: NewProjectViewModel by lazy { getViewModel() }
    override val binding: FragmentArticleListBinding by viewBinding(CreateMethod.INFLATE)

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

    private fun observe() {
        viewModel.getArticlesPager().liveData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
            binding.refresh.isRefreshing = false
        }
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
        }
    }

}