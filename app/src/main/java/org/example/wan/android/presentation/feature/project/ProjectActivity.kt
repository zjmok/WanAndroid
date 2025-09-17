package org.example.wan.android.presentation.feature.project

import android.os.Bundle
import org.example.wan.android.R
import org.example.wan.android.presentation.feature.base.activity.VBaseActivity
import org.example.wan.android.databinding.ActivityArticleListBinding

class ProjectActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
//            supportActionBar?.title = "项目"
            titleView.text = "项目"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    ProjectFragment.getInstance(false),
                )
                .commitNow()
        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}