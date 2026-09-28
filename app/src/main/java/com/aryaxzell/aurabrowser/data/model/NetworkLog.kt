package com.aryaxzell.aurabrowser.data.model

enum class NetworkDecision {
    ALLOWED,
    BLOCKED_AD,
    BLOCKED_TRACKER,
    SERVED_FROM_CACHE,
    FAILED
}

data class NetworkLogEntry(
    val id: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val method: String,
    val url: String,
    val isMainFrame: Boolean,
    val decision: NetworkDecision,
    val errorDescription: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val contentType: String? = null,
    val durationMs: Long? = null,
    val transferSize: Long? = null,
    val protocol: String? = null,
    val initiatorType: String? = null,
    val responseStatus: Int? = null
) {
    val requestType: String
        get() {
            val lowerUrl = url.lowercase()
            val accept = headers["Accept"]?.lowercase() ?: ""
            return when {
                isMainFrame || accept.contains("text/html") || lowerUrl.endsWith(".html") -> "Doc"
                accept.contains("text/css") || lowerUrl.endsWith(".css") -> "CSS"
                accept.contains("javascript") || lowerUrl.endsWith(".js") -> "JS"
                accept.contains("image") || lowerUrl.contains(".png") || lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg") || lowerUrl.contains(".gif") || lowerUrl.contains(".svg") || lowerUrl.contains(".webp") -> "Img"
                accept.contains("json") || accept.contains("xml") || lowerUrl.contains("/api/") -> "XHR"
                else -> "Other"
            }
        }
}
