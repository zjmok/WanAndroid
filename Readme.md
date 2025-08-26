# WanAndroid, View System + Compose

## 效果图

<div style="display: flex; justify-content: space-between;">
    <img src="picture/pic_pj.jpg" width="32%">
    <img src="picture/pic_qa.jpg" width="32%">
    <img src="picture/pic_qa2.jpg" width="32%">
</div>

<div style="display: flex; justify-content: space-between;">
    <img src="picture/pic_dark.jpg" width="32%">
    <img src="picture/pic_me.jpg" width="32%">
    <img src="picture/pic_me_dark.jpg" width="32%">
</div>

<div style="display: flex; justify-content: space-between;">
    <img src="picture/pic_sub.jpg" width="32%">
    <img src="picture/pic_email.jpg" width="32%">
    <img src="picture/pic_info.jpg" width="32%">
</div>

## 项目架构

MVVM 架构, 基于 Kotlin + JetPack 组件: Lifecycle + LiveData + ViewModel + Paging

## 功能及技术点

- [x] 网络, [OkHttp3](https://github.com/square/okhttp) + [Retrofit2](https://github.com/square/retrofit) + Coroutine, [PersistentCookieJar](https://github.com/franmontiel/PersistentCookieJar)

- [x] 图片, [Glide](https://github.com/bumptech/glide)

- [x] 消息, [LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)

- [x] 权限, [PermissionX](https://github.com/guolindev/PermissionX)

- [x] 首页导航, 使用 TabLayout + ViewPager2 + Fragment, 自定义 TabItem, 再次点击 Tab 刷新页面

- [x] DrawerLayout 侧滑导航

- [x] 状态栏沉浸, [ImmersionBar](https://github.com/gyf-dev/ImmersionBar)

- [x] Banner, [banner](https://github.com/youth5201314/banner)

- [x] 列表加载, Paging 组件

- [x] 列表刷新, ~~[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)~~ 原生

- [x] 深色模式, 跟随系统 + 手动模式

- [x] 多语言, 跟随系统 + 手动模式。加了功能, 没有翻译

- [x] Splash 页面适配, 仿 bilibili, 无缝衔接

- [x] 首页, 调整为一个列表, Banner + Other接口 + Paging 的列表聚合

- [x] APP 跳转
  - 邮件 Scheme 跳转。发送邮件时，使用指定邮件应用列表过滤掉其它应用（只是处理了邮件 Scheme 但不是邮件应用）
    - （网易邮箱有个 Bug，反馈了也一直不改，无法识别 EXTRA_EMAIL，其它邮件应用都能正常识别）
  - 微信协议 weixin Scheme 跳转微信 APP
  - 浏览器 Scheme 跳转。打开浏览器时，使用自定义浏览器列表，包含系统浏览器列表之外的一些浏览器（而且不会出现什么 天猫、京东、微博、WPS 这些阿猫阿狗都来冒充浏览器的情况）
    - （系统浏览器列表规则是，当用户在系统设置了默认浏览器应用，之后只能调起这一个默认浏览器）
    - （微信的方案似乎是，获取了可解析 http/https Scheme 的所有应用列表，然后自定义浏览器应用列表界面和App自己的默认浏览器逻辑）
    - （另外，小米 MIUI 12 有个 Bug，魔改它自己的默认浏览器到推荐位置后导致后面其它方式的索引差了一位，其它系统没有这个问题）
  - 应用商店 Scheme 跳转
  - http/https Scheme 处理
  - 自定义 wanandroid Scheme 处理

- [x] 自定义管理空间页面, 参考 Chrome, 具体路径: 应用详情 -> 清除数据 -> 管理空间 -> 自定义页面

- [x] 集成 Compose, 可混合使用 Compose 开发

- [x] 查看/删除 本地浏览历史, Compose UI, 基于 DataStore 本地数据存取

- [x] 本地书签, 因接口收藏功能依赖于文章 ID, 不是所有网页都可进行收藏; 而这个本地书签功能只是依赖 URL, 可以将任意网页保存为书签 

- [x] 全局搜索 公众号搜索, UI 使用 CoordinatorLayout + 自定义 Behavior

- [x] Debug 全局悬浮按钮

- [ ] 网络请求优化, 通过 LiveData 或 Flow 手动使用三级缓存。请求网络时, 在网络响应等待前, 先返回一次缓存数据

- 其它 工具类 便捷库

  - 工具类, [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)

  - Kotlin Android 扩展, [Splitties](https://github.com/LouisCAD/Splitties)

  - 弹窗, [XPopup](https://github.com/li-xiaojun/XPopup)

  - 布局, [RWidgetHelper](https://github.com/RuffianZhong/RWidgetHelper)

  - ViewBinding 扩展, [ViewBindingPropertyDelegate](https://github.com/androidbroadcast/ViewBindingPropertyDelegate)

  - RecyclerView ItemDecoration [SpacingItemDecoration](https://github.com/grzegorzojdana/SpacingItemDecoration)

