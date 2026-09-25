package com.aryaxzell.aurabrowser.data.model

enum class SearchEngine(val displayName: String, val searchUrl: String, val homeUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
    BING("Bing", "https://www.bing.com/search?q=", "https://www.bing.com"),
    BRAVE("Brave", "https://search.brave.com/search?q=", "https://search.brave.com");

    fun buildQueryUrl(query: String): String {
        val trimmed = query.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else if (trimmed.contains(".") && !trimmed.contains(" ") && trimmed.length > 3) {
            "https://$trimmed"
        } else {
            searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }
    }
}

data class TabItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = "",
    val isHome: Boolean = true,
    val isIncognito: Boolean = false,
    val isDesktopMode: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isOffline: Boolean = false,
    val errorMessage: String? = null
)

data class ShortcutItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val iconLetter: String = title.take(1).uppercase(),
    val faviconBase64: String? = null
)
