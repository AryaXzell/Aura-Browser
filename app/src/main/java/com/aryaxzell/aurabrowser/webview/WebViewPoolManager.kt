package com.aryaxzell.aurabrowser.webview

import android.content.Context
import android.webkit.WebView

/**
 * Manages a pool of active WebView instances per tab with LRU eviction.
 * Limits the number of live WebViews concurrently in memory to prevent high RAM consumption
 * on low-memory devices (such as 2-3 GB RAM devices).
 */
class WebViewPoolManager {
    private val pool = mutableMapOf<String, WebView>()
    private val lastAccessedMap = mutableMapOf<String, Long>()

    private fun getMaxLiveWebviews(context: Context): Int {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        return if (am != null && am.isLowRamDevice) {
            2 // Keep only 2 active WebViews in memory on low-RAM devices to avoid background termination
        } else {
            4 // Allow up to 4 active WebViews on standard devices
        }
    }

    fun prewarmWebView(context: Context) {
        // Post to Main Thread Looper as an IdleHandler to run ONLY after the main UI thread is completely idle (first frame drawn)
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            android.os.Looper.myQueue().addIdleHandler {
                try {
                    val prewarmed = WebView(context.applicationContext)
                    prewarmed.loadUrl("about:blank")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                false // Run once and remove
            }
        }
    }

    @Synchronized
    fun getOrCreateWebView(tabId: String, context: Context, onCreate: (WebView) -> Unit): WebView {
        val existing = pool[tabId]
        if (existing != null) {
            lastAccessedMap[tabId] = System.currentTimeMillis()
            return existing
        }

        val maxLimit = getMaxLiveWebviews(context)
        // LRU Eviction if pool reaches limit
        if (pool.size >= maxLimit) {
            val lruEntry = lastAccessedMap
                .filter { it.key != tabId && pool.containsKey(it.key) }
                .minByOrNull { it.value }

            if (lruEntry != null) {
                releaseWebView(lruEntry.key)
            }
        }

        val newWebView = WebView(context)
        onCreate(newWebView)
        pool[tabId] = newWebView
        lastAccessedMap[tabId] = System.currentTimeMillis()
        return newWebView
    }

    @Synchronized
    fun hasWebView(tabId: String): Boolean {
        return pool.containsKey(tabId)
    }

    @Synchronized
    fun releaseWebView(tabId: String) {
        val webView = pool.remove(tabId)
        lastAccessedMap.remove(tabId)
        if (webView != null) {
            try {
                webView.stopLoading()
                webView.clearHistory()
                webView.loadUrl("about:blank")
                webView.onPause()
                webView.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun releaseAll() {
        val allTabs = pool.keys.toList()
        for (tabId in allTabs) {
            releaseWebView(tabId)
        }
        pool.clear()
        lastAccessedMap.clear()
    }
}
