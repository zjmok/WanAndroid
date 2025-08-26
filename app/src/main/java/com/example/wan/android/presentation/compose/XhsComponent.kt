package com.example.wan.android.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wan.android.R
import com.example.wan.android.presentation.compose.ui.theme.WanAndroidTheme
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XhsComponent() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("小红书")
                },
            )
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { innerPadding ->
        Content(
            // 需要把 `.padding(innerPadding)` 传到里层，并在里层应用此 modifier，否则显示异常
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun Content(modifier: Modifier = Modifier) {

    val heights = List(20) { Random.nextInt(150, 300).dp } // 随机生成高度

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(heights.size) { index ->
            val height = heights[index]
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(height)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.icon_conan_selected),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "$index",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {

    val items = listOf("首页", "搜索", "通知", "我的")
    val icons = listOf(
        R.drawable.icon_home_selected,
        R.drawable.icon_project_selected,
        R.drawable.icon_subscribe_selected,
        R.drawable.icon_person_selected,
    )
    var selectedIndex by remember { mutableIntStateOf(0) }

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        modifier = Modifier.height(56.dp),
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
//                    Icon(
//                        painterResource(icons[index]),
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp),
//                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
//                        Spacer(modifier = Modifier.weight(2f))
                        Icon(
                            painterResource(icons[index]),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
//                        Spacer(modifier = Modifier.weight(1f))
                        Text(item, fontSize = 12.sp, textAlign = TextAlign.Center)
//                        Spacer(modifier = Modifier.weight(2f))
                    }
                },
                // icon 和 label 之间间距不可控，全放到 icon 里更方便自定义
//                label = {
//                    Text(item, fontSize = 14.sp)
//                },
                selected = selectedIndex == index,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorResource(R.color.icon_color_fore), // icon 选中颜色
                    selectedTextColor = colorResource(R.color.icon_color_fore), // text 选中颜色
                    unselectedIconColor = colorResource(R.color.icon_color_back), // icon 未选中颜色
                    unselectedTextColor = colorResource(R.color.icon_color_back), // text 未选中颜色
                    indicatorColor = Color.Transparent, // 选中效果
                ),
                onClick = {
                    selectedIndex = index
//                    toast(index)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun XhsComponentPreview() {
    WanAndroidTheme {
        XhsComponent()
    }
}
