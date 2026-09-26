package com.aryaxzell.aurabrowser.ui.components

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.aryaxzell.aurabrowser.data.adblock.AdBlockEngine
import com.aryaxzell.aurabrowser.data.download.DownloadTracker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aryaxzell.aurabrowser.data.model.TabItem
import com.aryaxzell.aurabrowser.viewmodel.BrowserViewModel
import com.aryaxzell.aurabrowser.webview.WebViewPoolManager
import java.io.ByteArrayInputStream

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    activeTabId: String,
    activeTabUrl: String,
    isActiveTabHome: Boolean,
    isActiveTabIncognito: Boolean,
    isActiveTabDesktopMode: Boolean,
    isActiveTabOffline: Boolean,
    activeTabErrorMessage: String?,
    isActiveTabLoading: Boolean,
    webViewPoolManager: WebViewPoolManager,
    adBlockEngine: AdBlockEngine,
    downloadTracker: DownloadTracker? = null,
    isAdBlockEnabled: Boolean,
    isJavaScriptEnabled: Boolean,
    isDoNotTrack: Boolean,
    webAction: BrowserViewModel.WebAction?,
    onActionConsumed: () -> Unit,
    onPageStarted: (tabId: String, url: String) -> Unit,
    onPageFinished: (tabId: String, url: String, title: String?, canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    onProgressChanged: (tabId: String, progress: Int) -> Unit,
    onReceivedError: (tabId: String, description: String, isOffline: Boolean) -> Unit,
    onFaviconReceived: (tabId: String, faviconBase64: String) -> Unit,
    onThemeColorReceived: ((tabId: String, colorInt: Int?) -> Unit)? = null,
    onShowFileChooser: (Intent, ValueCallback<Array<Uri>>) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var defaultUserAgent by remember { mutableStateOf("") }
    val currentAdBlockState by rememberUpdatedState(isAdBlockEnabled)
    val currentDoNotTrackState by rememberUpdatedState(isDoNotTrack)
    var isFirstAdBlockComposition by remember(activeTabId) { mutableStateOf(true) }

    // Pull to Refresh state tied to active tab loading
    val isRefreshing = isActiveTabLoading
    val pullToRefreshState = rememberPullToRefreshState()

    // Intercept back button for WebView navigation
    BackHandler(enabled = !isActiveTabHome) {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onRetry() // Fallback to home
        }
    }

    fun applyUserAgentForCurrentMode() {
        webViewInstance?.settings?.let { settings ->
            if (isActiveTabDesktopMode) {
                settings.userAgentString = DESKTOP_USER_AGENT
            } else if (defaultUserAgent.isNotBlank()) {
                settings.userAgentString = defaultUserAgent
            }
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
        }
    }

    // Handle ViewModel actions (Reload, Stop, GoBack, GoForward, LoadUrl)
    LaunchedEffect(webAction) {
        val action = webAction ?: return@LaunchedEffect
        when (action) {
            is BrowserViewModel.WebAction.LoadUrl -> {
                applyUserAgentForCurrentMode()
                webViewInstance?.loadUrl(action.url)
            }
            is BrowserViewModel.WebAction.Reload -> {
                applyUserAgentForCurrentMode()
                webViewInstance?.reload()
            }
            is BrowserViewModel.WebAction.Stop -> {
                webViewInstance?.stopLoading()
            }
            is BrowserViewModel.WebAction.GoBack -> {
                if (webViewInstance?.canGoBack() == true) {
                    webViewInstance?.goBack()
                }
            }
            is BrowserViewModel.WebAction.GoForward -> {
                if (webViewInstance?.canGoForward() == true) {
                    webViewInstance?.goForward()
                }
            }
        }
        onActionConsumed()
    }

    // Reactively reload when AdBlock toggle changes on an active non-home tab (skip first composition on tab entry)
    LaunchedEffect(isAdBlockEnabled) {
        if (isFirstAdBlockComposition) {
            isFirstAdBlockComposition = false
            return@LaunchedEffect
        }
        if (!isActiveTabHome && webViewInstance != null) {
            webViewInstance?.reload()
        }
    }

    // Reactively update JavaScript setting
    LaunchedEffect(isJavaScriptEnabled) {
        webViewInstance?.settings?.javaScriptEnabled = isJavaScriptEnabled
    }

    // Apply User Agent when tab entry or defaultUserAgent is captured
    LaunchedEffect(activeTabId, defaultUserAgent) {
        if (defaultUserAgent.isNotBlank()) {
            applyUserAgentForCurrentMode()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            webViewInstance?.reload()
        },
        state = pullToRefreshState,
        modifier = modifier.fillMaxSize()
    ) {
        // key ensures separate composition scope per tabId
        key(activeTabId) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val isNewInstance = !webViewPoolManager.hasWebView(activeTabId)
                    val webView = webViewPoolManager.getOrCreateWebView(activeTabId, ctx) { newView ->
                        newView.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        defaultUserAgent = newView.settings.userAgentString

                        // Viewport & Safe Area CSS support (env(safe-area-inset-*))
                        newView.settings.useWideViewPort = true
                        newView.settings.loadWithOverviewMode = true

                        // Security & Performance WebView settings
                        newView.settings.javaScriptEnabled = isJavaScriptEnabled
                        newView.settings.domStorageEnabled = true
                        newView.settings.databaseEnabled = true
                        newView.settings.cacheMode = WebSettings.LOAD_DEFAULT
                        newView.settings.builtInZoomControls = true
                        newView.settings.displayZoomControls = false
                        newView.settings.setSupportZoom(true)
                        newView.settings.allowFileAccess = false
                        newView.settings.allowContentAccess = false

                        // Cookie setup
                        CookieManager.getInstance().setAcceptCookie(!isActiveTabIncognito)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(newView, !isActiveTabIncognito)

                        // Download listener using Android DownloadManager
                        newView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                            try {
                                val request = DownloadManager.Request(Uri.parse(url)).apply {
                                    setMimeType(mimetype)
                                    val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                                    setTitle(filename)
                                    setDescription("Downloading with Aura Browser")
                                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                                }
                                val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                val downloadId = dm.enqueue(request)
                                downloadTracker?.registerOwnedDownload(downloadId)
                                Toast.makeText(ctx, "Download started: ${URLUtil.guessFileName(url, contentDisposition, mimetype)}", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(ctx, "Unable to start download: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        })

                        newView.webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                onProgressChanged(activeTabId, newProgress)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                if (view != null && !title.isNullOrBlank()) {
                                    onPageFinished(
                                        activeTabId,
                                        view.url ?: activeTabUrl,
                                        title,
                                        view.canGoBack(),
                                        view.canGoForward()
                                    )
                                }
                            }

                            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                                super.onReceivedIcon(view, icon)
                                if (icon != null) {
                                    try {
                                        val scaled = Bitmap.createScaledBitmap(icon, 64, 64, true)
                                        val outputStream = java.io.ByteArrayOutputStream()
                                        scaled.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                        val base64 = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
                                        onFaviconReceived(activeTabId, base64)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }

                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                if (filePathCallback == null) return false
                                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                    type = "*/*"
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                }
                                return try {
                                    onShowFileChooser(intent, filePathCallback)
                                    true
                                } catch (e: Exception) {
                                    false
                                }
                            }
                        }

                        newView.webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let { onPageStarted(activeTabId, it) }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                if (view != null) {
                                    onPageFinished(
                                        activeTabId,
                                        url ?: view.url.orEmpty(),
                                        view.title,
                                        view.canGoBack(),
                                        view.canGoForward()
                                    )

                                    view.evaluateJavascript(
                                        """
                                        (function() {
                                            var meta = document.querySelector('meta[name="theme-color"]');
                                            if (meta && meta.content) return meta.content;
                                            var header = document.querySelector('header') || document.querySelector('nav');
                                            if (header) {
                                                var bg = window.getComputedStyle(header).backgroundColor;
                                                if (bg && bg !== 'rgba(0, 0, 0, 0)' && bg !== 'transparent') return bg;
                                            }
                                            var bodyBg = window.getComputedStyle(document.body).backgroundColor;
                                            if (bodyBg && bodyBg !== 'rgba(0, 0, 0, 0)' && bodyBg !== 'transparent') return bodyBg;
                                            return null;
                                        })()
                                        """.trimIndent()
                                    ) { rawResult ->
                                        if (!rawResult.isNullOrBlank() && rawResult != "null" && rawResult != "\"null\"") {
                                            val clean = rawResult.replace("\"", "").trim()
                                            val parsedColor = parseColorString(clean)
                                            if (parsedColor != null) {
                                                onThemeColorReceived?.invoke(activeTabId, parsedColor)
                                            }
                                        }
                                    }
                                }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val targetUri = request?.url ?: return false
                                val scheme = targetUri.scheme?.lowercase() ?: return false

                                // Handle normal web links
                                if (scheme == "http" || scheme == "https") {
                                    return false
                                }

                                // Handle external application intents safely
                                return try {
                                    val intent = Intent(Intent.ACTION_VIEW, targetUri)
                                    ctx.startActivity(intent)
                                    true
                                } catch (e: Exception) {
                                    true
                                }
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                if (request != null) {
                                    val host = request.url.host?.lowercase() ?: ""
                                    val path = request.url.path?.lowercase() ?: ""

                                    if (currentAdBlockState && adBlockEngine.isAdDomain(host)) {
                                        return WebResourceResponse(
                                            "text/plain",
                                            "UTF-8",
                                            ByteArrayInputStream(ByteArray(0))
                                        )
                                    }
                                    if (currentDoNotTrackState && adBlockEngine.isTrackerDomain(host)) {
                                        return WebResourceResponse(
                                            "text/plain",
                                            "UTF-8",
                                            ByteArrayInputStream(ByteArray(0))
                                        )
                                    }
                                    if (currentDoNotTrackState && isKnownTrackerPath(path)) {
                                        return WebResourceResponse(
                                            "text/plain",
                                            "UTF-8",
                                            ByteArrayInputStream(ByteArray(0))
                                        )
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    val isOffline = !isNetworkAvailable(ctx)
                                    val desc = error?.description?.toString() ?: "Failed to load page"
                                    onReceivedError(activeTabId, desc, isOffline)
                                }
                            }
                        }
                    }

                    // Detach from previous parent if still attached
                    (webView.parent as? ViewGroup)?.removeView(webView)

                    // Load URL only if new instance or uninitialized
                    if (isNewInstance || webView.url.isNullOrBlank() || webView.url == "about:blank") {
                        if (activeTabUrl.isNotBlank() && !isActiveTabHome) {
                            webView.loadUrl(activeTabUrl)
                        }
                    }

                    webViewInstance = webView
                    webView
                },
                update = { view ->
                    webViewInstance = view
                }
            )
        }

        // Error / Offline Overlay
        if (isActiveTabOffline || activeTabErrorMessage != null) {
            val isOffline = isActiveTabOffline
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .safeDrawingPadding()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isOffline) "You're offline" else "Something went wrong",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isOffline) {
                            "Check your internet connection\nand try again."
                        } else {
                            "We couldn't load this page.\n${activeTabErrorMessage ?: "Please verify the URL."}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (activeTabUrl.isNotBlank()) {
                                webViewInstance?.reload()
                            } else {
                                onRetry()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (isOffline) "Retry" else "Try again",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

private fun parseColorString(colorStr: String): Int? {
    return try {
        if (colorStr.startsWith("#")) {
            android.graphics.Color.parseColor(colorStr)
        } else if (colorStr.startsWith("rgb")) {
            val numbers = colorStr.substringAfter("(").substringBefore(")").split(",")
            if (numbers.size >= 3) {
                val r = numbers[0].trim().toIntOrNull() ?: return null
                val g = numbers[1].trim().toIntOrNull() ?: return null
                val b = numbers[2].trim().toIntOrNull() ?: return null
                android.graphics.Color.rgb(r, g, b)
            } else null
        } else {
            android.graphics.Color.parseColor(colorStr)
        }
    } catch (e: Exception) {
        null
    }
}

private fun isKnownTrackerPath(path: String): Boolean {
    return path.contains("/analytics/collect") ||
        path.contains("/gtag/js") ||
        path.contains("/pixel.gif") ||
        path.contains("/track.gif") ||
        path.contains("/beacon")
}
