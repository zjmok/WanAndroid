package org.example.wan.android.data.model

import android.net.Uri

data class ImageInfo(
    val crop: Boolean,
    val uri: Uri,
    val path: String,
)
