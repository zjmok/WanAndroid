package com.example.wan.android.util

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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
