package com.aryaxzell.aurabrowser

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.ValueCallback
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aryaxzell.aurabrowser.ui.components.AddShortcutDialog
import com.aryaxzell.aurabrowser.ui.components.BookmarksView
import com.aryaxzell.aurabrowser.ui.components.BrowserBottomBar
import com.aryaxzell.aurabrowser.ui.components.BrowserTopBar
import com.aryaxzell.aurabrowser.ui.components.BrowserWebView
import com.aryaxzell.aurabrowser.ui.components.DownloadsTabContent
import com.aryaxzell.aurabrowser.ui.components.DownloadsView
import com.aryaxzell.aurabrowser.ui.components.HistoryView
import com.aryaxzell.aurabrowser.ui.components.HomepageView
import com.aryaxzell.aurabrowser.ui.components.SettingsView
import com.aryaxzell.aurabrowser.ui.components.TabSwitcherView
import com.aryaxzell.aurabrowser.ui.theme.MyApplicationTheme
import com.aryaxzell.aurabrowser.viewmodel.BrowserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: baik diterima maupun ditolak, download tetap jalan */ }

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val resultUris: Array<Uri>? = when {
            result.resultCode != RESULT_OK -> null
            data?.clipData != null -> {
                val clipData = data.clipData!!
                Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
            }
            data?.data != null -> arrayOf(data.data!!)
            else -> null
        }
        fileChooserCallback?.onReceiveValue(resultUris)
        fileChooserCallback = null
    }

    fun launchFileChooser(intent: Intent, callback: ValueCallback<Array<Uri>>) {
        fileChooserCallback?.onReceiveValue(null)
        fileChooserCallback = callback
        try {
            fileChooserLauncher.launch(intent)
        } catch (e: Exception) {
            fileChooserCallback = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Handle external VIEW intent (e.g. clicked link from another app)
        handleIntent(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            // Sinkronkan warna ikon status bar & navigation bar dengan tema aktif
            LaunchedEffect(isDarkTheme) {
                val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                accentColor = accentColor
            ) {
                BrowserApp(
                    viewModel = viewModel,
                    onShowFileChooser = { intent, callback -> launchFileChooser(intent, callback) }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null && (uri.scheme == "http" || uri.scheme == "https")) {
            viewModel.createNewTab(url = uri.toString(), isIncognito = false)
        }
    }
}

@Composable
fun BrowserApp(
    viewModel: BrowserViewModel,
    onShowFileChooser: (Intent, ValueCallback<Array<Uri>>) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    // Reactive preferences
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
    val showShortcuts by viewModel.showShortcuts.collectAsStateWithLifecycle()
    val showRecentHistory by viewModel.showRecentHistory.collectAsStateWithLifecycle()
    val tabSwitcherLayout by viewModel.tabSwitcherLayout.collectAsStateWithLifecycle()
    val bottomBarItems by viewModel.bottomBarItems.collectAsStateWithLifecycle()
    val wallpaperUri by viewModel.wallpaperUri.collectAsStateWithLifecycle()
    val isWallpaperBlurEnabled by viewModel.isWallpaperBlurEnabled.collectAsStateWithLifecycle()
    val homeIconUri by viewModel.homeIconUri.collectAsStateWithLifecycle()
    val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsStateWithLifecycle()
    val isDesktopModeDefault by viewModel.isDesktopModeDefault.collectAsStateWithLifecycle()
    val isJavaScriptEnabled by viewModel.isJavaScriptEnabled.collectAsStateWithLifecycle()
    val isDoNotTrackEnabled by viewModel.isDoNotTrackEnabled.collectAsStateWithLifecycle()
    val dnsProvider by viewModel.dnsProvider.collectAsStateWithLifecycle()
    val dnsCustomValue by viewModel.dnsCustomValue.collectAsStateWithLifecycle()

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle()

    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val isEditingUrl by viewModel.isEditingUrl.collectAsStateWithLifecycle()

    val showTabSwitcher by viewModel.showTabSwitcher.collectAsStateWithLifecycle()
    val showBookmarksSheet by viewModel.showBookmarksSheet.collectAsStateWithLifecycle()
    val showHistorySheet by viewModel.showHistorySheet.collectAsStateWithLifecycle()
    val showDownloadsSheet by viewModel.showDownloadsSheet.collectAsStateWithLifecycle()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsStateWithLifecycle()
    val showAddShortcutDialog by viewModel.showAddShortcutDialog.collectAsStateWithLifecycle()

    val webAction by viewModel.webAction.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()

    val isBookmarked = remember(activeTab.url, bookmarks) {
        activeTab.url.isNotBlank() && bookmarks.any { it.url == activeTab.url }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BrowserTopBar(
                url = activeTab.url,
                isHome = activeTab.isHome,
                isIncognito = activeTab.isIncognito,
                isDesktopMode = activeTab.isDesktopMode,
                isLoading = activeTab.isLoading,
                progress = activeTab.progress,
                themeColor = activeTab.themeColor,
                isEditingUrl = isEditingUrl,
                urlInput = urlInput,
                onUrlInputChange = { viewModel.updateUrlInput(it) },
                onStartEditingUrl = { viewModel.setIsEditingUrl(true) },
                onSubmitUrl = { viewModel.loadUrl(it) },
                onCancelEditingUrl = { viewModel.cancelEditingUrl() },
                onReload = { viewModel.reload() },
                onStop = { viewModel.stop() },
                onToggleBookmark = { viewModel.toggleBookmarkCurrentTab() },
                isBookmarked = isBookmarked,
                onToggleDesktop = { viewModel.toggleDesktopMode() },
                onOpenBookmarks = { viewModel.setBookmarksSheetVisible(true) },
                onOpenHistory = { viewModel.setHistorySheetVisible(true) },
                onOpenDownloads = { viewModel.setDownloadsSheetVisible(true) },
                onOpenSettings = { viewModel.setSettingsSheetVisible(true) },
                onOpenNewTab = { isIncognito -> viewModel.createNewTab(isIncognito = isIncognito) },
                onShareUrl = {
                    if (activeTab.url.isNotBlank()) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, activeTab.url)
                            putExtra(Intent.EXTRA_SUBJECT, activeTab.title)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
                    }
                }
            )
        },
        bottomBar = {
            BrowserBottomBar(
                enabledItems = bottomBarItems,
                canGoBack = activeTab.canGoBack,
                canGoForward = activeTab.canGoForward,
                tabCount = tabs.size,
                isHome = activeTab.isHome,
                themeColor = activeTab.themeColor,
                onBack = { viewModel.goBack() },
                onForward = { viewModel.goForward() },
                onNewTab = { viewModel.createNewTab() },
                onOpenDownloads = { viewModel.setDownloadsSheetVisible(true) },
                onOpenTabSwitcher = { viewModel.setTabSwitcherVisible(true) },
                onOpenBookmarks = { viewModel.setBookmarksSheetVisible(true) },
                onOpenHistory = { viewModel.setHistorySheetVisible(true) },
                onGoHome = { viewModel.goHome() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTab.id,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.96f, animationSpec = tween(200)))
                        .togetherWith(fadeOut(animationSpec = tween(150)))
                },
                label = "tab_switch_transition",
                modifier = Modifier.fillMaxSize()
            ) { targetTabId ->
                val tabToRender = tabs.find { it.id == targetTabId } ?: activeTab
                if (tabToRender.isHome) {
                    HomepageView(
                        userName = viewModel.preferences.userName,
                        searchEngine = searchEngine,
                        shortcuts = shortcuts,
                        recentHistory = history,
                        bookmarks = bookmarks,
                        showShortcuts = showShortcuts,
                        showRecentHistory = showRecentHistory,
                        wallpaperUri = wallpaperUri,
                        isWallpaperBlurEnabled = isWallpaperBlurEnabled,
                        homeIconUri = homeIconUri,
                        onSearchClick = { viewModel.setIsEditingUrl(true) },
                        onShortcutClick = { url -> viewModel.loadUrl(url) },
                        onAddShortcutClick = { viewModel.setAddShortcutDialogVisible(true) },
                        onHistoryItemClick = { url -> viewModel.loadUrl(url) },
                        onBookmarkItemClick = { url -> viewModel.loadUrl(url) },
                        onDeleteHistoryItem = { item -> viewModel.deleteHistoryItem(item) },
                        onOpenDownloads = { viewModel.setDownloadsSheetVisible(true) },
                        onOpenBookmarks = { viewModel.setBookmarksSheetVisible(true) },
                        onOpenHistory = { viewModel.setHistorySheetVisible(true) }
                    )
                } else if (tabToRender.url == "aurabrowser://downloads") {
                    DownloadsTabContent(
                        downloads = downloads,
                        onRefresh = { coroutineScope.launch { viewModel.refreshDownloads() } },
                        onDeleteDownload = { viewModel.removeDownload(it) }
                    )
                } else {
                    BrowserWebView(
                        activeTabId = tabToRender.id,
                        activeTabUrl = tabToRender.url,
                        isActiveTabHome = tabToRender.isHome,
                        isActiveTabIncognito = tabToRender.isIncognito,
                        isActiveTabDesktopMode = tabToRender.isDesktopMode,
                        isActiveTabOffline = tabToRender.isOffline,
                        activeTabErrorMessage = tabToRender.errorMessage,
                        isActiveTabLoading = tabToRender.isLoading,
                        webViewPoolManager = viewModel.webViewPoolManager,
                        adBlockEngine = viewModel.adBlockEngine,
                        downloadTracker = viewModel.downloadTracker,
                        isAdBlockEnabled = isAdBlockEnabled,
                        isJavaScriptEnabled = isJavaScriptEnabled,
                        isDoNotTrack = isDoNotTrackEnabled,
                        webAction = webAction,
                        onActionConsumed = { viewModel.consumeWebAction() },
                        onPageStarted = { tabId, url -> viewModel.onPageStarted(tabId, url) },
                        onPageFinished = { tabId, url, title, canGoBack, canGoForward ->
                            viewModel.onPageFinished(tabId, url, title, canGoBack, canGoForward)
                        },
                        onProgressChanged = { tabId, progress -> viewModel.onProgressChanged(tabId, progress) },
                        onReceivedError = { tabId, desc, isOffline -> viewModel.onReceivedError(tabId, desc, isOffline) },
                        onFaviconReceived = { tabId, favicon -> viewModel.onFaviconReceived(tabId, favicon) },
                        onThemeColorReceived = { tabId, color -> viewModel.onThemeColorReceived(tabId, color) },
                        onShowFileChooser = onShowFileChooser,
                        onRetry = { viewModel.goHome() }
                    )
                }
            }
        }
    }

    // Tab Switcher Bottom Sheet
    if (showTabSwitcher) {
        TabSwitcherView(
            tabs = tabs,
            activeTabId = activeTabId,
            layoutMode = tabSwitcherLayout,
            onSelectTab = { viewModel.selectTab(it) },
            onCloseTab = { viewModel.closeTab(it) },
            onNewTab = { isIncognito -> viewModel.createNewTab(isIncognito = isIncognito) },
            onDismiss = { viewModel.setTabSwitcherVisible(false) }
        )
    }

    // Bookmarks Sheet
    if (showBookmarksSheet) {
        BookmarksView(
            bookmarks = bookmarks,
            onSelectBookmark = { url ->
                viewModel.loadUrl(url)
                viewModel.setBookmarksSheetVisible(false)
            },
            onDeleteBookmark = { viewModel.deleteBookmark(it) },
            onDismiss = { viewModel.setBookmarksSheetVisible(false) }
        )
    }

    // History Sheet
    if (showHistorySheet) {
        HistoryView(
            history = history,
            onSelectHistory = { url ->
                viewModel.loadUrl(url)
                viewModel.setHistorySheetVisible(false)
            },
            onDeleteHistory = { viewModel.deleteHistoryItem(it) },
            onClearAllHistory = { viewModel.clearAllHistory() },
            onDismiss = { viewModel.setHistorySheetVisible(false) }
        )
    }

    // Downloads Sheet
    if (showDownloadsSheet) {
        DownloadsView(
            downloads = downloads,
            onRefresh = { coroutineScope.launch { viewModel.refreshDownloads() } },
            onDeleteDownload = { viewModel.removeDownload(it) },
            onOpenAsTab = { viewModel.openDownloadsTab() },
            onDismiss = { viewModel.setDownloadsSheetVisible(false) }
        )
    }

    // Settings Sheet
    if (showSettingsSheet) {
        SettingsView(
            currentSearchEngine = searchEngine,
            onSelectSearchEngine = { viewModel.updateSearchEngine(it) },
            userName = viewModel.preferences.userName,
            onUpdateUserName = { viewModel.updateUserName(it) },
            themeMode = themeMode,
            onSelectThemeMode = { viewModel.updateThemeMode(it) },
            accentColor = accentColor,
            onSelectAccentColor = { viewModel.updateAccentColor(it) },
            showShortcuts = showShortcuts,
            onToggleShowShortcuts = { viewModel.updateShowShortcuts(it) },
            showRecentHistory = showRecentHistory,
            onToggleShowRecentHistory = { viewModel.updateShowRecentHistory(it) },
            tabSwitcherLayout = tabSwitcherLayout,
            onSelectTabSwitcherLayout = { viewModel.updateTabSwitcherLayout(it) },
            bottomBarItems = bottomBarItems,
            onUpdateBottomBarItems = { viewModel.updateBottomBarItems(it) },
            wallpaperUri = wallpaperUri,
            isWallpaperBlurEnabled = isWallpaperBlurEnabled,
            onUpdateWallpaperUri = { viewModel.updateWallpaperUri(it) },
            onUpdateWallpaperBlur = { viewModel.updateWallpaperBlur(it) },
            homeIconUri = homeIconUri,
            onUpdateHomeIconUri = { viewModel.updateHomeIconUri(it) },
            isAdBlockEnabled = isAdBlockEnabled,
            onToggleAdBlock = { viewModel.updateAdBlock(it) },
            isDesktopModeDefault = isDesktopModeDefault,
            onToggleDesktopDefault = { viewModel.updateDesktopModeDefault(it) },
            isJavaScriptEnabled = isJavaScriptEnabled,
            onToggleJavaScript = { viewModel.updateJavaScript(it) },
            isDoNotTrack = isDoNotTrackEnabled,
            onToggleDoNotTrack = { viewModel.updateDoNotTrack(it) },
            dnsProvider = dnsProvider,
            dnsCustomValue = dnsCustomValue,
            onUpdateDnsProvider = { viewModel.updateDnsProvider(it) },
            onUpdateDnsCustomValue = { viewModel.updateDnsCustomValue(it) },
            onClearBrowsingData = { clearCache, clearHistory, clearCookies ->
                viewModel.clearBrowsingData(clearCache, clearHistory, clearCookies)
            },
            onDismiss = { viewModel.setSettingsSheetVisible(false) }
        )
    }

    // Add Shortcut Dialog
    if (showAddShortcutDialog) {
        AddShortcutDialog(
            onAddShortcut = { title, url ->
                viewModel.addShortcut(title, url)
            },
            onDismiss = { viewModel.setAddShortcutDialogVisible(false) }
        )
    }
}
