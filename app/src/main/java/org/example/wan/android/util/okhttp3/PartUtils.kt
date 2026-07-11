package org.example.wan.android.util.okhttp3

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLEncoder

fun String.toRequestBodyWithType() = this.toRequestBody("text/plain".toMediaType())

/**
// @Part bodyPartList: List<MultipartBody.Part>
val partList = mutableListOf(
createPart("type", "android"),
createPart("version", version),
createPart("info", info),
createPart("downloadUrl", file, "application/vnd.android.package-archive".toMediaType()),
)
 */
fun createPart(key: String, value: String): MultipartBody.Part {
    // 使用两个参数的函数可能会乱码
    // filename设置null则在form表单中不会被识别成文件 否则不为null时被识别成文件
    return MultipartBody.Part.createFormData(key, null, value.toRequestBodyWithType())
}

fun createPart(key: String, file: File, mediaType: MediaType): MultipartBody.Part {
    return MultipartBody.Part.createFormData(key, URLEncoder.encode(file.name, "UTF-8"), file.asRequestBody(mediaType))
}

/**
// @Part bodyPartList: List<MultipartBody.Part>
val bodyPartList = mutableListOf<MultipartBody.Part>()
.put("type", "android")
.put("version", version)
.put("info", info)
.put("downloadUrl", file, "application/vnd.android.package-archive".toMediaType())
 */
fun MutableList<MultipartBody.Part>.put(key: String, value: String): MutableList<MultipartBody.Part> {
    return this.apply { this.add(createPart(key, value)) }
}

fun MutableList<MultipartBody.Part>.put(key: String, file: File, mediaType: MediaType): MutableList<MultipartBody.Part> {
    return this.apply { add(createPart(key, file, mediaType)) }
}

fun MutableList<MultipartBody.Part>.putAll(pairs: Array<out Pair<String, String>>): MutableList<MultipartBody.Part> {
    return apply {
        for ((key, value) in pairs) {
            this.put(key, value)
        }
    }
}

/**
 * 示例 `partListOf("key" to "value", "key2" to "value2",)`
 * 但是后面还要自己再加上 file 类型的 part
 *
 * 参考自 mutableMapOf
 */
public fun partListOf(vararg pairs: Pair<String, String>): MutableList<MultipartBody.Part> =
    mutableListOf<MultipartBody.Part>().apply { this.putAll(pairs) }
