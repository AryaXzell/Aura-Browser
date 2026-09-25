# Aura Browser

Aura Browser is a lightweight, high-performance Android web browser built using Kotlin and Jetpack Compose. Engineered with low-memory devices in mind (Android Go tier, 2–3 GB RAM baseline), it provides a responsive browsing experience, multi-engine search, ad blocking, and tab management without unnecessary background overhead.

## Key Features

- **Multi-Tab Architecture with LRU WebView Pooling**: Instant tab switching without reloading pages, while capping concurrent live `WebView` instances to 4 to prevent out-of-memory errors on entry-level hardware.
- **Privacy and Security Controls**:
  - Integrated network interceptor for blocking telemetry and intrusive advertisement domains.
  - Do Not Track (DNT) header injection.
  - Independent JavaScript toggle.
  - Granular browsing data cleanup (cache, cookies, history).
  - Private Incognito mode with isolated cookie jars and immediate memory reclamation upon tab closure.
- **Customizable Search & Homepage**:
  - Direct integration with Google, DuckDuckGo, Bing, and Brave search engines.
  - Persistent customizable shortcuts with automatic favicon retrieval via `WebChromeClient`.
  - Quick access to browsing history and bookmarks stored locally.
- **Micro-Animations and Theming**:
  - Smooth theme transitions between Light, Dark, and System settings powered by `animateColorAsState`.
  - Non-blocking micro-animations (150–350ms) built with Compose animation APIs.
  - Native Android 12+ Splash Screen integration (`androidx.core:core-splashscreen`) eliminating blank launch flashes.

## Architecture

The application adopts Clean Architecture principles with MVVM (Model-View-ViewModel):

```
app/src/main/java/com/aryaxzell/aurabrowser/
├── data/
│   ├── db/                 # Room database, DAOs, and entities (History, Bookmarks)
│   ├── model/              # Domain models (TabItem, ShortcutItem, SearchEngine)
│   └── preferences/        # Reactive SharedPreferences using Kotlin StateFlow
├── ui/
│   ├── components/         # Jetpack Compose UI components
│   └── theme/              # Material 3 ColorScheme, Typography, and Theme definitions
├── viewmodel/              # BrowserViewModel managing application state
└── webview/                # WebViewPoolManager handling LRU WebView eviction
```

### Reactive State Flow
Application preferences and tab state are driven by Kotlin `StateFlow` and collected in Compose using `collectAsStateWithLifecycle()`. This ensures that setting updates—such as changing search engines or requesting desktop sites—reflect immediately across all active screens.

## Performance Engineering

Aura Browser is tested against entry-level devices (such as MediaTek Helio G25/G35, 2–3 GB RAM):

- **Startup Latency**: Zero runtime bloat from unused network or analytics providers. Process cold start directly initializes the Compose view tree within 1.5–3 seconds.
- **Memory Footprint**: `WebViewPoolManager` monitors tab count and destroys the least-recently-used WebView instances beyond the maximum limit, avoiding memory pressure and foreground process termination by the Android LMK (Low Memory Killer).

## Technical Specifications

- **Language**: Kotlin 2.2.x
- **UI Framework**: Jetpack Compose with Material Design 3
- **Local Persistence**: Room Database 2.7.x (SQLite)
- **Minimum SDK**: Android 7.0 (API Level 24)
- **Target SDK**: Android 15 (API Level 36)
- **Build System**: Gradle with Kotlin DSL and Version Catalog (`libs.versions.toml`)

## Building and Development

### Prerequisites
- JDK 17 or later
- Android SDK with platform tools and build-tools (API 36)
- Git

### Build Locally
Clone the repository:
```bash
git clone https://github.com/aryaxzell/aura-browser.git
cd aura-browser
```

To compile and assemble a standard debug APK:
```bash
./gradlew assembleDebug
```

To run unit and Robolectric tests:
```bash
./gradlew testDebugUnitTest
```

### Generating ABI-Specific APKs
To produce architecture-specific splits alongside a universal package, build with the `splitApks` property:
```bash
./gradlew assembleDebug -PsplitApks
```

Generated outputs located in `app/build/outputs/apk/debug/`:
1. `app-arm64-v8a-debug.apk`: 64-bit ARM architecture.
2. `app-armeabi-v7a-debug.apk`: 32-bit legacy ARM architecture.
3. `app-universal-debug.apk`: Universal package containing all architectures.

## Continuous Integration

Every commit and pull request triggers automated GitHub Actions workflows:
- Verification through automated unit and Robolectric test suites.
- Assembly and packaging of the 3 separate application artifacts (`arm64-v8a`, `armeabi-v7a`, `universal`).
- Automated artifact upload for testing and distribution.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
