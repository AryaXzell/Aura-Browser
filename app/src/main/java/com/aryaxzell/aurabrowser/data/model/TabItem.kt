package com.aryaxzell.aurabrowser.data.model

enum class SearchEngine(val displayName: String, val searchUrl: String, val homeUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
    BING("Bing", "https://www.bing.com/search?q=", "https://www.bing.com"),
    BRAVE("Brave", "https://search.brave.com/search?q=", "https://search.brave.com");

    fun buildQueryUrl(query: String): String {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return ""

        // 1. Explicit scheme (e.g. https://, http://, file://, about:, data:, javascript:, mailto:, tel:)
        val lowercase = trimmed.lowercase()
        val isExplicitSchemeWithSlashes = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://").containsMatchIn(trimmed)
        val isKnownNoSlashScheme = lowercase.startsWith("about:") ||
                lowercase.startsWith("data:") ||
                lowercase.startsWith("javascript:") ||
                lowercase.startsWith("mailto:") ||
                lowercase.startsWith("tel:")

        if (isExplicitSchemeWithSlashes || isKnownNoSlashScheme) {
            return trimmed
        }

        // 2. Contains whitespace -> search
        if (trimmed.any { it.isWhitespace() }) {
            return searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
        }

        // Extract hostPart (before '/', '?', '#')
        val hostAndPort = trimmed.split('/', '?', '#', limit = 2)[0]

        // 3. Localhost or IPv4
        if (isLocalhostOrIPv4(hostAndPort)) {
            return "http://$trimmed"
        }

        // 4. Looks like a valid domain host
        if (isValidDomainHost(hostAndPort)) {
            return "https://$trimmed"
        }

        // 5. Fallback to search
        return searchUrl + java.net.URLEncoder.encode(trimmed, "UTF-8")
    }

    private fun isLocalhostOrIPv4(hostAndPort: String): Boolean {
        val parts = hostAndPort.split(":", limit = 2)
        val host = parts[0].lowercase()
        if (parts.size > 1) {
            val port = parts[1]
            if (port.isEmpty() || !port.all { it.isDigit() }) return false
        }

        if (host == "localhost") return true

        val octets = host.split(".")
        if (octets.size != 4) return false
        return octets.all { octet ->
            octet.isNotEmpty() && octet.all { it.isDigit() } && (octet.toIntOrNull() in 0..255)
        }
    }

    private fun isValidDomainHost(hostAndPort: String): Boolean {
        if (hostAndPort.contains("@")) return false

        val parts = hostAndPort.split(":", limit = 2)
        val host = parts[0]
        if (parts.size > 1) {
            val port = parts[1]
            if (port.isEmpty() || !port.all { it.isDigit() }) return false
        }

        val labels = host.split(".")
        if (labels.size < 2) return false

        for (i in labels.indices) {
            val label = labels[i]
            if (label.isEmpty()) return false
            if (label.startsWith("-") || label.endsWith("-")) return false
            if (!label.all { it.isLetterOrDigit() || it == '-' }) return false
        }

        val tld = labels.last()
        return tld.length >= 2 && tld.all { it.isLetter() }
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
    val errorMessage: String? = null,
    val themeColor: Int? = null
)

data class ShortcutItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val iconLetter: String = title.take(1).uppercase(),
    val faviconBase64: String? = null
)
