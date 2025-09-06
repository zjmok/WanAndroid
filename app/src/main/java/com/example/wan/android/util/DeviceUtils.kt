package com.example.wan.android.util

import android.annotation.SuppressLint
import android.os.Build
import com.hjq.device.compat.DeviceOs


object DeviceUtils {

    // 示例: Xiaomi
    val brand get(): String = Build.BRAND

    // 示例: M2007J1SC
    val model get(): String = Build.MODEL

    // 示例: 10
    val androidVersion get(): String = Build.VERSION.RELEASE

    // 示例: 29
    val androidApi get(): Int = Build.VERSION.SDK_INT

    // 示例: MIUI
    val skinType get(): String = DeviceOs.getOsName()

    // 示例: 12.0.15.0
    val skinVersion get(): String = DeviceOs.getOsVersionName()

    // 示例: V12.0.15.0.QJJCNXM
    val skinName get(): String = DeviceOs.getOriginalOsVersionName()

    val marketName get(): String = "TODO"

    // 获取指定系统属性
    @SuppressLint("PrivateApi")
    fun getSystemProperty(key: String, defaultValue: String = ""): String {
        try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val getMethod = systemProperties.getMethod("get", String::class.java, String::class.java)
            return getMethod.invoke(null, key, defaultValue) as String
        } catch (e: Exception) {
//            e.printStackTrace()
            return defaultValue
        }
    }

}
