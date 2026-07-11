# AGENTS.md

## Build Commands

```bash
# Full build (debug)
./gradlew assembleAppDebug

# Build specific variant
./gradlew assembleGithubRelease

# Run tests
./gradlew testAppDebugUnitTest

# Lint check
./gradlew lintAppDebug

# Clean
./gradlew clean
```

## Project Architecture

**WanAndroid** — Android client for wanandroid.com. MVVM architecture using Kotlin + Jetpack. Mixed View System + Jetpack Compose.

### Modules

- **`:app`** — Main application module
- **`:module_utils`** — Shared utility library (`com.zjmok.util.*`)

### Build Configuration

- AGP 8.13.2 / Gradle 8.14.4 / Kotlin 2.1.21 / JDK 17 toolchain
- compileSdk 36, minSdk 21, targetSdk 36
- KSP for Room + Glide annotation processing
- Compose BOM 2025.11.01 (Kotlin 2.x Compose compiler plugin)
- Configuration cache enabled, parallel builds enabled
- Product flavors: `app` (default), `github`
- Signing: `zjmok.jks` with credentials from `local.properties` or env vars

### Data Flow

Single source of truth via `WanRepository` object:
```
Fragment/Composable → ViewModel → WanRepository → ApiService (Retrofit)
                                                    ↕
                                              WanRepository.getWithCache()
                                                    ↕
                                             CompositeCache (Memory → Disk)
```

Repository strategy: cache-first with async network refresh. `getWithCache()` returns cached data immediately while fetching fresh data in the background.

### Three-Level Cache System

- **Memory (L1)** — `MemoryCache`: in-memory `Map` with `Mutex` for concurrency
- **Disk (L2)** — `DiskCache`: file-based (JSON files in `cacheDir/network_cache`) with `Mutex`
- **Network (L3)** — `OkHttp CacheInterceptor`: OkHttp-level cache with `max-age`/`max-stale` strategy
- **CompositeCache**: combines Memory + Disk; reads L1→L2, writes to both
- Cached APIs: Banner (30min), HomeTopList (30min), ProjectTree (24h), WxArticleTree (24h), ProjectList (10min), WxArticleList (10min)

### Package Structure

| Package | Purpose |
|---|---|
| `data.remote.api` | Retrofit service interfaces + API models |
| `data.remote.interceptors` | OkHttp interceptors (cache, logging, header, baseUrl) |
| `data.local.cache` | Memory/Disk/Composite cache implementation |
| `data.repository` | WanRepository — single source of truth for all data |
| `data.model` | Data classes for API responses |
| `presentation.feature.*` | MVVM feature modules (home, project, square, subscribe, person, etc.) |
| `presentation.feature.base` | Base classes for Activity/Fragment/ViewModel |
| `presentation.compose` | Jetpack Compose screens (debug, account, history, bookmark) |
| `di` | (empty) — manual DI via `object` singletons |
| `util` | Extensions and utilities |
| `config` | Image loader config (Coil, Glide) |

### Base Activity/Fragment Hierarchy

```
BaseActivity (immersion bar, loading dialog, language, keyboard)
├── VBaseActivity (+ ViewBinding)
│   ├── VMBaseActivity (+ ViewModel)
│   └── VVMBaseActivity (+ both)
```

Same pattern for fragments: `BaseFragment` → `VBaseFragment` / `VMBaseFragment` / `VVMBaseFragment`.

### BaseViewModel

Provides coroutine helpers (`launch`, `async`, `cancelJob`) with built-in error handling for:
- `ApiException` (errorCode -1001 = not logged in triggers logout)
- Network exceptions (ConnectException, SocketTimeoutException, etc.)
- Error toasts (configurable)

### Key Dependencies

- **Network**: OkHttp4, Retrofit2, Gson, PersistentCookieJar
- **Images**: Glide (KSP), Coil (Compose)
- **Persistence**: Room (KSP), DataStore Preferences, LitePal
- **UI**: Material, ViewPager2, SmartRefreshLayout, ViewBinding, Compose
- **Others**: ImmersionBar, LiveEventBus, PermissionX, XPopup, Splitties

### Feature Navigation

Main interface: TabLayout + ViewPager2 with 5 tabs (Home, Project, Square, Subscribe, Person). DrawerLayout for side menu. Scheme routing handles `wanandroid://`, `http/https`, `market`, `weixin`, `mailto`.

Pages use Paging 3 for list loading with `ArticleListDataSource` (`PagingSource` implementation). Pull-to-refresh via SmartRefreshLayout.

Compose is used for: Debug page, Account screen, History screen, Bookmark screen, XHS-style page.

### Multi-language & Dark Mode

- Language: system-following + manual toggle via `AppCompatDelegate.setApplicationLocales()`
- Dark mode: system-following + manual toggle via `AppCompatDelegate.setDefaultNightMode()`
- Broadcasts `REFRESH_LANGUAGE` event via LiveEventBus — observed activities call `recreate()`
