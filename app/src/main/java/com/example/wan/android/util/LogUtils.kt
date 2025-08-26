package com.example.wan.android.util

import android.annotation.SuppressLint
import android.util.Log
import com.blankj.utilcode.util.LogUtils

fun log(any: Any?, tag: String? = "log") {
    loge(any = any, tag = tag)
}

@SuppressLint("LogUsage")
fun logStack(text: String?) {
//    Log.e("logStack", Log.getStackTraceString(Throwable(text)))
    Log.e("logStack", text, Throwable(text))
}

@SuppressLint("LogUsage")
fun logv(any: Any?, tag: String? = "log") {
    Log.v(tag, any.toString())
}

@SuppressLint("LogUsage")
fun logd(any: Any?, tag: String? = "log") {
    Log.d(tag, any.toString())
}

@SuppressLint("LogUsage")
fun logi(any: Any?, tag: String? = "log") {
    Log.i(tag, any.toString())
}

@SuppressLint("LogUsage")
fun logw(any: Any?, tag: String? = "log") {
    Log.w(tag, any.toString())
}

@SuppressLint("LogUsage")
fun loge(any: Any?, tag: String? = "log") {
    Log.e(tag, any.toString())
    if (any is Throwable) {
        LogUtils.eTag(tag, any)
//        any.printStackTrace()
    }
}
