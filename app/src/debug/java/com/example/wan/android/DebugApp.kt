package com.example.wan.android

import android.content.Intent

class DebugApp : App() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            startService(Intent(this, FloatButtonService::class.java))
        }
    }

}
