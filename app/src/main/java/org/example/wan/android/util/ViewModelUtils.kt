package org.example.wan.android.util

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// 获取 ViewModel 方式
// val viewModel: XXXViewModel by viewModels() // 作用域 当前 Activity or Fragment
// val viewModel: XXXViewModel by viewModels({ requireParentFragment() }) // Fragment 中使用，作用域 父 Fragment
// val viewModel: XXXViewModel by activityViewModels() // Fragment 中使用，作用域 Fragment 的宿主 Activity
// ViewModelProvider(this)[XXXViewModel::class.java] // 作用域 自定义，可以灵活控制作用域
// Hilt 等 注入

// val viewModel: XXXViewModel = viewModel()

/**
 * owner 是 ComponentActivity
 */
inline fun <reified T : ViewModel> ComponentActivity.getViewModel() =
    ViewModelProvider(this)[T::class.java]

/**
 * owner 是 Fragment
 */
inline fun <reified T : ViewModel> Fragment.getViewModel() =
    ViewModelProvider(this)[T::class.java]

/**
 * owner 是 ComponentActivity
 */
inline fun <reified T : ViewModel> Fragment.getViewModelOfActivity() =
    ViewModelProvider(requireActivity())[T::class.java]

/**
 * owner 是 Application
 */
inline fun <reified T : ViewModel> Application.getViewModel() =
    ViewModelProvider.AndroidViewModelFactory(this).create(T::class.java)
