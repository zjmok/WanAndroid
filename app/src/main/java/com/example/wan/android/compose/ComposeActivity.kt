package com.example.wan.android.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ActivityUtils
import com.example.wan.android.R
import com.example.wan.android.compose.ui.theme.WanAndroidTheme
import com.example.wan.android.index.person.BookmarkActivity
import com.example.wan.android.index.person.HistoryActivity
import com.example.wan.android.index.search.SearchActivity
import com.example.wan.android.index.setting.ManageSpaceActivity
import com.example.wan.android.ui.dialog.AppDetailDialog
import com.example.wan.android.utils.px2dp
import com.example.wan.android.utils.startBrowser
import com.example.wan.android.utils.startUrl
import com.example.wan.android.utils.toast

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                ComposeComponent()
            }
        }
    }
}

@Composable
private fun ComposeComponent() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        PageList(
            name = "Android",
            modifier = Modifier
                .background(colorResource(id = R.color.wx_background))
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageList(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        stickyHeader {
            Text(
                text = "Debug 页面",
                fontSize = 18.sp,
                color = colorResource(id = R.color.icon_color_fore),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.wx_background))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.size(1.px2dp()))
        }
        item {
            // Test
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        // 测试
                        toast("测试")
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "测试",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText)
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // SearchActivity
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        ActivityUtils.startActivity(SearchActivity::class.java)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "SearchActivity",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText)
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // DetailActivity
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        AppDetailDialog(context = context).show()
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "AppDetailDialog",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText)
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // AccountActivity
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        ActivityUtils.startActivity(AccountActivity::class.java)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "AccountActivity 我的",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText)
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // XhsActivity
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        ActivityUtils.startActivity(XhsActivity::class.java)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "XhsActivity 小红书",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText),
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // ManageSpaceActivity
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        ActivityUtils.startActivity(ManageSpaceActivity::class.java)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "ManageSpaceActivity 管理空间",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText),
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // HistoryActivity
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        ActivityUtils.startActivity(HistoryActivity::class.java)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "HistoryActivity 浏览历史",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText),
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // BookmarkActivity
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        ActivityUtils.startActivity(BookmarkActivity::class.java)
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "BookmarkActivity 本地书签",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText),
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // 系统浏览器列表
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        context.startUrl("https://www.wanandroid.com")
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "从 系统浏览器列表 打开",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText),
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
            // 自定义浏览器列表
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.wx_foreground))
                    .clickable {
                        context.startBrowser("https://www.wanandroid.com")
                    }
                    .padding(16.dp)
            ) {
                Text(
                    text = "从 自定义浏览器列表 打开",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.primaryText),
                )
            }
            Spacer(modifier = Modifier.size(1.px2dp()))
        }
        stickyHeader {
            Text(
                text = "测试在列表中间的粘性项",
                fontSize = 18.sp,
                color = colorResource(id = R.color.icon_color_fore),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.wx_foreground_pressed))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.size(1.px2dp()))
        }
        items(100) { index ->
            Text(
                text = "Activity$index",
                fontSize = 18.sp,
                color = colorResource(id = R.color.secondaryText),
                textAlign = TextAlign.Center, // Text 的文字在组合项内居中
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.wx_background))
//                    .align(alignment = Alignment.CenterHorizontally) // Text 组合项居中
                    .clickable {
                        toast("开发中")
                    }
                    .padding(16.dp)
            )
            Spacer(
                modifier = Modifier
                    .height(1.px2dp()) // 正确设置 1px
                    .fillMaxWidth()
                    .background(color = colorResource(R.color.wx_foreground))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComposeActivityPreview() {
    WanAndroidTheme {
        ComposeComponent()
    }
}
