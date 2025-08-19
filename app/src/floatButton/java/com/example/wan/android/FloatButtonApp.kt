package com.example.wan.android

import android.content.Intent

class FloatButtonApp : App() {

    override fun onCreate() {
        super.onCreate()
        startService(Intent(this, FloatButtonService::class.java))
    }

}
