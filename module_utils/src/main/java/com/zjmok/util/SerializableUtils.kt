package com.zjmok.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 实现了 Serializable 的对象 转换为 ByteArray
 */
fun Serializable.toByteArray(): ByteArray? {
//    return ConvertUtils.serializable2Bytes(this)
    try {
        ByteArrayOutputStream().use { baos ->
            ObjectOutputStream(baos).use { oos ->
                oos.writeObject(this)
                return baos.toByteArray()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

/**
 * ByteArray 转换为 实现了 Serializable 的对象
 */
fun <T : Serializable> ByteArray.toObject(): T? {
//    @Suppress("UNCHECKED_CAST")
//    return ConvertUtils.bytes2Object(this) as? T
    try {
        ByteArrayInputStream(this).use { bais ->
            ObjectInputStream(bais).use { ois ->
                @Suppress("UNCHECKED_CAST")
                return ois.readObject() as? T
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
