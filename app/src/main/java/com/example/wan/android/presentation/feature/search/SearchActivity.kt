package com.example.wan.android.presentation.feature.search

import android.os.Bundle
import com.example.wan.android.R
import com.example.wan.android.presentation.feature.base.activity.VBaseActivity
import com.example.wan.android.databinding.ActivityArticleListBinding
import com.example.wan.android.presentation.feature.search.fragment.SearchFragment

class SearchActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {

//            supportActionBar?.title = "搜索"
            titleView.text = "搜索"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    SearchFragment()
                )
                .commitNow()

        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}