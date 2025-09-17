package org.example.wan.android.data.remote.api

import androidx.annotation.Keep

@Keep
data class ApiResult<T>(
    val errorCode: Int,
    val errorMsg: String,
    private val data: T?
) {
    fun apiData(): T? {
        // 根据前后端约定具体规则
        if (errorCode == 0) {
            return data
        } else {
            throw ApiException(errorCode, errorMsg)
        }
    }
}
