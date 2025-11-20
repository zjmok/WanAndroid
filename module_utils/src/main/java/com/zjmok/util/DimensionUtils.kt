package com.zjmok.util

import android.content.res.Resources
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
