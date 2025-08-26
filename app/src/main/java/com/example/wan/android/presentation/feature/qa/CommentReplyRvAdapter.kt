package com.example.wan.android.presentation.feature.qa

import android.annotation.SuppressLint
import android.text.Html
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wan.android.R
import com.example.wan.android.data.model.ReplyComment
import com.example.wan.android.databinding.RvItemCommentReplyBinding
import com.example.wan.android.util.gone
import splitties.systemservices.layoutInflater
import splitties.views.imageResource

class CommentReplyRvAdapter(val floor: Int, val list: List<ReplyComment>) :
    RecyclerView.Adapter<CommentReplyRvViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentReplyRvViewHolder {
        return CommentReplyRvViewHolder(
            RvItemCommentReplyBinding.inflate(parent.context.layoutInflater)
        )
    }

    override fun getItemCount() = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentReplyRvViewHolder, position: Int) {
        val replyComment = list[position]

        holder.binding.tvIndex.text = "${floor}楼${list.size - position}座"
        holder.binding.tvName.text = "${replyComment.userName} 回复 ${replyComment.toUserName}"
        holder.binding.tvContent.text = Html.fromHtml(replyComment.content)
        holder.binding.tvTime.text = replyComment.niceDate

        if (replyComment.zan > 0) {
            holder.binding.ivGood.imageResource = R.drawable.icon_good_selected
        } else {
            holder.binding.ivGood.imageResource = R.drawable.icon_good
        }
        holder.binding.tvGoodNum.text = replyComment.zan.toString()

        holder.binding.rvReply.gone()
    }

}

class CommentReplyRvViewHolder(val binding: RvItemCommentReplyBinding) :
    RecyclerView.ViewHolder(binding.root)