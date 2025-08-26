package com.example.wan.android.presentation.feature.person

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.wan.android.App
import com.example.wan.android.R
import com.example.wan.android.presentation.compose.ui.theme.WanAndroidTheme
import com.example.wan.android.data.model.WebPage
import com.example.wan.android.presentation.feature.web.WebActivity
import com.example.wan.android.util.logd
import com.example.wan.android.util.px2dp
import kotlinx.coroutines.launch

class BookmarkActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WanAndroidTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    /*
                    topBar = {
                        // 获取 Compose 主题的颜色
                        val composeColor = MaterialTheme.colorScheme.primary
                        //
                        val viewsColor =
                            TopAppBar(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(80.dp),
                                title = {
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        Text(
                                            text = stringResource(R.string.app_name),
                                            maxLines = 1,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center,
//                                            color = Color.White, // Text 的颜色 会覆盖 titleContentColor
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    // 获取 Views 主题的颜色: Color(ContextCompat.getColor(context, R.color.primary))
                                    containerColor = Color(ContextCompat.getColor(context, R.color.primary)), // 设置背景颜色
                                    titleContentColor = Color.White, // 设置标题颜色
                                ),
                            )
                    },
                    */
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(R.color.icon_color_fore))
//                            .background(colorResource(R.color.background))
                            // 在background后再padding，可以填充状态栏颜色
                            .padding(innerPadding)
//                            .padding(Modifier.systemBarsPadding()) // Modifier.systemBarsPadding() 与 Scaffold 的 innerPadding 效果一样
                    ) {
                        BookmarkPage()
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarkPage() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<WebPage?>(null) }
    val bookmarkList = remember { mutableStateListOf<WebPage>() }
    val repository =
        com.example.wan.android.presentation.feature.web.WebPageRepository(dataStore = (context.applicationContext as App).dataStore)
    LaunchedEffect(Unit) {
        repository.observeWebPageList(isBookmark = true) {
            bookmarkList.clear()
            bookmarkList.add(WebPage(sticky = true))
            bookmarkList.addAll(it)
            bookmarkList.add(WebPage(sticky = true))
            bookmarkList.addAll(it)
            bookmarkList.add(WebPage(sticky = true))
            bookmarkList.addAll(it)
            bookmarkList.add(WebPage(sticky = true))
            bookmarkList.addAll(it)
            bookmarkList.add(WebPage(sticky = true))
            bookmarkList.addAll(it)
            // （为了方便测试，数据 5 倍重复显示）
        }
    }
    BookmarkList(
        bookmarkList = bookmarkList,
        itemClick = {
            WebActivity.start(it.url, it.title)
        },
        itemLongClick = {
            itemToDelete = it
            showDialog = true
        },
    )
    if (showDialog && itemToDelete != null) {
        BookmarkConfirmDialog(
            itemToDelete!!,
            cancel = {
                showDialog = false
            },
            ok = {
                lifecycleOwner.lifecycleScope.launch {
                    repository.removeWebPageByUrl(itemToDelete!!.url, isBookmark = true)
                }
                showDialog = false
            },
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun BookmarkList(
    bookmarkList: List<WebPage>,
    itemClick: (WebPage) -> Unit,
    itemLongClick: (WebPage) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background)) // 先填充背景
        ,
        // Column 让子组件水平居中对齐
        horizontalAlignment = Alignment.CenterHorizontally,
        // 垂直靠顶部
        verticalArrangement = Arrangement.Top
    ) {
/*
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(colorResource(R.color.icon_color_fore)),
            ) {
                Text(
                    "本地书签\n（为了方便测试，数据 5 倍重复显示）[1/5]",
                    color = colorResource(R.color.white),
                    textAlign = TextAlign.Center, // 设置文本对齐方式为居中
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
*/
        // 内容
        /*
                items(bookmarkList.size) { index ->
                    val data = bookmarkList[index]
                    BookmarkItem(
                        data = data,
                        count = bookmarkList.size,
                        currentIndex = index,
                        itemClick = itemClick,
                        itemLongClick = itemLongClick,
                    )
                }
        */

        // 计算列表的 nonStickyCount
        val nonStickyCount = bookmarkList.count { it.sticky.not() }

        bookmarkList.forEachIndexed { index, data ->

            // 计算当前项的 nonStickyIndex
            val currentStickyCount = bookmarkList
                .take(index + 1) // 截取到当前项，需要take(当前索引+1)
                .count { it.sticky }

            if (data.sticky) {
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(colorResource(R.color.icon_color_fore)),
                    ) {
                        Text(
                            "本地书签\n（为了方便测试，数据 5 倍重复显示）[${currentStickyCount}/5]",
                            color = colorResource(R.color.white),
                            textAlign = TextAlign.Center, // 设置文本对齐方式为居中
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            } else {
                val currentNonStickyIndex = index - currentStickyCount
                item {
                    BookmarkItem(
                        data = data,
                        count = nonStickyCount,
                        currentIndex = currentNonStickyIndex,
                        itemClick = itemClick,
                        itemLongClick = itemLongClick,
                    )
                }
            }
        }

        // 底线
        item {
            Column {
                Row(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.px2dp())
                            .weight(1.0f)
                            .background(color = colorResource(id = R.color.divider))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "我也是有底线的",
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.px2dp())
                            .weight(1.0f)
                            .background(color = colorResource(id = R.color.divider))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.px2dp())
                        .background(colorResource(R.color.divider)),
                )
            }
        }
    }
}

@Composable
private fun BookmarkItem(
    data: WebPage,
    count: Int = 0,
    currentIndex: Int = -1,
    itemClick: (WebPage) -> Unit = {},
    itemLongClick: (WebPage) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable {
                WebActivity.start(data.url, data.title)
            }
            .pointerInput(Unit) { // 会使 clickable 无效
                detectTapGestures(
                    onPress = {
                        // 按下
                        logd("onPress")
                    },
                    onTap = {
                        // 单击
                        logd("onTap")
                        itemClick.invoke(data)
                    },
                    onDoubleTap = {
                        // 双击
                        logd("onDoubleTap")
                    },
                    onLongPress = {
                        // 长按
                        logd("onLongPress")
                        itemLongClick.invoke(data)
                    },
                )
            },
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(1.px2dp())
                .background(colorResource(R.color.divider)),
        )
        Text(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 5.dp,
                )
                .align(Alignment.TopStart),
            text = data.title,
            color = colorResource(R.color.primaryText),
            fontSize = 16.sp,
            maxLines = 2, // 最多显示两行
            overflow = TextOverflow.Ellipsis, // 超过部分以省略号代替
            textAlign = TextAlign.Start, // 设置文本对齐方式为居中
        )
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.px2dp())
                .background(colorResource(R.color.divider))
        )
        Text(
            text = data.author ?: "",
            color = colorResource(R.color.secondaryText),
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    horizontal = 16.dp,
                    vertical = 5.dp,
                )
        )
        Text(
            text = data.time,
            color = colorResource(R.color.secondaryText),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    horizontal = 16.dp,
                    vertical = 5.dp,
                )
        )
        Text(
            text = "${currentIndex + 1}/${count}",
            color = colorResource(R.color.foreground30),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = 16.dp,
                    vertical = 5.dp,
                )
        )
    }
}

@Composable
fun BookmarkConfirmDialog(
    webPage: WebPage,
    cancel: () -> Unit,
    ok: () -> Unit
) {
    AlertDialog(
        title = {
            Text("删除确认", fontSize = 18.sp)
        },
        text = {
            Text("将删除 本地书签 [${webPage.title}]", fontSize = 14.sp)
        },
        onDismissRequest = cancel,
        confirmButton = {
            Text(
                "确定",
                modifier = Modifier
                    .clickable {
                        ok()
                    },
            )
        },
        dismissButton = {
            Text(
                "取消",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable {
                        cancel()
                    }

            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BookmarkActivityPreview() {
    val bookmarkList = mutableListOf<WebPage>()
    val webPage = WebPage(
        url = "https://mp.weixin.qq.com/s/Shf8Kiuj8TtMZfCaplPvjg",
        title = "Gemma 2 实例分享 | 使用 Dataflow 流式传输 ML 内容",
        author = "author",
        time = "2024/09/29 07:13:49"
    )
    for (i in 0 until 9) {
        bookmarkList.add(webPage)
    }
    BookmarkList(
        bookmarkList = bookmarkList,
        itemClick = {},
        itemLongClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun BookmarkItemPreview() {
    val webPage = WebPage(
        url = "https://mp.weixin.qq.com/s/Shf8Kiuj8TtMZfCaplPvjg",
        title = "Gemma 2 实例分享 | 使用 Dataflow 流式传输 ML 内容",
        author = "author",
        time = "2024/09/29 07:13:49"
    )
    BookmarkItem(data = webPage)
}

@Preview(showBackground = true)
@Composable
fun BookmarkDialogPreview() {
    val webPage = WebPage(
        url = "https://mp.weixin.qq.com/s/Shf8Kiuj8TtMZfCaplPvjg",
        title = "Gemma 2 实例分享 | 使用 Dataflow 流式传输 ML 内容",
        author = "author",
        time = "2024/09/29 07:13:49"
    )
    BookmarkConfirmDialog(webPage, {}, {})
}
