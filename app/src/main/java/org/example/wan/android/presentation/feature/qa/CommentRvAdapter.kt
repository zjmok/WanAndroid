package org.example.wan.android.presentation.feature.qa

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import org.example.wan.android.R
import org.example.wan.android.data.model.Comment
import org.example.wan.android.databinding.RvItemCommentBinding
import com.zjmok.util.fromHtmlLegacy
import com.zjmok.util.visible
import splitties.systemservices.layoutInflater
import splitties.views.imageResource

class CommentRvAdapter(var list: List<Comment>) : RecyclerView.Adapter<CommentRvViewHolder>() {

    private var byLikeOrTime = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentRvViewHolder {
        return CommentRvViewHolder(
            RvItemCommentBinding.inflate(parent.context.layoutInflater)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortByLike() {
        byLikeOrTime = true
        list = list.sortedWith(
//            compareBy(
            // 没有 `public fun <T> compareByDescending(vararg selectors: (T) -> Comparable<*>?): Comparator<T>`
            com.zjmok.util.compareByDescending(
                { it.zan },
                { it.publishDate },
            )
        )
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortByTime() {
        byLikeOrTime = false
        list = list.sortedWith(
//            compareBy(
            com.zjmok.util.compareByDescending(
                { it.publishDate },
                { it.zan },
            )
        )
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentRvViewHolder, position: Int) {
        val comment = list[position]

        val floor = list.size - position

        holder.binding.tvIndex.text = "${floor}楼"
        holder.binding.tvName.text = comment.userName
        holder.binding.tvContent.text = fromHtmlLegacy(comment.contentMd)
        holder.binding.tvTime.text = comment.niceDate

        if (comment.zan > 0) {
            holder.binding.ivGood.imageResource = R.drawable.icon_good_selected
            holder.binding.tvGoodNum.setTextColor(ColorUtils.getColor(R.color.icon_color_fore))
        } else {
            holder.binding.ivGood.imageResource = R.drawable.icon_good
            holder.binding.tvGoodNum.setTextColor(ColorUtils.getColor(R.color.secondaryText))
        }
        holder.binding.tvGoodNum.text = comment.zan.toString()

        holder.binding.rvReply.visible()

        holder.binding.rvReply.adapter = CommentReplyRvAdapter(
            floor, if (byLikeOrTime) {
                comment.replyComments.sortedWith(
//                    compareBy(
                    com.zjmok.util.compareByDescending(
                        { it.publishDate },
                        { it.zan },
                    )
                )
            } else {
                comment.replyComments.sortedWith(
//                    compareBy(
                    com.zjmok.util.compareByDescending(
                        { it.publishDate },
                        { it.zan },
                    )
                )
            }
        )

    }

}

class CommentRvViewHolder(val binding: RvItemCommentBinding) : RecyclerView.ViewHolder(binding.root)