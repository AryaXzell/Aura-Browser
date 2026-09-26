package com.aryaxzell.aurabrowser.viewmodel

import android.app.Application
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aryaxzell.aurabrowser.data.adblock.AdBlockEngine
import com.aryaxzell.aurabrowser.data.db.BookmarkEntity
import com.aryaxzell.aurabrowser.data.db.BrowserDatabase
import com.aryaxzell.aurabrowser.data.db.HistoryEntity
import com.aryaxzell.aurabrowser.data.download.DownloadTracker
import com.aryaxzell.aurabrowser.data.model.DownloadItem
import com.aryaxzell.aurabrowser.data.model.DownloadStatus
import com.aryaxzell.aurabrowser.data.model.SearchEngine
import com.aryaxzell.aurabrowser.data.model.ShortcutItem
import com.aryaxzell.aurabrowser.data.model.TabItem
import com.aryaxzell.aurabrowser.data.preferences.BrowserPreferences
import com.aryaxzell.aurabrowser.webview.WebViewPoolManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val db = BrowserDatabase.getInstance(application)
    private val dao = db.browserDao()
    val preferences = BrowserPreferences(application)
    val webViewPoolManager = WebViewPoolManager()

    // Expose preferences as reactive StateFlows
    val searchEngine: StateFlow<SearchEngine> = preferences.searchEngineFlow
    val themeMode: StateFlow<String> = preferences.themeModeFlow
    val accentColor: StateFlow<String> = preferences.accentColorFlow
    val showShortcuts: StateFlow<Boolean> = preferences.showShortcutsFlow
    val showRecentHistory: StateFlow<Boolean> = preferences.showRecentHistoryFlow
    val tabSwitcherLayout: StateFlow<String> = preferences.tabSwitcherLayoutFlow
    val bottomBarItems: StateFlow<List<String>> = preferences.bottomBarItemsFlow
    val wallpaperUri: StateFlow<String?> = preferences.wallpaperUriFlow
    val isWallpaperBlurEnabled: StateFlow<Boolean> = preferences.isWallpaperBlurEnabledFlow
    val homeIconUri: StateFlow<String?> = preferences.homeIconUriFlow
    val isAdBlockEnabled: StateFlow<Boolean> = preferences.isAdBlockEnabledFlow
    val isDesktopModeDefault: StateFlow<Boolean> = preferences.isDesktopModeDefaultFlow
    val isJavaScriptEnabled: StateFlow<Boolean> = preferences.isJavaScriptEnabledFlow
    val isDoNotTrackEnabled: StateFlow<Boolean> = preferences.isDoNotTrackEnabledFlow

    private val initialTab = TabItem(
        id = java.util.UUID.randomUUID().toString(),
        title = "Home",
        url = "",
        isHome = true,
        isDesktopMode = preferences.isDesktopModeDefault
    )

    private val _tabs = MutableStateFlow<List<TabItem>>(listOf(initialTab))
    val tabs: StateFlow<List<TabItem>> = _tabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(initialTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    // Reactive activeTab derived from _tabs and _activeTabId
    val activeTab: StateFlow<TabItem> = combine(_tabs, _activeTabId) { currentTabs, currentId ->
        currentTabs.find { it.id == currentId } ?: currentTabs.firstOrNull() ?: initialTab
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialTab
    )

    val bookmarks: StateFlow<List<BookmarkEntity>> = dao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _shortcuts = MutableStateFlow<List<ShortcutItem>>(emptyList())
    val shortcuts: StateFlow<List<ShortcutItem>> = _shortcuts.asStateFlow()

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _isEditingUrl = MutableStateFlow(false)
    val isEditingUrl: StateFlow<Boolean> = _isEditingUrl.asStateFlow()

    // Sheet / Dialog states
    private val _showTabSwitcher = MutableStateFlow(false)
    val showTabSwitcher: StateFlow<Boolean> = _showTabSwitcher.asStateFlow()

    private val _showBookmarksSheet = MutableStateFlow(false)
    val showBookmarksSheet: StateFlow<Boolean> = _showBookmarksSheet.asStateFlow()

    private val _showHistorySheet = MutableStateFlow(false)
    val showHistorySheet: StateFlow<Boolean> = _showHistorySheet.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private val _showDownloadsSheet = MutableStateFlow(false)
    val showDownloadsSheet: StateFlow<Boolean> = _showDownloadsSheet.asStateFlow()

    val downloadTracker = DownloadTracker(application)
    val adBlockEngine = AdBlockEngine(application)
    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private var downloadPollingJob: Job? = null

    init {
        // Load shortcuts and downloads asynchronously on IO thread to keep cold start UI instant
        viewModelScope.launch(Dispatchers.IO) {
            adBlockEngine.loadIfNeeded()
            val loadedShortcuts = preferences.getShortcuts()
            _shortcuts.value = loadedShortcuts
            refreshDownloads()
        }
    }

    private val _showAddShortcutDialog = MutableStateFlow(false)
    val showAddShortcutDialog: StateFlow<Boolean> = _showAddShortcutDialog.asStateFlow()

    private val _showMoreMenu = MutableStateFlow(false)
    val showMoreMenu: StateFlow<Boolean> = _showMoreMenu.asStateFlow()

    // WebView commands
    sealed class WebAction {
        data class LoadUrl(val url: String) : WebAction()
        object Reload : WebAction()
        object Stop : WebAction()
        object GoBack : WebAction()
        object GoForward : WebAction()
    }

    private val _webAction = MutableStateFlow<WebAction?>(null)
    val webAction: StateFlow<WebAction?> = _webAction.asStateFlow()

    fun consumeWebAction() {
        _webAction.value = null
    }

    fun getActiveTab(): TabItem {
        val currentTabs = _tabs.value
        val currentId = _activeTabId.value
        return currentTabs.find { it.id == currentId } ?: currentTabs.firstOrNull() ?: initialTab
    }

    fun setUrlInput(input: String) {
        _urlInput.value = input
    }

    fun updateUrlInput(input: String) {
        setUrlInput(input)
    }

    fun setIsEditingUrl(isEditing: Boolean) {
        _isEditingUrl.value = isEditing
        if (isEditing) {
            val active = getActiveTab()
            _urlInput.value = if (active.isHome) "" else active.url
        }
    }

    fun cancelEditingUrl() {
        setIsEditingUrl(false)
    }

    fun submitQueryOrUrl(queryOrUrl: String) {
        if (queryOrUrl.isBlank()) return
        val finalUrl = preferences.searchEngine.buildQueryUrl(queryOrUrl)
        loadUrl(finalUrl)
        _isEditingUrl.value = false
    }

    fun loadUrl(url: String) {
        updateActiveTab { it.copy(url = url, isHome = false, isLoading = true, progress = 10, isOffline = false, errorMessage = null) }
        _webAction.value = WebAction.LoadUrl(url)
    }

    fun reload() {
        _webAction.value = WebAction.Reload
    }

    fun stopLoading() {
        _webAction.value = WebAction.Stop
    }

    fun stop() {
        stopLoading()
    }

    fun goBack() {
        val active = getActiveTab()
        if (active.canGoBack) {
            _webAction.value = WebAction.GoBack
        } else if (!active.isHome) {
            goHome()
        }
    }

    fun goForward() {
        if (getActiveTab().canGoForward) {
            _webAction.value = WebAction.GoForward
        }
    }

    fun goHome() {
        updateActiveTab { it.copy(isHome = true, url = "", title = "Home", isLoading = false, progress = 0, isOffline = false, errorMessage = null) }
        _isEditingUrl.value = false
    }

    fun createNewTab(url: String = "", isIncognito: Boolean = false) {
        val isHome = url.isBlank()
        val isDownloads = url == "aurabrowser://downloads"
        val newTab = TabItem(
            id = java.util.UUID.randomUUID().toString(),
            title = if (isDownloads) "Downloads" else if (isHome) "Home" else "Loading...",
            url = url,
            isHome = isHome,
            isIncognito = isIncognito,
            isDesktopMode = preferences.isDesktopModeDefault
        )
        _tabs.update { it + newTab }
        _activeTabId.value = newTab.id
        _showTabSwitcher.value = false
        if (!isHome && !isDownloads) {
            loadUrl(url)
        }
    }

    fun selectTab(tabId: String) {
        val previousTab = getActiveTab()
        if (previousTab.url == "aurabrowser://downloads" && tabId != previousTab.id && !_showDownloadsSheet.value) {
            stopDownloadPolling()
        }
        if (_tabs.value.any { it.id == tabId }) {
            _activeTabId.value = tabId
            _showTabSwitcher.value = false
        }
    }

    fun closeTab(tabId: String) {
        val closingTab = _tabs.value.find { it.id == tabId }
        if (closingTab?.url == "aurabrowser://downloads" && !_showDownloadsSheet.value) {
            stopDownloadPolling()
        }
        // Free up WebView memory from pool
        webViewPoolManager.releaseWebView(tabId)

        val currentTabs = _tabs.value
        if (currentTabs.size <= 1) {
            // Keep one home tab
            val resetTab = TabItem(
                id = java.util.UUID.randomUUID().toString(),
                title = "Home",
                url = "",
                isHome = true,
                isDesktopMode = preferences.isDesktopModeDefault
            )
            _tabs.value = listOf(resetTab)
            _activeTabId.value = resetTab.id
            return
        }

        val closingIndex = currentTabs.indexOfFirst { it.id == tabId }
        val remaining = currentTabs.filter { it.id != tabId }
        _tabs.value = remaining

        if (_activeTabId.value == tabId) {
            val nextIndex = if (closingIndex >= remaining.size) remaining.size - 1 else closingIndex
            _activeTabId.value = remaining[nextIndex].id
        }
    }

    fun toggleDesktopMode() {
        val current = getActiveTab().isDesktopMode
        updateActiveTab { it.copy(isDesktopMode = !current) }
        reload()
    }

    fun toggleBookmark() {
        val active = getActiveTab()
        if (active.isHome || active.url.isBlank()) return
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.url == active.url }
            if (isBookmarked) {
                dao.deleteBookmarkByUrl(active.url)
            } else {
                dao.insertBookmark(
                    BookmarkEntity(
                        title = active.title.ifBlank { active.url },
                        url = active.url
                    )
                )
            }
        }
    }

    fun toggleBookmarkCurrentTab() {
        toggleBookmark()
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            dao.deleteBookmark(bookmark)
        }
    }

    fun deleteHistoryItem(historyItem: HistoryEntity) {
        viewModelScope.launch {
            dao.deleteHistory(historyItem)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAllHistory()
        }
    }

    fun clearBrowsingData(clearCache: Boolean, clearHistory: Boolean, clearCookies: Boolean) {
        viewModelScope.launch {
            if (clearHistory) {
                dao.clearAllHistory()
            }
            if (clearCookies) {
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
            }
            if (clearCache) {
                WebStorage.getInstance().deleteAllData()
            }
        }
    }

    fun addShortcut(title: String, url: String) {
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
        val newShortcuts = _shortcuts.value + ShortcutItem(title = title, url = formattedUrl)
        _shortcuts.value = newShortcuts
        _showAddShortcutDialog.value = false
        viewModelScope.launch(Dispatchers.IO) {
            preferences.saveShortcuts(newShortcuts)
        }
    }

    fun removeShortcut(id: String) {
        val newShortcuts = _shortcuts.value.filter { it.id != id }
        _shortcuts.value = newShortcuts
        viewModelScope.launch(Dispatchers.IO) {
            preferences.saveShortcuts(newShortcuts)
        }
    }

    fun updateSearchEngine(engine: SearchEngine) {
        preferences.searchEngine = engine
    }

    fun updateUserName(name: String) {
        preferences.userName = name
    }

    fun updateAdBlock(enabled: Boolean) {
        preferences.isAdBlockEnabled = enabled
    }

    fun updateDesktopModeDefault(enabled: Boolean) {
        preferences.isDesktopModeDefault = enabled
        updateActiveTab { it.copy(isDesktopMode = enabled) }
        reload()
    }

    fun updateJavaScript(enabled: Boolean) {
        preferences.isJavaScriptEnabled = enabled
    }

    fun updateDoNotTrack(enabled: Boolean) {
        preferences.isDoNotTrackEnabled = enabled
    }

    fun updateThemeMode(mode: String) {
        preferences.themeMode = mode
    }

    fun updateAccentColor(accent: String) {
        preferences.accentColor = accent
    }

    fun updateShowShortcuts(show: Boolean) {
        preferences.showShortcuts = show
    }

    fun updateShowRecentHistory(show: Boolean) {
        preferences.showRecentHistory = show
    }

    fun updateTabSwitcherLayout(layout: String) {
        preferences.tabSwitcherLayout = layout
    }

    fun updateBottomBarItems(items: List<String>) {
        preferences.bottomBarItems = items
    }

    fun updateWallpaperUri(uri: String?) {
        preferences.wallpaperUri = uri
    }

    fun updateWallpaperBlur(enabled: Boolean) {
        preferences.isWallpaperBlurEnabled = enabled
    }

    fun updateHomeIconUri(uri: String?) {
        preferences.homeIconUri = uri
    }

    val dnsProvider: StateFlow<String> = preferences.dnsProviderFlow
    val dnsCustomValue: StateFlow<String> = preferences.dnsCustomValueFlow

    fun updateDnsProvider(provider: String) {
        preferences.dnsProvider = provider
    }

    fun updateDnsCustomValue(value: String) {
        preferences.dnsCustomValue = value
    }

    // Sheet visibility setters
    fun setTabSwitcherVisible(visible: Boolean) {
        _showTabSwitcher.value = visible
    }

    fun setBookmarksSheetVisible(visible: Boolean) {
        _showBookmarksSheet.value = visible
    }

    fun setHistorySheetVisible(visible: Boolean) {
        _showHistorySheet.value = visible
    }

    fun setSettingsSheetVisible(visible: Boolean) {
        _showSettingsSheet.value = visible
    }

    fun setDownloadsSheetVisible(visible: Boolean) {
        _showDownloadsSheet.value = visible
        if (visible) {
            startDownloadPolling()
        } else {
            stopDownloadPolling()
        }
    }

    private fun startDownloadPolling() {
        downloadPollingJob?.cancel()
        downloadPollingJob = viewModelScope.launch {
            while (true) {
                refreshDownloads()
                // Hanya poll jika masih ada download yang berstatus PENDING/RUNNING —
                // hemat resource, berhenti otomatis begitu semua download selesai/gagal
                val hasActiveDownload = _downloads.value.any {
                    it.status == DownloadStatus.PENDING || it.status == DownloadStatus.RUNNING
                }
                if (!hasActiveDownload) break
                delay(1000L)
            }
        }
    }

    private fun stopDownloadPolling() {
        downloadPollingJob?.cancel()
        downloadPollingJob = null
    }

    suspend fun refreshDownloads() {
        val result = withContext(Dispatchers.IO) {
            downloadTracker.getDownloads()
        }
        _downloads.value = result
    }

    fun removeDownload(id: Long) {
        downloadTracker.removeDownload(id)
        viewModelScope.launch {
            refreshDownloads()
        }
    }

    fun openDownloadsTab() {
        createNewTab(url = "aurabrowser://downloads", isIncognito = false)
        setDownloadsSheetVisible(false)
        startDownloadPolling()
    }

    fun setAddShortcutDialogVisible(visible: Boolean) {
        _showAddShortcutDialog.value = visible
    }

    fun setMoreMenuVisible(visible: Boolean) {
        _showMoreMenu.value = visible
    }

    // Callbacks from WebView (associated with specific tabId)
    fun onPageStarted(tabId: String, url: String) {
        updateTab(tabId) {
            it.copy(
                url = url,
                isLoading = true,
                progress = 15,
                isOffline = false,
                errorMessage = null
            )
        }
    }

    fun onPageFinished(tabId: String, url: String, title: String?, canGoBack: Boolean, canGoForward: Boolean) {
        val pageTitle = if (!title.isNullOrBlank() && !title.startsWith("http")) title else url
        updateTab(tabId) {
            it.copy(
                url = url,
                title = pageTitle,
                isLoading = false,
                progress = 100,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                isOffline = false,
                errorMessage = null
            )
        }

        val tab = _tabs.value.find { it.id == tabId }
        if (tab != null && !tab.isIncognito && !tab.isHome && url.isNotBlank() && !url.startsWith("about:blank")) {
            viewModelScope.launch {
                dao.insertHistory(
                    HistoryEntity(
                        title = pageTitle,
                        url = url
                    )
                )
            }
        }
    }

    private var lastProgressUpdateTime = 0L
    private val PROGRESS_UPDATE_THROTTLE_MS = 80L

    fun onProgressChanged(tabId: String, progress: Int) {
        val now = System.currentTimeMillis()
        if (progress in 1..99 && now - lastProgressUpdateTime < PROGRESS_UPDATE_THROTTLE_MS) {
            return
        }
        lastProgressUpdateTime = now
        updateTab(tabId) {
            it.copy(
                progress = progress,
                isLoading = progress < 100
            )
        }
    }

    fun onReceivedError(tabId: String, description: String, isOffline: Boolean) {
        updateTab(tabId) {
            it.copy(
                isLoading = false,
                progress = 0,
                isOffline = isOffline,
                errorMessage = description
            )
        }
    }

    fun onThemeColorReceived(tabId: String, colorInt: Int?) {
        updateTab(tabId) {
            it.copy(themeColor = colorInt)
        }
    }

    fun onFaviconReceived(tabId: String, faviconBase64: String) {
        val tab = _tabs.value.find { it.id == tabId } ?: return
        if (tab.url.isBlank()) return

        val tabHost = try {
            java.net.URI(tab.url).host?.removePrefix("www.")
        } catch (e: Exception) {
            null
        } ?: return

        val matchingShortcut = _shortcuts.value.find { shortcut ->
            val shortcutHost = try {
                java.net.URI(shortcut.url).host?.removePrefix("www.")
            } catch (e: Exception) {
                null
            }
            shortcutHost == tabHost && shortcut.faviconBase64 == null
        }

        if (matchingShortcut != null) {
            val updatedShortcuts = _shortcuts.value.map {
                if (it.id == matchingShortcut.id) it.copy(faviconBase64 = faviconBase64) else it
            }
            _shortcuts.value = updatedShortcuts
            viewModelScope.launch(Dispatchers.IO) {
                preferences.saveShortcuts(updatedShortcuts)
            }
        }
    }

    private fun updateActiveTab(transform: (TabItem) -> TabItem) {
        val currentId = _activeTabId.value
        updateTab(currentId, transform)
    }

    private fun updateTab(tabId: String, transform: (TabItem) -> TabItem) {
        _tabs.update { list ->
            list.map { if (it.id == tabId) transform(it) else it }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webViewPoolManager.releaseAll()
    }
}
