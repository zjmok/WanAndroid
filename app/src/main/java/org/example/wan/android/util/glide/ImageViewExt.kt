package org.example.wan.android.util.glide

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

@SuppressLint("GlideUsage")
fun ImageView.load(
    any: Any?,
    cornerRadius: Int = 0,
    @DrawableRes placeholderRes: Int = 0
) {
    // 升级为 ksp 后不再使用 GlideApp, 而是直接使用 Glide, 配置依然有效
    // https://bumptech.github.io/glide/doc/download-setup.html#kotlin---ksp
    // https://bumptech.github.io/glide/doc/generatedapi.html#this-page-and-the-generated-api-are-deprecated
    Glide.with(this)
        .load(any)
        .centerCrop()
        .transform(RoundedCornersTransformation(cornerRadius, 0))
        .placeholder(placeholderRes)
        .into(this)
}

@SuppressLint("GlideUsage")
fun ImageView.loadRes(
    @RawRes @DrawableRes res: Int,
    cornerRadius: Int = 0,
    @DrawableRes placeholderRes: Int = 0
) {
    Glide.with(this)
        .load(res)
        .centerCrop()
        .transform(RoundedCornersTransformation(cornerRadius, 0))
        .placeholder(placeholderRes)
        .into(this)
}

@SuppressLint("GlideUsage")
fun ImageView.loadCircle(res: Any?, @DrawableRes placeholderRes: Int = 0) {
    Glide.with(this)
        .load(res)
        .centerCrop()
        .circleCrop()
        .placeholder(placeholderRes)
        .into(this)

}
