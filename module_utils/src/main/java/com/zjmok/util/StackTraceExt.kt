package com.zjmok.util

import android.content.Context
import java.lang.reflect.Modifier

fun Array<StackTraceElement>?.getAppStackTrace(context: Context): Array<StackTraceElement>? {
    if (this == null) {
        return null
    }
    val filter = this.filter {
        val clazz = it.javaClass
        val className = it.className
        className.startsWith(context.packageName) // 指定包名
                && clazz.isInterface.not() // 不是接口
                && Modifier.isAbstract(clazz.modifiers) // 不是抽象类
    }.toTypedArray()
    return filter
}
