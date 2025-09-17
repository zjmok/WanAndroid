package org.example.wan.android.presentation.feature.qa

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import org.example.wan.android.R
import org.example.wan.android.data.model.Comment
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlin.math.roundToInt

/**
 * @param articleId 评论内容很多 需要分页加载 在列表请求网络
 */
@SuppressLint("ViewConstructor")
class CommentPopup(context: Context, val articleId: Int, val list: List<Comment>) :
    BottomPopupView(context) {

    override fun getMaxHeight() = (XPopupUtils.getScreenHeight(context) * 0.7f).roundToInt()

    override fun getImplLayoutId() = R.layout.popup_comment

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()

        val tvCommentNum = bottomPopupContainer.findViewById<AppCompatTextView>(R.id.tv_comment_num)
        val rv = bottomPopupContainer.findViewById<RecyclerView>(R.id.rv)
        val sw = bottomPopupContainer.findViewById<SwitchCompat>(R.id.sw)

        tvCommentNum.text =
            "评论 ${list.size}"

        val adapter = CommentRvAdapter(list)
        rv.adapter = adapter

        sw.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // 按时间最新排序
                adapter.sortByTime()
            } else {
                // 按点赞大小排序
                adapter.sortByLike()
            }
        }
        sw.isChecked = false
        adapter.sortByLike()
    }

}