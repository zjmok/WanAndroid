package com.example.wan.android.util

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToInt

inline val Float.dp2px: Float get() = this * Resources.getSystem().displayMetrics.density
inline val Int.dp2px: Float get() = this.toFloat().dp2px
inline val Float.dp2pxInt: Int get() = this.dp2px.roundToInt()
inline val Int.dp2pxInt: Int get() = this.toFloat().dp2px.roundToInt()
inline val Int.px2dp: Float get() = this / Resources.getSystem().displayMetrics.density

inline val Float.sp2px: Float get() = this * Resources.getSystem().displayMetrics.scaledDensity
inline val Int.sp2px: Float get() = this.toFloat().sp2px
inline val Float.sp2pxInt: Int get() = this.sp2px.roundToInt()
inline val Int.sp2pxInt: Int get() = this.toFloat().sp2px.roundToInt()
inline val Int.px2sp: Float get() = this / Resources.getSystem().displayMetrics.scaledDensity

// compose px2dp: Dp
@Composable
fun Float.px2dp() = with(LocalDensity.current) { this@px2dp.toDp() }

// compose px2dp: Dp
@Composable
fun Int.px2dp() = with(LocalDensity.current) { this@px2dp.toDp() }

// compose dp2px: Float
@Composable
fun Dp.dp2px() = with(LocalDensity.current) { this@dp2px.toPx() }

// compose dp2px: Int
@Composable
fun Dp.dp2pxInt() = with(LocalDensity.current) { this@dp2pxInt.toPx().roundToInt() }

// compose px2sp: TextUnit
@Composable
fun Float.px2sp() = with(LocalDensity.current) { this@px2sp.toSp() }

// compose px2sp: TextUnit
@Composable
fun Int.px2sp() = with(LocalDensity.current) { this@px2sp.toSp() }

// compose sp2px: Float
@Composable
fun TextUnit.sp2px() = with(LocalDensity.current) { this@sp2px.toPx() }

// compose sp2px: Int
@Composable
fun TextUnit.sp2pxInt() = with(LocalDensity.current) { this@sp2pxInt.toPx().roundToInt() }
