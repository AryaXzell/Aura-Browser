package com.aryaxzell.aurabrowser.viewmodel

import android.app.Application
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aryaxzell.aurabrowser.data.db.BookmarkEntity
import com.aryaxzell.aurabrowser.data.db.BrowserDatabase
import com.aryaxzell.aurabrowser.data.db.HistoryEntity
import com.aryaxzell.aurabrowser.data.model.SearchEngine
import com.aryaxzell.aurabrowser.data.model.ShortcutItem
import com.aryaxzell.aurabrowser.data.model.TabItem
import com.aryaxzell.aurabrowser.data.preferences.BrowserPreferences
import com.aryaxzell.aurabrowser.webview.WebViewPoolManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    private val db = BrowserDatabase.getInstance(application)
    private val dao = db.browserDao()
    val preferences = BrowserPreferences(application)
    val webViewPoolManager = WebViewPoolManager()

    // Expose preferences as reactive StateFlows
    val searchEngine: StateFlow<SearchEngine> = preferences.searchEngineFlow
    val themeMode: StateFlow<String> = preferences.themeModeFlow
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

    private val _shortcuts = MutableStateFlow(preferences.getShortcuts())
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

    fun setIsEditingUrl(isEditing: Boolean) {
        _isEditingUrl.value = isEditing
        if (isEditing) {
            val active = getActiveTab()
            _urlInput.value = if (active.isHome) "" else active.url
        }
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
        val newTab = TabItem(
            id = java.util.UUID.randomUUID().toString(),
            title = if (isHome) "Home" else "Loading...",
            url = url,
            isHome = isHome,
            isIncognito = isIncognito,
            isDesktopMode = preferences.isDesktopModeDefault
        )
        _tabs.update { it + newTab }
        _activeTabId.value = newTab.id
        _showTabSwitcher.value = false
        if (!isHome) {
            loadUrl(url)
        }
    }

    fun selectTab(tabId: String) {
        if (_tabs.value.any { it.id == tabId }) {
            _activeTabId.value = tabId
            _showTabSwitcher.value = false
        }
    }

    fun closeTab(tabId: String) {
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
        preferences.saveShortcuts(newShortcuts)
        _showAddShortcutDialog.value = false
    }

    fun removeShortcut(id: String) {
        val newShortcuts = _shortcuts.value.filter { it.id != id }
        _shortcuts.value = newShortcuts
        preferences.saveShortcuts(newShortcuts)
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

    fun onProgressChanged(tabId: String, progress: Int) {
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
            preferences.saveShortcuts(updatedShortcuts)
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
