package org.example.wan.android.presentation.feature.qa

import android.os.Bundle
import org.example.wan.android.R
import org.example.wan.android.presentation.feature.base.activity.VBaseActivity
import org.example.wan.android.databinding.ActivityArticleListBinding
import org.example.wan.android.presentation.feature.qa.fragment.QaFragment

class QaActivity : VBaseActivity<ActivityArticleListBinding>() {

    override val binding by lazy { ActivityArticleListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {

//            supportActionBar?.title = "问答"
            titleView.text = "问答"
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.root.id,
                    QaFragment()
                )
                .commitNow()

        }
    }

    override fun initStatusBarColor() = R.color.status_bar

}