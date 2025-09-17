package org.example.wan.android.config

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.request.CachePolicy
import org.example.wan.android.constant.AppConst
import java.io.File

// https://coil-kt.github.io/coil/image_loaders/
object CoilConfig {

    fun getImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                //可选，需要展示 Gif 则引入
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                //可选，需要展示 Video 则引入
                add(VideoFrameDecoder.Factory())
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache(
                DiskCache.Builder()
                    .maxSizeBytes(AppConst.IMAGE_CACHE_SIZE)
                    .directory(File(AppConst.coilPath))
                    .build()
            )
            .build()
    }

}