package com.example.wan.android.presentation.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wan.android.presentation.compose.ui.theme.WanAndroidTheme

class XhsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                XhsComponent()
            }
        }
    }
}
