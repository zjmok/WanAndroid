package com.example.wan.android.util

import android.os.Looper

@JvmField
val mainLooper: Looper = Looper.getMainLooper()
inline val currentLooper get() = Looper.myLooper()
inline val isMainLooper get() = Looper.myLooper() == Looper.getMainLooper()

@JvmField
val mainThread: Thread = Looper.getMainLooper().thread
val currentThread: Thread get() = Thread.currentThread()
inline val isMainThread get() = Thread.currentThread() == Looper.getMainLooper().thread // 通过判断线程
//inline val isMainThread get() = isMainLooper // 通过判断 Lopper
