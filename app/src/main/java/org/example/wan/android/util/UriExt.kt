package org.example.wan.android.util

import android.net.Uri
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.UriUtils
import java.io.File

fun Uri.getFile(): File? {
    return UriUtils.uri2File(this)
}

fun File.getUri(): Uri {
    FileUtils.createOrExistsFile(this)
    return UriUtils.file2Uri(this)
}
