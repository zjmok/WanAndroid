package com.example.wan.android.presentation.feature.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class VBaseFragment<VB : ViewBinding> : BaseFragment() {

    // `by viewBinding()` 委托可在 `onDestroyView` 里执行 `binding = null`
    protected abstract val binding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}