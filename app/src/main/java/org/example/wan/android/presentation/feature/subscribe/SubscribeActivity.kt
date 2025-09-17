package org.example.wan.android.presentation.feature.subscribe

import android.os.Bundle
import org.example.wan.android.R
import org.example.wan.android.presentation.feature.base.activity.VBaseActivity
import org.example.wan.android.databinding.ActivityArticleListBinding

class SubscribeActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
//            supportActionBar?.title = "公众号"
            titleView.text = "公众号"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    SubscribeFragment.getInstance(false),
                )
                .commitNow()
        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}