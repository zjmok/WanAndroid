package org.example.wan.android.presentation.feature.home

import android.os.Bundle
import org.example.wan.android.R
import org.example.wan.android.presentation.feature.base.activity.VBaseActivity
import org.example.wan.android.databinding.ActivityArticleListBinding

class HomeActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {

//            supportActionBar?.title = "推荐"
            titleView.text = "推荐"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    HomeFragment.getInstance(false)
                )
                .commitNow()

        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}