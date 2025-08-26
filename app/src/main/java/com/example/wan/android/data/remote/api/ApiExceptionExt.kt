package com.example.wan.android.data.remote.api

fun Throwable.isApiException() = this is ApiException

fun Throwable.isNoLogin() = this is ApiException && this.errorCode == -1001
