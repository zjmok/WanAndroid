package com.zjmok.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun Uri.toFileCompat(): File? {
//    return UriUtils.uri2File(this)
    return uri2File(UtilLib.App, this)
}

fun File.toUriCompat(): Uri {
    createOrExistsFile(this)
//    return UriUtils.file2Uri(this)
    return file2Uri(UtilLib.App, this)
}

fun createOrExistsFile(file: File?): Boolean {
    if (file == null) return false
    if (file.exists()) return file.isFile
    if (!createOrExistsDir(file.parentFile)) return false
    try {
        return file.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
}

fun createOrExistsDir(file: File?): Boolean {
    return file?.let {
        if (file.exists()) file.isDirectory else file.mkdirs()
    } ?: false
}

/**
 * Android 上的 Uri 主要有两种 Scheme
 * - file: 表示一个直接的文件路径。在 Android 10 及以下，你可能通过 FileProvider 或直接路径获取到此类 Uri
 * - content: 表示通过 ContentProvider 提供的内容（如媒体库、文件选择器选取的文件等）。这是 Android 11 及以上版本中更常见的形式，通常无法直接获取其绝对路径
 * 适配的核心在于：对于 content 类型的 Uri，最可靠的方式是将它指向的文件内容复制到应用私有目录（沙盒目录）中，然后对这个副本文件进行操作
 */
fun uri2File(context: Context, uri: Uri): File? {
    return when (uri.scheme) {
        ContentResolver.SCHEME_FILE -> handleFileScheme(uri)
        ContentResolver.SCHEME_CONTENT -> handleContentScheme(context, uri)
        else -> null // 处理其它或未知的 scheme
    }
}

private fun handleFileScheme(uri: Uri): File? {
    // 直接通过路径创建 File 对象
    return uri.path?.let { File(it) }
}

private fun handleContentScheme(context: Context, uri: Uri): File? {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    try {
        val contentResolver = context.contentResolver
        // 生成一个唯一的文件名，避免覆盖。可以根据需要调整命名策略。
        val fileExtension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri)) ?: "dat"
        val timeStamp = System.currentTimeMillis()
        val randomSuffix = (Math.random() * 1000).toInt()
        val fileName = "temp_file_${timeStamp}_${randomSuffix}.${fileExtension}"

        // 创建目标文件（应用缓存目录 context.cacheDir context.externalFilesDir 等）
        val cacheDir = context.externalCacheDir
        val outputFile = File(cacheDir, fileName)

        // 打开输入流和输出流，进行复制
        inputStream = contentResolver.openInputStream(uri)
        outputStream = FileOutputStream(outputFile)

        if (inputStream != null) {
            val buffer = ByteArray(4 * 1024) // 4K buffer
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            return outputFile
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // 处理异常，例如文件未找到、IO异常等
    } finally {
        // 关闭流
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}

fun file2Uri(context: Context, file: File): Uri {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // 需要在清单文件里注册相应的 FileProvider
        val authority = "${context.packageName}.util.fileProvider"
        return FileProvider.getUriForFile(context, authority, file)
    } else {
        return Uri.fromFile(file)
    }
}
