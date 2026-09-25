package com.aryaxzell.aurabrowser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aryaxzell.aurabrowser.ui.components.AddShortcutDialog
import com.aryaxzell.aurabrowser.ui.components.BookmarksView
import com.aryaxzell.aurabrowser.ui.components.BrowserBottomBar
import com.aryaxzell.aurabrowser.ui.components.BrowserTopBar
import com.aryaxzell.aurabrowser.ui.components.BrowserWebView
import com.aryaxzell.aurabrowser.ui.components.HistoryView
import com.aryaxzell.aurabrowser.ui.components.HomepageView
import com.aryaxzell.aurabrowser.ui.components.SettingsView
import com.aryaxzell.aurabrowser.ui.components.TabSwitcherView
import com.aryaxzell.aurabrowser.ui.theme.MyApplicationTheme
import com.aryaxzell.aurabrowser.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle external VIEW intent (e.g. clicked link from another app)
        handleIntent(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                BrowserApp(viewModel = viewModel)
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
fun BrowserApp(viewModel: BrowserViewModel) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

    // Reactive preferences
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val isAdBlockEnabled by viewModel.isAdBlockEnabled.collectAsStateWithLifecycle()
    val isDesktopModeDefault by viewModel.isDesktopModeDefault.collectAsStateWithLifecycle()
    val isJavaScriptEnabled by viewModel.isJavaScriptEnabled.collectAsStateWithLifecycle()
    val isDoNotTrackEnabled by viewModel.isDoNotTrackEnabled.collectAsStateWithLifecycle()

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle()

    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val isEditingUrl by viewModel.isEditingUrl.collectAsStateWithLifecycle()

    val showTabSwitcher by viewModel.showTabSwitcher.collectAsStateWithLifecycle()
    val showBookmarksSheet by viewModel.showBookmarksSheet.collectAsStateWithLifecycle()
    val showHistorySheet by viewModel.showHistorySheet.collectAsStateWithLifecycle()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsStateWithLifecycle()
    val showAddShortcutDialog by viewModel.showAddShortcutDialog.collectAsStateWithLifecycle()

    val webAction by viewModel.webAction.collectAsStateWithLifecycle()
    val isBookmarked = bookmarks.any { it.url == activeTab.url && activeTab.url.isNotBlank() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            BrowserTopBar(
                activeTab = activeTab,
                isEditingUrl = isEditingUrl,
                urlInput = urlInput,
                onUrlInputChange = { viewModel.setUrlInput(it) },
                onStartEditingUrl = { viewModel.setIsEditingUrl(true) },
                onSubmitUrl = { viewModel.submitQueryOrUrl(it) },
                onCancelEditingUrl = { viewModel.setIsEditingUrl(false) },
                onReload = { viewModel.reload() },
                onStop = { viewModel.stopLoading() },
                onToggleBookmark = { viewModel.toggleBookmark() },
                isBookmarked = isBookmarked,
                onToggleDesktop = { viewModel.toggleDesktopMode() },
                onOpenBookmarks = { viewModel.setBookmarksSheetVisible(true) },
                onOpenHistory = { viewModel.setHistorySheetVisible(true) },
                onOpenSettings = { viewModel.setSettingsSheetVisible(true) },
                onOpenNewTab = { isIncognito -> viewModel.createNewTab(isIncognito = isIncognito) },
                onShareUrl = {
                    if (activeTab.url.isNotBlank()) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, activeTab.url)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share link via")
                        context.startActivity(shareIntent)
                    }
                }
            )
        },
        bottomBar = {
            BrowserBottomBar(
                canGoBack = activeTab.canGoBack,
                canGoForward = activeTab.canGoForward,
                tabCount = tabs.size,
                isHome = activeTab.isHome,
                onBack = { viewModel.goBack() },
                onForward = { viewModel.goForward() },
                onNewTab = { viewModel.createNewTab() },
                onOpenTabSwitcher = { viewModel.setTabSwitcherVisible(true) },
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
                label = "tab_switch_transition"
            ) { targetTabId ->
                val tabToRender = tabs.find { it.id == targetTabId } ?: activeTab
                if (tabToRender.isHome) {
                    HomepageView(
                        userName = viewModel.preferences.userName,
                        searchEngine = searchEngine,
                        shortcuts = shortcuts,
                        recentHistory = history,
                        bookmarks = bookmarks,
                        onSearchClick = { viewModel.setIsEditingUrl(true) },
                        onShortcutClick = { url -> viewModel.loadUrl(url) },
                        onAddShortcutClick = { viewModel.setAddShortcutDialogVisible(true) },
                        onHistoryItemClick = { url -> viewModel.loadUrl(url) },
                        onBookmarkItemClick = { url -> viewModel.loadUrl(url) }
                    )
                } else {
                    BrowserWebView(
                        activeTab = tabToRender,
                        webViewPoolManager = viewModel.webViewPoolManager,
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

    // Settings Sheet
    if (showSettingsSheet) {
        SettingsView(
            currentSearchEngine = searchEngine,
            onSelectSearchEngine = { viewModel.updateSearchEngine(it) },
            userName = viewModel.preferences.userName,
            onUpdateUserName = { viewModel.updateUserName(it) },
            themeMode = themeMode,
            onSelectThemeMode = { viewModel.updateThemeMode(it) },
            isAdBlockEnabled = isAdBlockEnabled,
            onToggleAdBlock = { viewModel.updateAdBlock(it) },
            isDesktopModeDefault = isDesktopModeDefault,
            onToggleDesktopDefault = { viewModel.updateDesktopModeDefault(it) },
            isJavaScriptEnabled = isJavaScriptEnabled,
            onToggleJavaScript = { viewModel.updateJavaScript(it) },
            isDoNotTrack = isDoNotTrackEnabled,
            onToggleDoNotTrack = { viewModel.updateDoNotTrack(it) },
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
