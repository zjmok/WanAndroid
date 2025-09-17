package org.example.wan.android.composable

import android.annotation.SuppressLint
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.accompanist.drawablepainter.rememberDrawablePainter

/**
 * 不支持 adaptive icon 的 drawable
 */
@SuppressLint("ModifierParameter")
@Composable
fun ImageFromRes(
    @DrawableRes drawableRes: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        painter = painterResource(drawableRes),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
}

/**
 * BitmapPainter 方式 加载 adaptive icon 的 drawable
 */
@Deprecated("使用 ImageFromDrawableRes")
@SuppressLint("ModifierParameter")
@Composable
fun ImageFromDrawableByBitmapPainter(
    @DrawableRes drawableRes: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val painter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val adaptiveIconDrawable = ResourcesCompat.getDrawable(
            LocalContext.current.resources, drawableRes, LocalContext.current.theme
        ) as? AdaptiveIconDrawable
        if (adaptiveIconDrawable != null) {
            // mipmap-anydpi-v26/ic_launcher.xml 等 adaptive icon 资源
            BitmapPainter(adaptiveIconDrawable.toBitmap().asImageBitmap())
        } else {
            // 普通资源
            painterResource(drawableRes)
        }
    } else {
        painterResource(drawableRes)
    }
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
}

@SuppressLint("ModifierParameter")
@Composable
fun ImageFromDrawable(
    drawable: Drawable,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        painter = rememberDrawablePainter(drawable),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
}

/**
 * Accompanist 实验性 APIs, 兼容性好
 *
 * 支持 adaptive icon 的 drawable
 *
 * DrawablePainter 方式 加载 drawable
 */
@SuppressLint("ModifierParameter")
@Composable
fun ImageFromDrawableRes(
    @DrawableRes drawableRes: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val drawable = ResourcesCompat.getDrawable(
        LocalContext.current.resources, drawableRes, LocalContext.current.theme
    )
    Image(
        painter = rememberDrawablePainter(drawable),
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
}
