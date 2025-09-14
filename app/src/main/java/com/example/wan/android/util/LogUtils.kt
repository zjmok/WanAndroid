@file:Suppress("SameParameterValue", "unused", "SpellCheckingInspection")
@file:SuppressLint("LogUsage")

package com.example.wan.android.util

import android.annotation.SuppressLint
import android.util.Log
import com.example.wan.android.BuildConfig

val isEnableLog = BuildConfig.DEBUG
const val TAG = "log"
const val CHUNK_SIZE = 1024

//////////////////////////////////////////////////

fun log(any: Any?, tag: String? = TAG, trace: Int = 5) {
    if (isEnableLog.not()) return
    printStackLine(trace) // 此处 trace 为 4，调用此方法处为 5
    loge(any = any, tag = tag)
}

fun logLong(any: Any?, tag: String? = TAG, trace: Int = 5) {
    if (isEnableLog.not()) return
    printStackLine(trace) // 此处 trace 为 4，调用此方法处为 5
    loge(any = any, tag = tag)
}

//////////////////////////////////////////////////

fun logStack(text: String?) {
    if (isEnableLog.not()) return
//    Log.e("logStack", Log.getStackTraceString(Throwable(text)))
    Log.e("logStack", text, Throwable(text))
}

//////////////////////////////////////////////////

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
//        return "at ${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
//        return "${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
        return "${element.className.split(".").last()}.${element.methodName}(${element.fileName}:${element.lineNumber})"
    }
    return ""
}

/**
 * 打印调用代码位置
 *
 * 使用时注意调试合适的 trace 参数
 */
fun printStackLine(trace: Int = 4) {
    if (isEnableLog.not()) return
    val stackInfo = getStackLine(trace) // 此处 trace 为 3，调用此方法处为 4
    loge(stackInfo)
}

//////////////////////////////////////////////////

fun logv(any: Any?, tag: String? = TAG) {
    if (isEnableLog.not()) return
    Log.v(tag, any.toString())
}

fun logd(any: Any?, tag: String? = TAG) {
    if (isEnableLog.not()) return
    Log.d(tag, any.toString())
}

fun logi(any: Any?, tag: String? = TAG) {
    if (isEnableLog.not()) return
    Log.i(tag, any.toString())
}

fun logw(any: Any?, tag: String? = TAG) {
    if (isEnableLog.not()) return
    Log.w(tag, any.toString())
}

fun loge(any: Any?, tag: String? = TAG) {
    if (isEnableLog.not()) return
    if (any is Throwable) {
        Log.e(tag, any.message, any)
    } else {
        Log.e(tag, any.toString())
    }
}

//////////////////////////////////////////////////

fun logvLong(any: String?, tag: String? = TAG, prettyFormat: Boolean = true) {
    if (isEnableLog.not()) return
    val message = any.toString()
    val list = string2List(message, 6, prettyFormat)
    list.forEach {
        Log.v(tag, it)
    }
}

fun logdLong(any: String?, tag: String? = TAG, prettyFormat: Boolean = true) {
    if (isEnableLog.not()) return
    val message = any.toString()
    val list = string2List(message, 6, prettyFormat)
    list.forEach {
        Log.d(tag, it)
    }
}

fun logiLong(any: String?, tag: String? = TAG, prettyFormat: Boolean = true) {
    if (isEnableLog.not()) return
    val message = any.toString()
    val list = string2List(message, 6, prettyFormat)
    list.forEach {
        Log.i(tag, it)
    }
}

fun logwLong(any: String?, tag: String? = TAG, prettyFormat: Boolean = true) {
    if (isEnableLog.not()) return
    val message = any.toString()
    val list = string2List(message, 6, prettyFormat)
    list.forEach {
        Log.w(tag, it)
    }
}

fun logeLong(any: String?, tag: String? = TAG, prettyFormat: Boolean = true) {
    if (isEnableLog.not()) return
    val message = any.toString()
    val list = string2List(message, 6, prettyFormat)
    list.forEach {
        Log.e(tag, it)
    }
}

//////////////////////////////////////////////////

private fun string2List(message: String, trace: Int = 5, prettyFormat: Boolean = true): List<String> {
    return if (prettyFormat) {
        prettyFormat(message, trace)
    } else {
        message.chunked(CHUNK_SIZE).map {
            it.split("\n")
        }.flatten()
    }
}

/**
 * 格式化长日志
 */
private fun prettyFormat(message: String, trace: Int = 4): List<String> {
    val list = mutableListOf<String>()
    val topLine =
        "┌───────────────────────────────────────────────────────────────────────────────────────────────────"
    list.add(topLine)
    val stackInfo = getStackLine(trace)
    val stackLine =
        "│ $stackInfo" // 代码定位
    list.add(stackLine)
    val middleLine =
        "├───────────────────────────────────────────────────────────────────────────────────────────────────"
    list.add(middleLine)
    message.chunked(CHUNK_SIZE).map { // chunked 字符串变一维列表
        it.split("\n") // 兼容换行，一维列表变二维列表
    }.flatten().map { // flatMap 二维列表变回一维列表
        "│ $it"
    }.let { list.addAll(it) }
    val bottomLine =
        "└───────────────────────────────────────────────────────────────────────────────────────────────────"
    list.add(bottomLine)
    return list
}

//////////////////////////////////////////////////
