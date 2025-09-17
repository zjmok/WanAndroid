package org.example.wan.android.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ActivityUtils
import org.example.wan.android.R
import org.example.wan.android.composable.ImageFromDrawableRes
import org.example.wan.android.composable.ImageFromRes
import org.example.wan.android.presentation.compose.ui.theme.WanAndroidTheme
import org.example.wan.android.presentation.feature.person.PersonViewModel
import org.example.wan.android.presentation.feature.setting.SettingActivity
import org.example.wan.android.util.toast

@Composable
fun AccountComponent(viewModel: PersonViewModel) {

    // 将 LiveData 的数据状态化
    val superUserInfo by viewModel.superUserInfo.observeAsState()

    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.wx_background))
            .fillMaxSize()
            .wrapContentSize(Alignment.TopCenter)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(id = R.color.wx_foreground))
                .clickable(
                    // 点击事件的水波纹效果
                    indication = null, // 去除 ripple 效果
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    toast("未登录时跳转登录页面")
                }
                .padding(28.dp, 50.dp, 16.dp, 30.dp),
        ) {
            ImageFromDrawableRes(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp)),
                drawableRes = R.mipmap.ic_launcher,
            )
            Spacer(modifier = Modifier.width(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 36.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = superUserInfo?.userInfo?.nickname ?: "未登录",
                        fontSize = 18.sp,
                        color = colorResource(id = R.color.primaryText)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row {
                        Text(
                            text = "用户名: ${superUserInfo?.userInfo?.username ?: "null"}",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.secondaryText)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = "id: ${superUserInfo?.userInfo?.id ?: "-1"}",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.secondaryText)
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "邮箱: ${superUserInfo?.userInfo?.email ?: "null"}",
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.secondaryText)
                    )
                }
                ImageFromRes(
                    modifier = Modifier.size(25.dp),
                    drawableRes = R.drawable.icon_arrow_right
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(id = R.color.wx_foreground))
                .clickable {
                    toast("开发中")
                }
                .padding(16.dp)
        ) {
            ImageFromRes(
                modifier = Modifier.size(28.dp),
                drawableRes = R.drawable.icon_bi
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "积分排行",
                fontSize = 16.sp,
                color = colorResource(id = R.color.primaryText)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "积分: ${superUserInfo?.coinInfo?.coinCount}",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondaryText)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "排行: ${superUserInfo?.coinInfo?.rank}",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondaryText)
            )
            Spacer(modifier = Modifier.width(10.dp))
            ImageFromRes(
                modifier = Modifier.size(25.dp),
                drawableRes = R.drawable.icon_arrow_right
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(id = R.color.wx_foreground))
                .clickable {
                    toast("开发中")
                }
                .padding(16.dp)
        ) {
            ImageFromRes(
                modifier = Modifier.size(28.dp),
                drawableRes = R.drawable.icon_article
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "收藏文章",
                fontSize = 16.sp,
                color = colorResource(id = R.color.primaryText)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "收藏量: ${superUserInfo?.collectArticleInfo?.count} 篇",
                fontSize = 14.sp,
                color = colorResource(id = R.color.secondaryText)
            )
            Spacer(modifier = Modifier.width(10.dp))
            ImageFromRes(
                modifier = Modifier.size(25.dp),
                drawableRes = R.drawable.icon_arrow_right
            )
        }
        Spacer(modifier = Modifier.height(0.5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(id = R.color.wx_foreground))
                .clickable {
                    toast("开发中")
                }
                .padding(16.dp)
        ) {
            ImageFromRes(
                modifier = Modifier.size(28.dp),
                drawableRes = R.drawable.icon_share
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "分享文章",
                fontSize = 16.sp,
                color = colorResource(id = R.color.primaryText)
            )
            Spacer(modifier = Modifier.weight(1f))
            ImageFromRes(
                modifier = Modifier.size(25.dp),
                drawableRes = R.drawable.icon_arrow_right
            )
        }
        Spacer(modifier = Modifier.height(0.5.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(id = R.color.wx_foreground))
                .clickable {
                    toast("开发中")
                }
                .padding(16.dp)
        ) {
            ImageFromRes(
                modifier = Modifier.size(28.dp),
                drawableRes = R.drawable.icon_site
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "收藏网站",
                fontSize = 16.sp,
                color = colorResource(id = R.color.primaryText)
            )
            Spacer(modifier = Modifier.weight(1f))
            ImageFromRes(
                modifier = Modifier.size(25.dp),
                drawableRes = R.drawable.icon_arrow_right
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(colorResource(id = R.color.wx_foreground))
                .clickable {
                    ActivityUtils.startActivity(SettingActivity::class.java)
                }
                .padding(16.dp)
        ) {
            ImageFromRes(
                modifier = Modifier.size(28.dp),
                drawableRes = R.drawable.icon_settings
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "设置", fontSize = 16.sp, color = colorResource(id = R.color.primaryText))
            Spacer(modifier = Modifier.weight(1f))
            ImageFromRes(
                modifier = Modifier.size(25.dp),
                drawableRes = R.drawable.icon_arrow_right
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountComponentPreview() {
    WanAndroidTheme {
        AccountComponent(PersonViewModel())
    }
}
