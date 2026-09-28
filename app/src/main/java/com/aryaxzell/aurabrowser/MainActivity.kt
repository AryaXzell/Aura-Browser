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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aryaxzell.aurabrowser.data.model.WhatsNewData
import com.aryaxzell.aurabrowser.ui.components.AddShortcutDialog
import com.aryaxzell.aurabrowser.ui.components.BookmarksView
import com.aryaxzell.aurabrowser.ui.components.BrowserBottomBar
import com.aryaxzell.aurabrowser.ui.components.BrowserTopBar
import com.aryaxzell.aurabrowser.ui.components.BrowserWebView
import com.aryaxzell.aurabrowser.ui.components.DownloadsTabContent
import com.aryaxzell.aurabrowser.ui.components.DownloadsView
import com.aryaxzell.aurabrowser.ui.components.HistoryView
import com.aryaxzell.aurabrowser.ui.components.HomepageView
import com.aryaxzell.aurabrowser.ui.components.OnboardingView
import com.aryaxzell.aurabrowser.ui.components.SettingsView
import com.aryaxzell.aurabrowser.ui.components.TabSwitcherView
import com.aryaxzell.aurabrowser.ui.theme.MyApplicationTheme
import com.aryaxzell.aurabrowser.viewmodel.BrowserViewModel
import kotlinx.coroutines.launch

import com.aryaxzell.aurabrowser.ui.components.ConsoleLogView
import com.aryaxzell.aurabrowser.ui.components.NetworkLogView
import com.aryaxzell.aurabrowser.ui.components.PageSourceView
import com.aryaxzell.aurabrowser.ui.components.ResourceTiming
import com.aryaxzell.aurabrowser.ui.components.SiteInfoSheet

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

        // Pre-warm the WebView pool asynchronously during idle state to reduce tab creation latency by 500ms on low-end devices
        viewModel.webViewPoolManager.prewarmWebView(this)

        // Initialize static assets MemoryCache with activity context to dynamically tune size based on RAM constraints
        com.aryaxzell.aurabrowser.data.cache.MemoryCache.initialize(this)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val lastSeenVersionCode by viewModel.lastSeenOnboardingVersionCode.collectAsStateWithLifecycle()

            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            val shouldShowOnboarding = remember(lastSeenVersionCode) {
                (lastSeenVersionCode == 0) || (
                    lastSeenVersionCode < BuildConfig.VERSION_CODE &&
                    WhatsNewData.entries.any { it.minVersionCode > lastSeenVersionCode && it.minVersionCode <= BuildConfig.VERSION_CODE }
                )
            }

            // Sinkronkan warna ikon status bar & navigation bar dengan tema aktif
            LaunchedEffect(isDarkTheme) {
                val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !isDarkTheme
                insetsController.isAppearanceLightNavigationBars = !isDarkTheme
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                accentColor = accentColor
            ) {
                AnimatedContent(
                    targetState = shouldShowOnboarding,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.96f, animationSpec = tween(200)))
                            .togetherWith(fadeOut(animationSpec = tween(150)))
                    },
                    label = "onboarding_handoff_transition"
                ) { showOnboarding ->
                    if (showOnboarding) {
                        OnboardingView(
                            lastSeenVersionCode = lastSeenVersionCode,
                            currentThemeMode = themeMode,
                            currentAccentColor = accentColor,
                            currentLanguage = appLanguage,
                            onSelectThemeMode = { viewModel.updateThemeMode(it) },
                            onSelectAccentColor = { viewModel.updateAccentColor(it) },
                            onSelectLanguage = { viewModel.updateAppLanguage(it) },
                            onCompleteOnboarding = {
                                viewModel.updateLastSeenOnboardingVersionCode(BuildConfig.VERSION_CODE)
                            }
                        )
                    } else {
                        BrowserApp(
                            viewModel = viewModel,
                            onShowFileChooser = { intent, callback -> launchFileChooser(intent, callback) }
                        )
                    }
                }
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
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
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
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()

    val isDeveloperMode by viewModel.isDeveloperMode.collectAsStateWithLifecycle()
    val isRemoteDebugging by viewModel.isRemoteDebugging.collectAsStateWithLifecycle()
    val isLinkPreviewEnabled by viewModel.isLinkPreviewEnabled.collectAsStateWithLifecycle()
    val linkPreviewTarget by viewModel.linkPreviewTarget.collectAsStateWithLifecycle()
    val siteSettingsMap by viewModel.siteSettingsMap.collectAsStateWithLifecycle()
    val securityInfoMap by viewModel.securityInfoMap.collectAsStateWithLifecycle()
    val networkLogTrigger by viewModel.networkLogUpdateTrigger.collectAsStateWithLifecycle()

    val showTabSwitcher by viewModel.showTabSwitcher.collectAsStateWithLifecycle()
    val showBookmarksSheet by viewModel.showBookmarksSheet.collectAsStateWithLifecycle()
    val showHistorySheet by viewModel.showHistorySheet.collectAsStateWithLifecycle()
    val showDownloadsSheet by viewModel.showDownloadsSheet.collectAsStateWithLifecycle()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsStateWithLifecycle()
    val showAddShortcutDialog by viewModel.showAddShortcutDialog.collectAsStateWithLifecycle()

    val showSiteInfoSheet by viewModel.showSiteInfoSheet.collectAsStateWithLifecycle()
    val showPageSourceView by viewModel.showPageSourceView.collectAsStateWithLifecycle()
    val showNetworkLogView by viewModel.showNetworkLogView.collectAsStateWithLifecycle()
    val showConsoleLogView by viewModel.showConsoleLogView.collectAsStateWithLifecycle()

    val webAction by viewModel.webAction.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()

    val bookmarkedUrls = remember(bookmarks) {
        bookmarks.map { it.url }.toSet()
    }
    val isBookmarked = remember(activeTab.url, bookmarkedUrls) {
        activeTab.url.isNotBlank() && bookmarkedUrls.contains(activeTab.url)
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
                securityInfo = securityInfoMap[activeTab.id],
                isDeveloperMode = isDeveloperMode,
                onUrlInputChange = { viewModel.updateUrlInput(it) },
                onStartEditingUrl = { viewModel.setIsEditingUrl(true) },
                onSubmitUrl = { viewModel.submitQueryOrUrl(it) },
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
                },
                onOpenSiteInfo = { viewModel.setShowSiteInfoSheet(true) },
                onOpenPageSource = { viewModel.setShowPageSourceView(true) },
                onOpenNetworkLog = { viewModel.setShowNetworkLogView(true) },
                onOpenConsoleLog = { viewModel.setShowConsoleLogView(true) }
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
                        isDeveloperMode = isDeveloperMode,
                        isRemoteDebugging = isRemoteDebugging,
                        siteSettingsMap = siteSettingsMap,
                        webAction = webAction,
                        onActionConsumed = { viewModel.consumeWebAction() },
                        onPageStarted = { tabId, url -> viewModel.onPageStarted(tabId, url) },
                        onPageFinished = { tabId, url, title, canGoBack, canGoForward ->
                            viewModel.onPageFinished(tabId, url, title, canGoBack, canGoForward)
                        },
                        onReceivedTitle = { tabId, title ->
                            viewModel.onReceivedTitle(tabId, title)
                        },
                        onProgressChanged = { tabId, progress -> viewModel.onProgressChanged(tabId, progress) },
                        onReceivedError = { tabId, desc, isOffline -> viewModel.onReceivedError(tabId, desc, isOffline) },
                        onFaviconReceived = { tabId, favicon -> viewModel.onFaviconReceived(tabId, favicon) },
                        onThemeColorReceived = { tabId, color -> viewModel.onThemeColorReceived(tabId, color) },
                        onUpdateSecurityInfo = { tabId, info -> viewModel.updateSecurityInfo(tabId, info) },
                        onAddNetworkLogEntry = { tabId, entry -> viewModel.addNetworkLogEntry(tabId, entry) },
                        onAddConsoleLogEntry = { tabId, entry -> viewModel.addConsoleLogEntry(tabId, entry) },
                        onLinkLongPress = { targetUrl, isIncognito ->
                            viewModel.showLinkPreview(targetUrl, isIncognito)
                        },
                        onShowFileChooser = onShowFileChooser,
                        onRetry = { viewModel.goHome() }
                    )
                }
            }

            // Floating Search Suggestions
            if (isEditingUrl && searchSuggestions.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)) // dim background
                        .clickable { viewModel.setIsEditingUrl(false) } // click outside to close
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.TopCenter)
                            .clickable(enabled = false) {}, // prevent clicks propagating through card
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            searchSuggestions.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.submitQueryOrUrl(suggestion)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
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

    val slideIn = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
    val slideOut = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))

    // Bookmarks Sheet
    AnimatedVisibility(
        visible = showBookmarksSheet,
        enter = slideIn,
        exit = slideOut
    ) {
        BookmarksView(
            bookmarks = bookmarks,
            onSelectBookmark = { url ->
                viewModel.loadUrl(url)
                viewModel.setBookmarksSheetVisible(false)
            },
            onDeleteBookmark = { viewModel.deleteBookmark(it) },
            onSwitchToHistory = {
                viewModel.setBookmarksSheetVisible(false)
                viewModel.setHistorySheetVisible(true)
            },
            onSwitchToDownloads = {
                viewModel.setBookmarksSheetVisible(false)
                viewModel.setDownloadsSheetVisible(true)
            },
            onDismiss = { viewModel.setBookmarksSheetVisible(false) }
        )
    }

    // History Sheet
    AnimatedVisibility(
        visible = showHistorySheet,
        enter = slideIn,
        exit = slideOut
    ) {
        HistoryView(
            history = history,
            onSelectHistory = { url ->
                viewModel.loadUrl(url)
                viewModel.setHistorySheetVisible(false)
            },
            onDeleteHistory = { viewModel.deleteHistoryItem(it) },
            onClearAllHistory = { viewModel.clearAllHistory() },
            onSwitchToBookmarks = {
                viewModel.setHistorySheetVisible(false)
                viewModel.setBookmarksSheetVisible(true)
            },
            onSwitchToDownloads = {
                viewModel.setHistorySheetVisible(false)
                viewModel.setDownloadsSheetVisible(true)
            },
            onDismiss = { viewModel.setHistorySheetVisible(false) }
        )
    }

    // Downloads Sheet
    AnimatedVisibility(
        visible = showDownloadsSheet,
        enter = slideIn,
        exit = slideOut
    ) {
        DownloadsView(
            downloads = downloads,
            onRefresh = { coroutineScope.launch { viewModel.refreshDownloads() } },
            onDeleteDownload = { viewModel.removeDownload(it) },
            onOpenAsTab = { viewModel.openDownloadsTab() },
            onSwitchToBookmarks = {
                viewModel.setDownloadsSheetVisible(false)
                viewModel.setBookmarksSheetVisible(true)
            },
            onSwitchToHistory = {
                viewModel.setDownloadsSheetVisible(false)
                viewModel.setHistorySheetVisible(true)
            },
            onDismiss = { viewModel.setDownloadsSheetVisible(false) }
        )
    }

    // Settings Sheet
    AnimatedVisibility(
        visible = showSettingsSheet,
        enter = slideIn,
        exit = slideOut
    ) {
        SettingsView(
            currentLanguage = appLanguage,
            onSelectLanguage = { viewModel.updateAppLanguage(it) },
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
            isLinkPreviewEnabled = isLinkPreviewEnabled,
            onToggleLinkPreview = { viewModel.updateLinkPreviewEnabled(it) },
            isDoNotTrack = isDoNotTrackEnabled,
            onToggleDoNotTrack = { viewModel.updateDoNotTrack(it) },
            dnsProvider = dnsProvider,
            dnsCustomValue = dnsCustomValue,
            onUpdateDnsProvider = { viewModel.updateDnsProvider(it) },
            onUpdateDnsCustomValue = { viewModel.updateDnsCustomValue(it) },
            isDeveloperMode = isDeveloperMode,
            isRemoteDebugging = isRemoteDebugging,
            onSetDeveloperMode = { viewModel.setDeveloperMode(it) },
            onSetRemoteDebugging = { viewModel.setRemoteDebugging(it) },
            onClearBrowsingData = { clearCache, clearHistory, clearCookies ->
                viewModel.clearBrowsingData(clearCache, clearHistory, clearCookies)
            },
            onDismiss = { viewModel.setSettingsSheetVisible(false) }
        )
    }

    // Site Info Sheet
    if (showSiteInfoSheet) {
        val activeHost = remember(activeTab.url) {
            try {
                Uri.parse(activeTab.url).host?.lowercase()?.removePrefix("www.") ?: ""
            } catch (e: Exception) { "" }
        }

        SiteInfoSheet(
            url = activeTab.url,
            securityInfo = securityInfoMap[activeTab.id],
            siteSettings = siteSettingsMap[activeHost],
            onUpdateSiteSettings = { js, desktop, adBlock ->
                viewModel.updateSiteSettings(activeHost, js, desktop, adBlock)
            },
            onResetSiteSettings = {
                viewModel.resetSiteSettings(activeHost)
            },
            onReloadPage = {
                viewModel.reload()
            },
            onDismiss = { viewModel.setShowSiteInfoSheet(false) }
        )
    }

    // Page Source View
    if (showPageSourceView) {
        var renderedDom by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            val safeWebView = viewModel.webViewPoolManager.getWebView(activeTab.id)
            safeWebView?.evaluateJavascript("document.documentElement.outerHTML") { rawResult: String? ->
                if (!rawResult.isNullOrBlank() && rawResult != "null") {
                    try {
                        val clean = org.json.JSONTokener(rawResult).nextValue().toString()
                        renderedDom = clean
                    } catch (e: Exception) {
                        renderedDom = rawResult
                    }
                }
            }
        }

        PageSourceView(
            url = activeTab.url,
            isDesktopMode = activeTab.isDesktopMode,
            renderedHtml = renderedDom,
            onDismiss = { viewModel.setShowPageSourceView(false) }
        )
    }

    // Network Log View
    if (showNetworkLogView) {
        NetworkLogView(
            logs = remember(networkLogTrigger, activeTab.id) { viewModel.getNetworkLogs(activeTab.id) },
            onRequestResourceTimings = { onResult ->
                val safeWebView = viewModel.webViewPoolManager.getWebView(activeTab.id)
                safeWebView?.evaluateJavascript(
                    """
                    (function() {
                        var entries = performance.getEntriesByType('resource');
                        return JSON.stringify(entries.map(function(e) {
                            return {
                                name: e.name,
                                durationMs: Math.round(e.duration),
                                transferSize: e.transferSize || null,
                                protocol: e.nextHopProtocol || null,
                                initiatorType: e.initiatorType || null
                            };
                        }));
                    })()
                    """.trimIndent()
                ) { rawResult: String? ->
                    if (!rawResult.isNullOrBlank() && rawResult != "null") {
                        try {
                            val cleanJson = org.json.JSONTokener(rawResult).nextValue().toString()
                            val array = org.json.JSONArray(cleanJson)
                            val list = mutableListOf<ResourceTiming>()
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                list.add(
                                    ResourceTiming(
                                        name = obj.optString("name"),
                                        durationMs = if (obj.has("durationMs") && !obj.isNull("durationMs")) obj.getLong("durationMs") else null,
                                        transferSize = if (obj.has("transferSize") && !obj.isNull("transferSize")) obj.getLong("transferSize") else null,
                                        protocol = obj.optString("protocol").takeIf { it.isNotBlank() },
                                        initiatorType = obj.optString("initiatorType").takeIf { it.isNotBlank() }
                                    )
                                )
                            }
                            onResult(list)
                        } catch (e: Exception) {
                            onResult(emptyList())
                        }
                    } else {
                        onResult(emptyList())
                    }
                }
            },
            onClearLogs = { viewModel.clearNetworkLogs(activeTab.id) },
            onDismiss = { viewModel.setShowNetworkLogView(false) }
        )
    }

    // Console Log View
    if (showConsoleLogView) {
        ConsoleLogView(
            logs = remember(activeTab.id) { viewModel.getConsoleLogs(activeTab.id) },
            onClearLogs = { viewModel.clearConsoleLogs(activeTab.id) },
            onDismiss = { viewModel.setShowConsoleLogView(false) }
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

    // Link Preview Overlay & Context Menu
    linkPreviewTarget?.let { target ->
        val targetIsBookmarked = remember(bookmarks, target.url) {
            bookmarks.any { it.url == target.url }
        }

        com.aryaxzell.aurabrowser.ui.components.LinkPreviewOverlay(
            target = target,
            isLinkPreviewEnabled = isLinkPreviewEnabled,
            isBookmarked = targetIsBookmarked,
            adBlockEngine = viewModel.adBlockEngine,
            isAdBlockEnabled = isAdBlockEnabled,
            isDoNotTrack = isDoNotTrackEnabled,
            onOpen = {
                viewModel.loadUrl(target.url)
                viewModel.dismissLinkPreview()
            },
            onOpenInNewTab = {
                viewModel.createNewTab(url = target.url, isIncognito = target.isSourceIncognito)
                viewModel.dismissLinkPreview()
            },
            onOpenInIncognitoTab = {
                viewModel.createNewTab(url = target.url, isIncognito = true)
                viewModel.dismissLinkPreview()
            },
            onDownload = {
                viewModel.downloadTracker.enqueueDownload(url = target.url)
                viewModel.dismissLinkPreview()
            },
            onToggleBookmark = {
                viewModel.toggleBookmarkForUrl(target.url, target.title) { isAdded ->
                    android.widget.Toast.makeText(
                        context,
                        if (isAdded) "Added to bookmarks" else "Removed from bookmarks",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.dismissLinkPreview()
            },
            onCopyLink = {
                com.aryaxzell.aurabrowser.data.util.ClipboardUtil.copyToClipboard(context, "Link", target.url)
                viewModel.dismissLinkPreview()
            },
            onShare = {
                com.aryaxzell.aurabrowser.data.util.ShareUtil.shareUrl(context, target.url, target.title)
                viewModel.dismissLinkPreview()
            },
            onDismiss = {
                viewModel.dismissLinkPreview()
            }
        )
    }
}
