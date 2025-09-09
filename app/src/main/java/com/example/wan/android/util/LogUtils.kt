package com.example.wan.android.util

import android.annotation.SuppressLint
import android.util.Log
import com.blankj.utilcode.util.LogUtils

fun log(any: Any?, tag: String? = "log", trace: Int = 5) {
    printStackLine(trace) // 此处 trace 为 4，调用此方法处为 5
    loge(any = any, tag = tag)
}

@SuppressLint("LogUsage")
fun logStack(text: String?) {
//    Log.e("logStack", Log.getStackTraceString(Throwable(text)))
    Log.e("logStack", text, Throwable(text))
}

/**
 * 打印调用代码位置
 *
 * 使用时注意调试合适的 trace 参数
 *
 * - 如果直接调用 getStackInfo()，则 trace 应该设置为 3
 * - 每多一层封装，trace 就需要加 1
 */
fun getStackLine(trace: Int = 3): String {
    val stackTrace = Thread.currentThread().stackTrace // 此处 trace 为 2，调用此方法处为 3
    if (stackTrace.size >= trace + 1) {
        val element = stackTrace[trace]
        return "${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
    }
    return ""
}

/**
 * 打印调用代码位置
 *
 * 使用时注意调试合适的 trace 参数
 */
fun printStackLine(trace: Int = 4) {
    val stackInfo = getStackLine(trace) // 此处 trace 为 3，调用此方法处为 4
    loge(stackInfo)
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
