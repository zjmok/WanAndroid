package org.example.wan.android.presentation.feature.square

import android.os.Bundle
import org.example.wan.android.R
import org.example.wan.android.presentation.feature.base.activity.VBaseActivity
import org.example.wan.android.databinding.ActivityArticleListBinding

class SquareActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {

//            supportActionBar?.title = "广场"
            titleView.text = "广场"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    SquareMixFragment.getInstance(false)
                )
                .commitNow()

        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}