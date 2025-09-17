package org.example.wan.android.presentation.feature.common

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.LogUtils

class ActivityLifecycleObserver : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        LogUtils.e("onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        LogUtils.e("onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        LogUtils.e("onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        LogUtils.e("onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        LogUtils.e("onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtils.e("onDestroy")
    }

}