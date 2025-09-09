package com.example.wan.android.presentation.feature.home.paging

import android.annotation.SuppressLint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.wan.android.R
import com.example.wan.android.data.model.BannerItem
import com.example.wan.android.data.model.DataX
import com.example.wan.android.data.model.LikeData
import com.example.wan.android.databinding.RvItemArticleBinding
import com.example.wan.android.databinding.RvItemBannerBinding
import com.example.wan.android.presentation.feature.common.ArticleTagAdapter
import com.example.wan.android.presentation.feature.home.HomeBannerAdapter
import com.example.wan.android.presentation.feature.home.HomeFragment
import com.example.wan.android.util.gone
import com.example.wan.android.util.load
import com.example.wan.android.util.loadRes
import com.example.wan.android.util.visible
import com.lxj.xpopup.XPopup
import com.youth.banner.indicator.CircleIndicator
import splitties.views.onClick

class HomePagingAdapter(val fragment: HomeFragment, private var bannerData: List<BannerItem>) :
    PagingDataAdapter<DataX, ViewHolder>(object : DiffUtil.ItemCallback<DataX>() {
        override fun areItemsTheSame(oldItem: DataX, newItem: DataX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataX, newItem: DataX): Boolean {
            return oldItem == newItem
        }
    }) {

    private val homeBannerAdapter = HomeBannerAdapter(bannerData)

    fun setBanner(banner: List<BannerItem>) {
        bannerData = banner
        homeBannerAdapter.setDatas(banner)
    }

    private var bannerClickListener: ((Int, Int, BannerItem) -> Unit)? = null

    fun onBannerClick(listener: (Int, Int, BannerItem) -> Unit) {
        bannerClickListener = listener
    }

    private var likeClickListener: ((LikeData) -> Unit)? = null

    fun onLikeClick(listener: (LikeData) -> Unit) {
        likeClickListener = listener
    }

    fun notifyLikeChanged(likeData: LikeData) {
        if (likeData.position == 0) { // index == 0 是 banner
            val item = bannerData[likeData.position2!!]
            item.collect = likeData.like
            homeBannerAdapter.notifyLikeChanged(likeData)
        } else {
            val item = getItem(likeData.position - 1)!! // index == 0 是 banner
            item.collect = likeData.like
            notifyItemChanged(likeData.position)
        }
    }

    private var itemClickListener: ((Int, DataX) -> Unit)? = null

    fun onItemClick(listener: (Int, DataX) -> Unit) {
        itemClickListener = listener
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0) {

            // banner
            homeBannerAdapter.onClick { i1, i2, bannerItem ->
                bannerClickListener?.invoke(i1, i2, bannerItem)
            }

        } else {

            val item = getItem(position - 1) // index == 0 是 banner

            // list
            holder as HomePagingViewHolder
            holder.binding.run {
                item?.let {

                    tvTime.text = item.niceDate

                    tvAuthor.text = if (item.superChapterName == "广场Tab") {
                        "分享人: ${item.shareUser}"
                    } else {
                        "作者: ${item.author}"
                    }

                    tvTitle.text = Html.fromHtml(item.title)

                    tvSubtitle.text = Html.fromHtml(item.desc)
                    tvSubtitle.visible(item.desc.isNotBlank())

                    tvClassification.text = listOf(
                        item.superChapterName,
                        item.chapterNameDecoded,
                    ).filter {
                        // 解析为 null 了
                        @Suppress("UselessCallOnNotNull")
                        it.isNullOrBlank().not()
                    }.joinToString("/")

                    tvPosition.text = "[ $position ]"

                    ivImg.load(item.envelopePic)
                    ivImg.visible(item.envelopePic.isNotBlank())

                    root.setOnClickListener {
                        itemClickListener?.invoke(position, item)
                    }

                    ivTop.visible(item.type == 1)
                    ivNew.visible(item.fresh)

                    if (item.tags.isNullOrEmpty().not()) {
                        tag.adapter = ArticleTagAdapter(item.tags!!)
                        tag.visible()
                    } else {
                        tag.gone()
                    }

                    // 收藏
                    if (item.collect) {
                        ivStar.loadRes(R.drawable.icon_like_selected)
                    } else {
                        ivStar.loadRes(R.drawable.icon_like)
                    }
                    ivStar.onClick {
                        if (item.collect) {
                            XPopup.Builder(holder.binding.root.context)
                                .asConfirm("移除收藏", "《${Html.fromHtml(item.title)}》") {
                                    likeClickListener?.invoke(
                                        LikeData(
                                            id = item.id,
                                            originId = item.originId,
                                            like = false,
                                            position = position,
                                        )
                                    )
                                }.show()
                        } else {
                            likeClickListener?.invoke(
                                LikeData(
                                    id = item.id,
                                    originId = item.originId,
                                    like = true,
                                    position = position,
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return super.getItemCount() // + 1 ?
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == 0) {
            HomeBannerViewHolder(
                RvItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    .apply {
                        root.addBannerLifecycleObserver(fragment.viewLifecycleOwner)
                            .setAdapter(homeBannerAdapter)
                            .indicator = CircleIndicator(fragment.requireContext())
                    }
            )
        } else {
            HomePagingViewHolder(
                RvItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

}

class HomeBannerViewHolder(val binding: RvItemBannerBinding) : ViewHolder(binding.root)
class HomePagingViewHolder(val binding: RvItemArticleBinding) : ViewHolder(binding.root)
