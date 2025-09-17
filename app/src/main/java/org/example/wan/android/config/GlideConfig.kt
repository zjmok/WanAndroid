package org.example.wan.android.config

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import org.example.wan.android.constant.AppConst

// https://muyangmin.github.io/glide-docs-cn/doc/configuration.html
@GlideModule
class GlideConfig : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {

        // 设置图片的显示格式ARGB_8888(指图片大小为32bit)
        builder.setDefaultRequestOptions(RequestOptions().run {
            format(DecodeFormat.PREFER_ARGB_8888)
        })

        builder.setDiskCache(DiskLruCacheFactory(AppConst.glidePath, AppConst.IMAGE_CACHE_SIZE))

        // 日志级别
        builder.setLogLevel(Log.DEBUG)

    }

    override fun isManifestParsingEnabled() = false

}