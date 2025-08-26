package com.example.wan.android.presentation.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wan.android.presentation.compose.ui.theme.WanAndroidTheme
import com.example.wan.android.presentation.feature.person.PersonViewModel
import com.example.wan.android.util.getViewModel

class AccountActivity : ComponentActivity() {

    val viewModel: PersonViewModel get() = getViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        viewModel
                    )
                }
            }
        }
        // 在 onCreate 执行 或 在 Composable 的 LaunchEffect 执行 都可以
        // LaunchEffect 在初次组合或键变化时执行一次
        viewModel.getUserInfo()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, viewModel: PersonViewModel) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
    AccountComponent(viewModel)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WanAndroidTheme {
        Greeting("Android", viewModel = PersonViewModel())
    }
}
