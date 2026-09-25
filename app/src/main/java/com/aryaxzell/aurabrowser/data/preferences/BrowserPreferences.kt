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
        private const val KEY_SHORTCUTS = "pref_shortcuts"
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
