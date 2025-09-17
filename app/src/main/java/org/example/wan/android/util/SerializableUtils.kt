package org.example.wan.android.util

import com.blankj.utilcode.util.ConvertUtils
import java.io.Serializable

fun Serializable.serializable2ByteArray(): ByteArray {
    return ConvertUtils.serializable2Bytes(this)
}

fun ByteArray.byteArray2Serializable(): Any {
    return ConvertUtils.bytes2Object(this)
}
