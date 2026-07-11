package com.zjmok.util

import java.text.SimpleDateFormat
import java.util.Locale

val dateFormat get() = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

fun formatSecondsHMS(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
}

fun formatSecondsToDHMS(seconds: Int): String {
    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%d天%02d小时%02d分钟%02d秒", days, hours, minutes, secs)
}
