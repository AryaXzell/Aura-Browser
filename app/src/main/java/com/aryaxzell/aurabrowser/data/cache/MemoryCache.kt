package com.aryaxzell.aurabrowser.data.cache

import android.util.LruCache
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

/**
 * In-memory LRU cache for static web resources (CSS, JS, images, fonts).
 * Significantly reduces disk I/O and network latency by serving frequently accessed
 * static assets directly from RAM, specifically tailored for low-end devices.
 */
object MemoryCache {
    // Max 8MB of static assets in RAM to prevent memory pressure
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
        val responseHeaders: Map<String, String>?,
        val expiresAt: Long? = null
    )

    fun get(url: String): WebResourceResponse? {
        val cached = cache.get(url) ?: return null
        
        // Respect HTTP Cache-freshness (max-age / Expires)
        if (cached.expiresAt != null && System.currentTimeMillis() > cached.expiresAt) {
            cache.remove(url)
            return null
        }

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

    @Suppress("DEPRECATION")
    fun put(url: String, mimeType: String, encoding: String, data: ByteArray, headers: Map<String, String>?) {
        // Only cache resources smaller than 1.5MB to avoid clogging the RAM cache
        if (data.size > 1536 * 1024) return

        // Respect Cache-Control: no-store / no-cache
        val cacheControl = headers?.entries?.firstOrNull { it.key.equals("Cache-Control", ignoreCase = true) }?.value?.lowercase() ?: ""
        if (cacheControl.contains("no-store") || cacheControl.contains("no-cache")) {
            return
        }

        // Parse max-age or Expires for freshness
        var expiresAt: Long? = null
        if (cacheControl.contains("max-age=")) {
            try {
                val maxAgePart = cacheControl.substringAfter("max-age=").substringBefore(",").trim()
                val maxAgeSeconds = maxAgePart.toLongOrNull()
                if (maxAgeSeconds != null) {
                    expiresAt = System.currentTimeMillis() + (maxAgeSeconds * 1000)
                }
            } catch (_: Exception) {}
        } else {
            val expiresHeader = headers?.entries?.firstOrNull { it.key.equals("Expires", ignoreCase = true) }?.value
            if (expiresHeader != null) {
                try {
                    val parsedDate = java.util.Date(expiresHeader)
                    expiresAt = parsedDate.time
                } catch (_: Exception) {}
            }
        }

        cache.put(url, CachedResource(mimeType, encoding, data, headers, expiresAt))
    }

    fun clear() {
        cache.evictAll()
    }

    /**
     * Intercepts and caches static GET requests (CDNs, libraries, common assets).
     * Bypasses the disk database entirely for these items.
     * All blocking synchronous I/O has been removed from this path to prevent stalling page rendering.
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

        // Try memory cache first (instant, O(1), zero I/O)
        return get(url)
    }
}
