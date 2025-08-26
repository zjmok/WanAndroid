package com.example.wan.android.data.remote

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.TimeUnit

fun LifecycleCoroutineScope.download(
    url: String,
    saveFile: File,
    onStart: ((call: okhttp3.Call) -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onFailure: ((error: Throwable) -> Unit)? = null,
    onSuccess: ((totalLen: Long) -> Unit)? = null,
    onProgress: ((currentLen: Long, totalLen: Long) -> Unit)? = null,
) {

    val scope = this

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    val handler = CoroutineExceptionHandler { _, throwable ->
        onFailure?.invoke(throwable)
    }
    scope.launch(Dispatchers.IO + handler) {

        okHttpClient.newCall(request).let { call ->
            onStart?.invoke(call)
            call.enqueue(object : okhttp3.Callback {

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    scope.launch(Dispatchers.Main + handler) {
                        if (response.isSuccessful) {
                            withContext(Dispatchers.IO) {
                                try {
                                    val body = response.body!!
                                    val totalLen = body.contentLength()
                                    var writeLen = 0L
                                    val inputStream = body.byteStream()
                                    val outputStream = FileOutputStream(saveFile)

                                    var length: Int
                                    val data = ByteArray(65536)
                                    while ((inputStream.read(data).also { length = it }) != -1) {
                                        outputStream.write(data, 0, length)
                                        writeLen += length

                                        withContext(Dispatchers.Main) {
                                            onProgress?.invoke(writeLen, totalLen)
                                            if (writeLen == totalLen) {
                                                onSuccess?.invoke(totalLen)
                                            }
                                        }

                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        if (e is SocketException && e.message == "Socket closed") {
                                            onCancel?.invoke()
                                        } else {
                                            onFailure?.invoke(e)
                                        }
                                    }
                                }
                            }
                        } else {
                            onFailure?.invoke(RuntimeException("response.isSuccessful = false"))
                        }
                    }
                }

                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    scope.launch(Dispatchers.Main + handler) {
                        onFailure?.invoke(e)
                    }
                }

            })
        }
    }

}
