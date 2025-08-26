package com.example.wan.android.presentation.feature.common

import android.view.View
import com.example.wan.android.R
import com.example.wan.android.data.model.Tag
import com.example.wan.android.databinding.ViewTagBinding
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import splitties.systemservices.layoutInflater

class ArticleTagAdapter(listTag: List<Tag>) : TagAdapter<Tag>(listTag) {

    override fun getView(parent: FlowLayout, position: Int, t: Tag): View {
        return ViewTagBinding.inflate(parent.layoutInflater, parent, false).root.apply {
            text = t.name
            val bgRes = when (t.name) {
                "本站发布" -> {
                    R.drawable.selector_tag_bg_green
                }

                "问答" -> {
                    R.drawable.selector_tag_bg_red
                }

                "项目" -> {
                    R.drawable.selector_tag_bg_blue
                }

                "公众号" -> {
                    R.drawable.selector_tag_bg_yellow
                }

                else -> {
                    R.drawable.selector_tag_bg_green
                }
            }
            setBackgroundResource(bgRes)
        }
    }

}
