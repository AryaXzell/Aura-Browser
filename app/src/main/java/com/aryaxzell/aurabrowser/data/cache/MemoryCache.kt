package com.aryaxzell.aurabrowser.data.cache

import android.util.LruCache
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * In-memory LRU cache for static web resources (CSS, JS, images, fonts).
 * Significantly reduces disk I/O and network latency by serving frequently accessed
 * static assets directly from RAM, specifically tailored for low-end devices.
 */
object MemoryCache {
    // Max 8MB of static assets in RAM to prevent memory pressure on low-end devices (RAM 2-3GB)
    private val maxCacheSize = 8 * 1024 * 1024
    
    private val cache = object : LruCache<String, CachedResource>(maxCacheSize) {
        override fun sizeOf(key: String, value: CachedResource): Int {
            return value.data.size
        }
    }

    class CachedResource(
        val mimeType: String,
        val encoding: String,
        val data: ByteArray,
        val responseHeaders: Map<String, String>?
    )

    fun get(url: String): WebResourceResponse? {
        val cached = cache.get(url) ?: return null
        val stream = ByteArrayInputStream(cached.data)
        return WebResourceResponse(
            cached.mimeType,
            cached.encoding,
            200,
            "OK",
            cached.responseHeaders,
            stream
        )
    }

    fun put(url: String, mimeType: String, encoding: String, data: ByteArray, headers: Map<String, String>?) {
        // Only cache resources smaller than 1.5MB to avoid clogging the RAM cache
        if (data.size > 1536 * 1024) return
        cache.put(url, CachedResource(mimeType, encoding, data, headers))
    }

    fun clear() {
        cache.evictAll()
    }

    /**
     * Intercepts and caches static GET requests (CDNs, libraries, common assets).
     * Bypasses the disk database entirely for these items.
     */
    fun handleIntercept(request: WebResourceRequest): WebResourceResponse? {
        val url = request.url.toString()
        val method = request.method

        if (!method.equals("GET", ignoreCase = true)) return null
        if (!url.startsWith("http://") && !url.startsWith("https://")) return null

        val path = request.url.path?.lowercase() ?: ""
        val isStatic = path.endsWith(".js") ||
                       path.endsWith(".css") ||
                       path.endsWith(".woff") ||
                       path.endsWith(".woff2") ||
                       path.endsWith(".ttf") ||
                       path.endsWith(".png") ||
                       path.endsWith(".jpg") ||
                       path.endsWith(".jpeg") ||
                       path.endsWith(".webp") ||
                       path.endsWith(".svg") ||
                       path.endsWith(".ico")

        if (!isStatic) return null

        // 1. Try memory cache first (instant, O(1), zero I/O)
        val cached = get(url)
        if (cached != null) {
            return cached
        }

        // 2. Fetch and populate cache for public/CDN/static assets
        val isCdnOrStaticAsset = url.contains("cdn") ||
                                 url.contains("bootstrap") ||
                                 url.contains("jquery") ||
                                 url.contains("cdnjs") ||
                                 url.contains("fonts.gstatic") ||
                                 url.contains("googleapis") ||
                                 url.contains("wp-content") ||
                                 url.contains("assets") ||
                                 url.contains("static")

        if (isCdnOrStaticAsset) {
            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.connectTimeout = 4000
                conn.readTimeout = 4000
                
                // Forward request headers
                request.requestHeaders.forEach { (key, value) ->
                    conn.setRequestProperty(key, value)
                }
                
                conn.connect()
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val contentType = conn.contentType ?: "application/octet-stream"
                    val mimeType = contentType.substringBefore(";").trim()
                    val encoding = if (contentType.contains("charset=")) {
                        contentType.substringAfter("charset=").substringBefore(";").trim()
                    } else {
                        "UTF-8"
                    }

                    val responseHeaders = mutableMapOf<String, String>()
                    conn.headerFields.forEach { (key, values) ->
                        if (key != null && values.isNotEmpty()) {
                            responseHeaders[key] = values.joinToString(", ")
                        }
                    }

                    val data = conn.inputStream.use { it.readBytes() }
                    put(url, mimeType, encoding, data, responseHeaders)

                    return WebResourceResponse(
                        mimeType,
                        encoding,
                        200,
                        "OK",
                        responseHeaders,
                        ByteArrayInputStream(data)
                    )
                }
            } catch (e: Exception) {
                // Graceful fallback: let default WebView flow handle it
            }
        }

        return null
    }
}
