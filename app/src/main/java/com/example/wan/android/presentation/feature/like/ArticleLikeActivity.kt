package com.example.wan.android.presentation.feature.like

import android.os.Bundle
import com.example.wan.android.R
import com.example.wan.android.presentation.feature.base.activity.VBaseActivity
import com.example.wan.android.databinding.ActivityArticleListBinding

class ArticleLikeActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {

//            supportActionBar?.title = "收藏博文"
            titleView.text = "收藏博文"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    ArticleLikeFragment()
                )
                .commitNow()

        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}