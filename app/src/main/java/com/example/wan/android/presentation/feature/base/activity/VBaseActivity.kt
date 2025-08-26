package com.example.wan.android.presentation.feature.base.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding

abstract class VBaseActivity<VB : ViewBinding> : BaseActivity() {

    protected abstract val binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}