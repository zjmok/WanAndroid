package org.example.wan.android.util.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToInt

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
