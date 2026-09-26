package com.aryaxzell.aurabrowser.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.aryaxzell.aurabrowser.data.model.SearchEngine
import com.aryaxzell.aurabrowser.data.model.ShortcutItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class BrowserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("aura_browser_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SEARCH_ENGINE = "pref_search_engine"
        private const val KEY_USER_NAME = "pref_user_name"
        private const val KEY_ADBLOCK_ENABLED = "pref_adblock_enabled"
        private const val KEY_DESKTOP_MODE_DEFAULT = "pref_desktop_mode_default"
        private const val KEY_JAVASCRIPT_ENABLED = "pref_javascript_enabled"
        private const val KEY_DO_NOT_TRACK = "pref_do_not_track"
        private const val KEY_THEME_MODE = "pref_theme_mode" // "system", "dark", "light"
        private const val KEY_ACCENT_COLOR = "pref_accent_color" // "blue", "green", "purple", "rose", "gold", "cyan"
        private const val KEY_SHOW_SHORTCUTS = "pref_show_shortcuts"
        private const val KEY_SHOW_RECENT_HISTORY = "pref_show_recent_history"
        private const val KEY_TAB_SWITCHER_LAYOUT = "pref_tab_switcher_layout" // "grid" or "list"
        private const val KEY_BOTTOM_BAR_ITEMS = "pref_bottom_bar_items"
        private const val DEFAULT_BOTTOM_BAR_ITEMS = "back,forward,home,tabs,downloads"
        private const val KEY_WALLPAPER_URI = "pref_wallpaper_uri"
        private const val KEY_WALLPAPER_BLUR_ENABLED = "pref_wallpaper_blur_enabled"
        private const val KEY_HOME_ICON_URI = "pref_home_icon_uri"
        private const val KEY_SHORTCUTS = "pref_shortcuts"
        private const val KEY_DNS_PROVIDER = "pref_dns_provider"
        private const val KEY_DNS_CUSTOM_VALUE = "pref_dns_custom_value"
    }

    private val _dnsProvider = MutableStateFlow(prefs.getString(KEY_DNS_PROVIDER, "system") ?: "system")
    val dnsProviderFlow: StateFlow<String> = _dnsProvider.asStateFlow()
    var dnsProvider: String
        get() = _dnsProvider.value
        set(value) {
            prefs.edit().putString(KEY_DNS_PROVIDER, value).apply()
            _dnsProvider.value = value
        }

    private val _dnsCustomValue = MutableStateFlow(prefs.getString(KEY_DNS_CUSTOM_VALUE, "") ?: "")
    val dnsCustomValueFlow: StateFlow<String> = _dnsCustomValue.asStateFlow()
    var dnsCustomValue: String
        get() = _dnsCustomValue.value
        set(value) {
            prefs.edit().putString(KEY_DNS_CUSTOM_VALUE, value).apply()
            _dnsCustomValue.value = value
        }

    private val _homeIconUri = MutableStateFlow(prefs.getString(KEY_HOME_ICON_URI, null))
    val homeIconUriFlow: StateFlow<String?> = _homeIconUri.asStateFlow()
    var homeIconUri: String?
        get() = _homeIconUri.value
        set(value) {
            prefs.edit().putString(KEY_HOME_ICON_URI, value).apply()
            _homeIconUri.value = value
        }

    private val _tabSwitcherLayout = MutableStateFlow(prefs.getString(KEY_TAB_SWITCHER_LAYOUT, "grid") ?: "grid")
    val tabSwitcherLayoutFlow: StateFlow<String> = _tabSwitcherLayout.asStateFlow()
    var tabSwitcherLayout: String
        get() = _tabSwitcherLayout.value
        set(value) {
            prefs.edit().putString(KEY_TAB_SWITCHER_LAYOUT, value).apply()
            _tabSwitcherLayout.value = value
        }

    private val _bottomBarItems = MutableStateFlow(
        (prefs.getString(KEY_BOTTOM_BAR_ITEMS, DEFAULT_BOTTOM_BAR_ITEMS) ?: DEFAULT_BOTTOM_BAR_ITEMS)
            .split(",").filter { it.isNotBlank() }
    )
    val bottomBarItemsFlow: StateFlow<List<String>> = _bottomBarItems.asStateFlow()
    var bottomBarItems: List<String>
        get() = _bottomBarItems.value
        set(value) {
            prefs.edit().putString(KEY_BOTTOM_BAR_ITEMS, value.joinToString(",")).apply()
            _bottomBarItems.value = value
        }

    private val _wallpaperUri = MutableStateFlow(prefs.getString(KEY_WALLPAPER_URI, null))
    val wallpaperUriFlow: StateFlow<String?> = _wallpaperUri.asStateFlow()
    var wallpaperUri: String?
        get() = _wallpaperUri.value
        set(value) {
            prefs.edit().putString(KEY_WALLPAPER_URI, value).apply()
            _wallpaperUri.value = value
        }

    private val _isWallpaperBlurEnabled = MutableStateFlow(prefs.getBoolean(KEY_WALLPAPER_BLUR_ENABLED, false))
    val isWallpaperBlurEnabledFlow: StateFlow<Boolean> = _isWallpaperBlurEnabled.asStateFlow()
    var isWallpaperBlurEnabled: Boolean
        get() = _isWallpaperBlurEnabled.value
        set(value) {
            prefs.edit().putBoolean(KEY_WALLPAPER_BLUR_ENABLED, value).apply()
            _isWallpaperBlurEnabled.value = value
        }

    private val _accentColor = MutableStateFlow(prefs.getString(KEY_ACCENT_COLOR, "blue") ?: "blue")
    val accentColorFlow: StateFlow<String> = _accentColor.asStateFlow()
    var accentColor: String
        get() = _accentColor.value
        set(value) {
            prefs.edit().putString(KEY_ACCENT_COLOR, value).apply()
            _accentColor.value = value
        }

    private val _showShortcuts = MutableStateFlow(prefs.getBoolean(KEY_SHOW_SHORTCUTS, true))
    val showShortcutsFlow: StateFlow<Boolean> = _showShortcuts.asStateFlow()
    var showShortcuts: Boolean
        get() = _showShortcuts.value
        set(value) {
            prefs.edit().putBoolean(KEY_SHOW_SHORTCUTS, value).apply()
            _showShortcuts.value = value
        }

    private val _showRecentHistory = MutableStateFlow(prefs.getBoolean(KEY_SHOW_RECENT_HISTORY, true))
    val showRecentHistoryFlow: StateFlow<Boolean> = _showRecentHistory.asStateFlow()
    var showRecentHistory: Boolean
        get() = _showRecentHistory.value
        set(value) {
            prefs.edit().putBoolean(KEY_SHOW_RECENT_HISTORY, value).apply()
            _showRecentHistory.value = value
        }

    private val _searchEngine = MutableStateFlow(loadSearchEngine())
    val searchEngineFlow: StateFlow<SearchEngine> = _searchEngine.asStateFlow()
    var searchEngine: SearchEngine
        get() = _searchEngine.value
        set(value) {
            prefs.edit().putString(KEY_SEARCH_ENGINE, value.name).apply()
            _searchEngine.value = value
        }

    private fun loadSearchEngine(): SearchEngine {
        val name = prefs.getString(KEY_SEARCH_ENGINE, SearchEngine.GOOGLE.name) ?: SearchEngine.GOOGLE.name
        return try {
            SearchEngine.valueOf(name)
        } catch (e: Exception) {
            SearchEngine.GOOGLE
        }
    }

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "system") ?: "system")
    val themeModeFlow: StateFlow<String> = _themeMode.asStateFlow()
    var themeMode: String
        get() = _themeMode.value
        set(value) {
            prefs.edit().putString(KEY_THEME_MODE, value).apply()
            _themeMode.value = value
        }

    private val _isAdBlockEnabled = MutableStateFlow(prefs.getBoolean(KEY_ADBLOCK_ENABLED, true))
    val isAdBlockEnabledFlow: StateFlow<Boolean> = _isAdBlockEnabled.asStateFlow()
    var isAdBlockEnabled: Boolean
        get() = _isAdBlockEnabled.value
        set(value) {
            prefs.edit().putBoolean(KEY_ADBLOCK_ENABLED, value).apply()
            _isAdBlockEnabled.value = value
        }

    private val _isDesktopModeDefault = MutableStateFlow(prefs.getBoolean(KEY_DESKTOP_MODE_DEFAULT, false))
    val isDesktopModeDefaultFlow: StateFlow<Boolean> = _isDesktopModeDefault.asStateFlow()
    var isDesktopModeDefault: Boolean
        get() = _isDesktopModeDefault.value
        set(value) {
            prefs.edit().putBoolean(KEY_DESKTOP_MODE_DEFAULT, value).apply()
            _isDesktopModeDefault.value = value
        }

    private val _isJavaScriptEnabled = MutableStateFlow(prefs.getBoolean(KEY_JAVASCRIPT_ENABLED, true))
    val isJavaScriptEnabledFlow: StateFlow<Boolean> = _isJavaScriptEnabled.asStateFlow()
    var isJavaScriptEnabled: Boolean
        get() = _isJavaScriptEnabled.value
        set(value) {
            prefs.edit().putBoolean(KEY_JAVASCRIPT_ENABLED, value).apply()
            _isJavaScriptEnabled.value = value
        }

    private val _isDoNotTrackEnabled = MutableStateFlow(prefs.getBoolean(KEY_DO_NOT_TRACK, true))
    val isDoNotTrackEnabledFlow: StateFlow<Boolean> = _isDoNotTrackEnabled.asStateFlow()
    var isDoNotTrackEnabled: Boolean
        get() = _isDoNotTrackEnabled.value
        set(value) {
            prefs.edit().putBoolean(KEY_DO_NOT_TRACK, value).apply()
            _isDoNotTrackEnabled.value = value
        }

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Arya") ?: "Arya"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    fun getShortcuts(): List<ShortcutItem> {
        val json = prefs.getString(KEY_SHORTCUTS, null)
        if (json.isNullOrBlank()) {
            return listOf(
                ShortcutItem(title = "Google", url = "https://www.google.com"),
                ShortcutItem(title = "GitHub", url = "https://github.com"),
                ShortcutItem(title = "YouTube", url = "https://www.youtube.com"),
                ShortcutItem(title = "Reddit", url = "https://www.reddit.com"),
                ShortcutItem(title = "Wikipedia", url = "https://www.wikipedia.org")
            )
        }
        val list = mutableListOf<ShortcutItem>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ShortcutItem(
                        id = obj.optString("id"),
                        title = obj.getString("title"),
                        url = obj.getString("url"),
                        faviconBase64 = obj.optString("faviconBase64", null).takeIf { !it.isNullOrBlank() }
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveShortcuts(shortcuts: List<ShortcutItem>) {
        val arr = JSONArray()
        shortcuts.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("title", it.title)
            obj.put("url", it.url)
            if (it.faviconBase64 != null) {
                obj.put("faviconBase64", it.faviconBase64)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_SHORTCUTS, arr.toString()).apply()
    }
}
