# AGENTS.md

本文档为 AI 协作代理（Claude / Cursor / Trae 等）在本仓库工作时的统一约定。修改代码前请先通读本文档。

## 项目简介

WanAndroid 是基于 wanandroid.com 开放 API 的 Android 客户端，采用 View System + Jetpack Compose 混合开发模式。整体架构为 MVVM + Clean Architecture 风格（domain 用例并入 Repository，未单独分层）。

- 应用包名：`org.example.wan.android`
- 最低 SDK：21
- 目标 / 编译 SDK：36
- Kotlin：2.1.21
- AGP：8.13.2
- JDK 工具链：17（输出字节码 1.8）

## 模块结构

```text
./
├── app/                        # 主应用模块
│   ├── src/main/               # 主源集
│   │   ├── java/org/example/wan/android/
│   │   │   ├── data/           # 数据层（repository / remote / local / model）
│   │   │   ├── presentation/   # 表现层（feature / compose）
│   │   │   ├── config/         # 三方库初始化（Coil / Glide / Matisse）
│   │   │   ├── constant/       # 常量（AppConst / BusKey / EventBus）
│   │   │   ├── service/        # Service
│   │   │   ├── util/           # 应用工具类与扩展
│   │   │   ├── composable/     # Compose 复用组件
│   │   │   ├── App.kt          # Application 入口
│   │   │   ├── AppViewModel.kt # 应用级 ViewModel
│   │   │   └── AppLifecycleEventObserver.kt
│   │   ├── res/                # 主资源
│   │   ├── res-language/       # 多语言资源（含少数民族语言）
│   │   ├── res-splash/         # Splash 资源
│   │   ├── res-setting/        # 设置页资源
│   │   └── AndroidManifest.xml
│   ├── src/debug/              # debug 专用源集（BaseUrlInterceptor 真实实现 / FloatButton）
│   ├── src/placeholders/       # release 占位空壳（BaseUrlInterceptor 占位实现）
│   ├── build.gradle            # Groovy DSL
│   └── proguard-rules.pro
├── module_utils/               # 工具库（com.zjmok.util），Android Library
│   └── build.gradle.kts        # Kotlin DSL，发布坐标 com.github.zjmok:util:0.0.1
├── module_lint/                # 自定义 Lint 规则（JVM 库，settings.gradle 中已注释，未参与构建）
│   └── build.gradle
├── doc/                        # 项目文档
├── picture/                    # README 截图
├── .github/workflows/          # CI（tag 触发构建 GithubRelease APK）
├── settings.gradle             # 仓库统一声明（FAIL_ON_PROJECT_REPOS）
├── build.gradle                # 根构建脚本（kotlin_version / agp_version / plugins）
├── config_source.gradle        # app 模块 sourceSet 拓展配置
├── gradle.properties           # Gradle 与 JVM 配置
├── http-client.env.json        # IntelliJ HTTP Client 环境
└── http_wanandroid_api.http    # WanAndroid 接口调试
```

## 构建与运行

### 常用命令

```bash
# 调试构建（默认 app + debug 变体）
./gradlew assembleDebug

# Github 渠道 Release 构建（CI 使用）
./gradlew assembleGithubRelease

# 安装到设备
./gradlew installAppDebug

# Lint 检测
./gradlew lint
./gradlew lintRelease
```

### 构建变体（Build Variants）

- `flavorDimensions = ["channel"]`
- `productFlavors`：`app`（默认正式渠道）/ `github`（applicationId 加 `.github` 后缀）
- `buildTypes`：`debug`（默认、可调试、不混淆）/ `release`（R8 混淆 + 资源压缩）
- 最终变体示例：`AppDebug`、`AppRelease`、`GithubDebug`、`GithubRelease`

### 签名

- Release 签名从根目录 `release.jks.properties` 文件或环境变量读取（CI 注入）
- 模板见 `release.jks.properties.template`
- `release.jks` 与 `release.jks.properties` 已在 `.gitignore` 中

### 版本号

- `versionCode` = git 提交计数（`git rev-list --count HEAD`）
- `versionName` = `"${gitBranch}.${gitCommitCount}"`
- 通过 `providers.exec { ... }` 实现，兼容 Gradle configuration-cache

## 架构与关键约定

### 分层

```text
presentation (UI 层)
    │  调用
    ▼
data (Repository + 网络 + 本地 + 缓存)
    │
    ▼
remote / local / cache (具体实现)
```

- ViewModel 直接调用 `WanRepository`，无独立 domain 用例层
- 单一数据源：所有数据访问通过 `WanRepository`（object 单例）
- 网络层：`RetrofitClient`（object 单例）持有 OkHttp + Retrofit + 4 个 Retrofit Service

### 依赖注入

**手动 DI**，不使用 Hilt / Koin。

- 单例使用 Kotlin `object`：`App`、`WanRepository`、`RetrofitClient`、`DiskCache`、`UtilLib`
- `App` 持有共享对象：`appViewModel`、`appScope`（CoroutineScope）、`dataStore`
- ViewModel 通过 `module_utils` 的 `ViewModelUtils.getViewModel()` 扩展获取
- 不要为这些单例引入构造函数参数

### View System 与 Compose 共存

- **主体**：所有 feature 页面使用 AppCompatActivity + ViewBinding + XML
- **Compose 并行**：`presentation/compose/` 下的 Activity 继承 `ComponentActivity` + `setContent { WanAndroidTheme { ... } }`
- ViewBinding 防内存泄漏：使用 `ViewBindingPropertyDelegate`（`by viewBinding()`）
- Compose 互操作已引入：`ui-viewbinding`（XML 嵌 Compose）+ `runtime-livedata`（Compose 观察 LiveData）

### 三级缓存

位于 `data/local/cache/`，由 `WanRepository.getWithCache()` 编排：

- L1 `MemoryCache`：进程内 Map + Mutex
- L2 `DiskCache`：文件 IO + Mutex，路径 `cacheDir/network_cache/`
- L3 `Network`：Retrofit Service
- 策略：有缓存立即返回 + 异步刷新；未命中同步请求并写回两层缓存
- 缓存接口：Banner、HomeTopList、ProjectTree、WxArticleTree、ProjectList、WxArticleList
- 详见 `doc/三级缓存架构设计与实现分析.md`

### 本地存储

- **DataStore (Preferences)**：实际使用，存储浏览历史与本地书签（`WebPageRepository`）
- **SharedPreferences**：通过 utilcodex `SPUtils` 使用（夜间模式、cookie 持久化）
- **LitePal**：仅初始化未实际使用（`litepal.xml` 仍是模板）
- **Room**：依赖已声明但未发现 `@Database` / `@Dao` / `@Entity` 实现，新增本地数据时建议沿用 DataStore

### 基类层级

位于 `presentation/feature/base/`：

- Activity：`BaseActivity` → `VBaseActivity<VB>` → `VMBaseActivity` / `VVMBaseActivity<VB, VM>`
- Fragment：`BaseFragment` → `VBaseFragment<VB>` → `VMBaseFragment` / `VVMBaseFragment<VB, VM>`
- ViewModel：`BaseViewModel`（封装协程 `launch` / `async`、统一错误处理、登录态管理）

新增页面应继承对应基类，不要直接继承 AppCompatActivity / Fragment。

## 代码风格与约定

### 通用

- Kotlin code style = `official`（见 `gradle.properties`）
- 文件编码 UTF-8
- 不要为内部代码添加额外注释或文档字符串，除非逻辑不自明
- 仅在系统边界（用户输入、外部 API）做校验
- 不主动添加 emoji、不在文档中使用 emoji
- 不创建多余文件，优先编辑现有文件

### 日志

- **禁止**直接使用 `android.util.Log`（`module_lint/LogDetector` 已定义规则，启用后会报 WARNING）
- 使用项目统一的 `log()` / `logd()` / `loge()` / `logi()` 扩展（位于 `module_utils` 的 `LogUtils.kt`）
- Release 构建会通过 ProGuard `-assumenosideeffects` 移除 `android.util.Log` 调用

### 资源

- 多语言资源在 `src/main/res-language/`，已支持 20+ 语言（含少数民族语言）
- Splash 资源单独放在 `src/main/res-splash/`
- 设置页资源单独放在 `src/main/res-setting/`
- 新增资源按功能归入对应 sourceSet，不要堆在 `res/` 根下

### Gradle 配置

- `settings.gradle` 使用 `FAIL_ON_PROJECT_REPOS`，**所有仓库必须在 settings.gradle 中声明**，模块级 build.gradle 不可再加 `repositories`
- 仓库镜像优先级：阿里云 > 华为云 > 腾讯云 > 网易 > maven central > jitpack
- `gradle.properties` 已启用 `parallel`、`caching`、`configuration-cache`、`incremental`
- JVM 内存：`-Xmx8g -XX:MaxMetaspaceSize=2g`（大内存配置避免 OOM，详见 `doc/OutOfMemoryError_Java_heap_space.md`）

### Scheme / DeepLink

- `SchemeActivity`（exported=true）处理 4 种 scheme：`wanandroid` / `http` / `https` / `market`
- 修改 scheme 处理逻辑需同步更新 `AndroidManifest.xml` 的 `<intent-filter>`

### WebView

- WebView 使用 `WebView(App.INSTANCE)` 构造，避免 Activity Context 泄漏
- `onDestroy` 必须按顺序：`stopLoading()` → `destroy()` → `removeAllViews()` → `super.onDestroy()`
- 修改 WebView 行为时关注 `WebPageRepository`（浏览历史与书签持久化）

## CI / CD

`.github/workflows/build_release.yml`：

- 触发条件：push `v*` tag
- 构建 `GithubRelease` 变体
- 签名通过 Environment Secrets 注入（`RELEASE_STORE_FILE_BASE64` 等）
- 产物上传到 GitHub Release：`app/build/outputs/apk/github/release/*.apk`

## 常见陷阱

1. **不要在模块 build.gradle 中加 repositories**：会与 `FAIL_ON_PROJECT_REPOS` 冲突报错
2. **不要直接使用 `executeCmd` 执行 git 命令**：会破坏 configuration-cache，必须用 `providers.exec { ... }`
3. **不要给 `App` / `WanRepository` / `RetrofitClient` 加构造参数**：它们是 `object` 单例
4. **新增 Activity / Fragment 必须在 `AndroidManifest.xml` 注册**
5. **修改源集目录需同步更新 `config_source.gradle`**
6. **`module_lint` 当前未启用**：`settings.gradle` 中 `include ":module_lint"` 已注释，自定义 Lint 规则暂不生效
7. **Room 依赖已声明但未使用**：新增本地数据库前确认是否真的需要，当前数据存储走 DataStore

## 相关文档

- [Readme.md](Readme.md) - 项目功能与技术点总览
- [doc/项目架构.md](doc/项目架构.md) - 详细架构说明
- [doc/三级缓存架构设计与实现分析.md](doc/三级缓存架构设计与实现分析.md) - 缓存实现细节
- [doc/OutOfMemoryError_Java_heap_space.md](doc/OutOfMemoryError_Java_heap_space.md) - JVM 内存配置
- [doc/studio64.exe.vmoptions 和 gradle.properties 中的 JVM 配置.md](doc/studio64.exe.vmoptions%20和%20gradle.properties%20中的%20JVM%20配置.md) - AS 与 Gradle JVM 配置
