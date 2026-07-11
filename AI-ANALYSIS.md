# WanAndroid 项目分析报告

## 项目概览

**项目名称**: WanAndroid  
**类型**: Android 原生应用  
**架构**: MVVM + Clean Architecture  
**语言**: Kotlin 100%  
**API 版本**: minSdk 21, targetSdk 36, compileSdk 36  

## 技术栈

### 核心框架
- **Kotlin**: 2.x (基于 kotlin_version 变量)
- **Jetpack**: Lifecycle, LiveData, ViewModel, Paging, Navigation, Room, DataStore
- **Compose**: 2025.11.01 (支持混合开发)

### 网络层
- OkHttp3 4.12.0
- Retrofit2 2.12.0
- PersistentCookieJar
- Coroutines

### 图片加载
- Glide 4.16.0 (主力)
- Coil 2.4.0 (Compose 专用)

### UI 组件
- Material Design 1.13.0
- ViewPager2
- SmartRefreshLayout (刷新)
- Banner (轮播)
- XPopup (弹窗)
- PermissionX (权限)
- RWidgetHelper (布局)
- Matisse (图库)

### 工具库
- AndroidUtilCode 1.31.1
- Splitties 3.0.0
- LiveEventBus 1.8.14
- ImmersionBar 3.4.0
- LitePal 3.2.3
- ViewBindingPropertyDelegate

### 本地存储
- Room 2.7.2
- DataStore 1.1.7
- LitePal

## 项目结构

```
WanAndroid/
├── app/                     # 主应用模块
│   └── src/main/
│       ├── java/org/example/wan/android/
│       │   ├── data/        # 数据层 (API, Repository, Model, Cache)
│       │   ├── presentation/ # 展示层 (Activity, Fragment, ViewModel)
│       │   ├── util/        # 工具类
│       │   ├── constant/    # 常量
│       │   └── config/      # 框架配置
│       └── res/            # 资源
├── module_utils/             # 工具库模块 (33个工具类)
├── module_lint/            # 自定义 Lint 库
└── build/                  # 构建输出
```

## 模块划分

### Data 层
- **data/model**: 数据模型 (Article, Banner, User 等)
- **data/remote**: 网络请求 (RetrofitClient, Services, Interceptors)
- **data/local**: 本地存储 (Cache, Room)
- **data/repository**: 数据仓库

### Presentation 层
- **feature**: 核心功能模块
  - home: 首页 (Banner + 列表聚合)
  - qa: 问答
  - project: 项目
  - subscribe: 公众号
  - like: 收藏
  - login: 登录
  - web: 浏览器
  - common: 通用组件
- **base**: 基类 (BaseViewModel)
- **dialog**: 弹窗

### 工具模块
- **util/glide**: Glide 扩展
- **util/okhttp3**: OkHttp 扩展
- **util/gson**: Gson 扩展
- **util/liveeventbus**: 事件总线
- **util/permissionx**: 权限管理

## 功能特性

### 已完成
- [x] 多渠道打包 (app, github)
- [x] TabLayout + ViewPager2 + Fragment 导航
- [x] DrawerLayout 侧滑导航
- [x] 状态栏沉浸
- [x] Banner 轮播
- [x] Paging 分页加载
- [x] 原生下拉刷新
- [x] 深色模式 (跟随系统 + 手动)
- [x] 多语言
- [x] Splash 无缝衔接
- [x] 多种 Scheme 跳转 (邮件/微信/浏览器/应用商店/http/https/wanandroid)
- [x] 自定义浏览器/应用选择
- [x] 管理空间页面
- [x] Compose 混合使用
- [x] 本地浏览历史 (Compose + DataStore)
- [x] 本地书签
- [x] 全局搜索
- [x] Debug 悬浮按钮

### 待优化
- [ ] 网络请求三级缓存优化
- [ ] 问答评论改为 BottomSheetDialogFragment

## 构建配置

### Gradle
- 版本: 8.14.4
- JVM: 17
- Java 字节码: 1.8
- Kotlin 字节码: 1.8
- Configuration Cache: 开启
- Parallel Build: 开启

### 版本号管理
- **versionCode**: git commit count
- **versionName**: {branch}.{commitCount}_{gitCommitId}

### 变体组合
- Flavor: app, github
- BuildType: debug, release

## 依赖亮点

1. **混合开发**: View + Compose 完美共存
2. **多图库支持**: Glide(传统) + Coil(Compose)
3. **多语言支持**: 运行时切换
4. **深度优化**: SmartRefreshLayout + Paging
5. **自定义 Lint**: module_lint 模块

## Git 信息

**最新提交**: 0922331 (no message)  
**分支**:  
- origin/phone/dev  
- origin/phone/release  
- origin/github/dev  
- origin/gitee/dev

## 总结

这是一个功能完备的 Android 知识库客户端应用，采用了现代化的 MVVM + Clean Architecture 架构。技术栈全面，覆盖了网络、图片、存储、UI 等各个方面。亮点是支持 View 和 Compose 混合开发，代码质量较高，工具类丰富。