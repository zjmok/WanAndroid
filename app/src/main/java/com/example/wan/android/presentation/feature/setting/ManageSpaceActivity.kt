package com.example.wan.android.presentation.feature.setting

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.bumptech.glide.Glide
import com.example.wan.android.data.remote.RetrofitClient
import com.example.wan.android.util.alert
import com.example.wan.android.util.cancel
import com.example.wan.android.util.ok
import com.example.wan.android.util.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 自定义 管理空间 页面
// 需要在清单 application 设置 `android:manageSpaceActivity`
class ManageSpaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManageSpaceScreen()
        }
    }
}

@Composable
fun ManageSpaceScreen() {

    var cacheSize by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        updateCacheSize { cacheSize = it }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(text = "管理空间", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // 清除缓存逻辑
            showClearCacheDialog(context) {
                clearCache(context, coroutineScope) {
                    cacheSize = it
                }
            }
        }) {
            Text("清除缓存 ($cacheSize)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            toast("假装在清除数据")
        }) {
            Text("清除数据")
        }
    }
}

fun updateCacheSize(onSizeUpdated: (String) -> Unit) {
    val cachePath = PathUtils.getExternalAppCachePath()
    val len = FileUtils.getLength(cachePath)
    val size = ConvertUtils.byte2FitMemorySize(len, 2)
    onSizeUpdated(size)
}

fun clearCache(context: Context, coroutineScope: CoroutineScope, onCacheCleared: (String) -> Unit) {
    val cachePath = PathUtils.getExternalAppCachePath()
    coroutineScope.launch {
        withContext(Dispatchers.IO) {
            Glide.get(context).clearDiskCache() // glide api 清除缓存
            RetrofitClient.clearCache() // okhttp api 清除缓存
            FileUtils.deleteAllInDir(cachePath) // 删除 APP 私有目录 的 cache 目录
        }
        delay(200) // 假装正在努力清除缓存, 清理得太快就连 loading 都看不到
        toast("清理完成")
        updateCacheSize(onCacheCleared)
    }
}

fun showClearCacheDialog(context: Context, onConfirm: () -> Unit) {
    context.alert("提示", "将会清理图片、网络请求等缓存，是否继续？") {
        cancel {}
        ok {
            onConfirm()
        }
    }.show()
}
