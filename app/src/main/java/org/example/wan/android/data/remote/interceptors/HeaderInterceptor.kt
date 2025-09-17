package org.example.wan.android.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder().apply {
/*
        // 全局 Header
        header("Authorization", UserUtils.getToken())
        val map = mapOf(
            "deviceModel" to "${DeviceUtils.getManufacturer()}_${DeviceUtils.getModel()}", // 手机型号
            "systemVersion" to "Android_${DeviceUtils.getSDKVersionName()}", // 系统版本
            "uniqueSign" to DeviceUtils.getUniqueDeviceId(), // 设备唯一标记
        )
        header("User-Agent", GsonUtils.toJson(map))
*/
        }.build()
        return chain.proceed(request)
    }
}
