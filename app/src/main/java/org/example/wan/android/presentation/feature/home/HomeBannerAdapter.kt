package org.example.wan.android.presentation.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.example.wan.android.data.model.BannerItem
import org.example.wan.android.data.model.LikeData
import org.example.wan.android.databinding.ViewBannerBinding
import org.example.wan.android.util.glide.load
import com.youth.banner.adapter.BannerAdapter
import splitties.views.onClick

class HomeBannerAdapter(list: List<BannerItem>) :
    BannerAdapter<BannerItem, HomeBannerViewHolder>(list) {

    private var bannerClickListener: ((Int, Int, BannerItem) -> Unit)? = null

    fun onClick(listener: ((Int, Int, BannerItem) -> Unit)) {
        bannerClickListener = listener
    }

    fun notifyLikeChanged(likeData: LikeData) {
        val item = getData(likeData.position2!!)
        item.collect = likeData.like
        notifyItemChanged(likeData.position2)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        return HomeBannerViewHolder(
            ViewBannerBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindView(
        holder: HomeBannerViewHolder,
        data: BannerItem,
        position: Int,
        size: Int
    ) {
        holder.binding.run {
            // 注意，必须设置为match_parent，这个是viewpager2强制要求的
            root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            iv.load(data.imagePath)
            root.onClick {
                bannerClickListener?.invoke(
                    0, // banner 在外层列表 index == 0
                    position,
                    data,
                )
            }
        }
    }

}

class HomeBannerViewHolder(val binding: ViewBannerBinding) : RecyclerView.ViewHolder(binding.root)