package com.example.wan.android.data.remote.api

class ApiException(
    var errorCode: Int,
    override var message: String
) : RuntimeException()