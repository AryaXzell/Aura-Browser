package com.aryaxzell.aurabrowser.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.GeolocationPermissions
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aryaxzell.aurabrowser.data.adblock.AdBlockEngine
import com.aryaxzell.aurabrowser.data.localization.AppStrings
import com.aryaxzell.aurabrowser.data.model.AppLanguage
import com.aryaxzell.aurabrowser.ui.icons.AddBox
import com.aryaxzell.aurabrowser.ui.icons.Bookmark
import com.aryaxzell.aurabrowser.ui.icons.BookmarkBorder
import com.aryaxzell.aurabrowser.ui.icons.ContentCopy
import com.aryaxzell.aurabrowser.ui.icons.Download
import com.aryaxzell.aurabrowser.ui.icons.Explore
import com.aryaxzell.aurabrowser.ui.icons.IosShare
import com.aryaxzell.aurabrowser.ui.icons.Shield
import com.aryaxzell.aurabrowser.viewmodel.BrowserViewModel
import kotlinx.coroutines.delay

@Composable
fun LinkPreviewOverlay(
    target: BrowserViewModel.LinkPreviewTarget,
    isLinkPreviewEnabled: Boolean,
    isBookmarked: Boolean,
    adBlockEngine: AdBlockEngine,
    isAdBlockEnabled: Boolean,
    isDoNotTrack: Boolean,
    language: AppLanguage = AppLanguage.EN,
    onOpen: () -> Unit,
    onOpenInNewTab: () -> Unit,
    onOpenInIncognitoTab: () -> Unit,
    onDownload: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = remember(language) { AppStrings(language) }
    val host = remember(target.url) {
        try {
            Uri.parse(target.url).host?.lowercase()?.removePrefix("www.") ?: target.url
        } catch (e: Exception) {
            target.url
        }
    }
    val isHttps = target.url.startsWith("https://")
    val isBlobOrData = target.url.startsWith("blob:") || target.url.startsWith("data:")

    var userShowPreview by remember { mutableStateOf(isLinkPreviewEnabled) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        BackHandler {
            onDismiss()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.44f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = false
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                val availableHeightDp = maxHeight
                val canShowPreview = userShowPreview && availableHeightDp >= 380.dp

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isHttps) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = "Not secure",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = host,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!canShowPreview) {
                                        Text(
                                            text = target.url,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            if (isLinkPreviewEnabled && availableHeightDp >= 380.dp) {
                                TextButton(
                                    onClick = { userShowPreview = !userShowPreview }
                                ) {
                                    Icon(
                                        imageVector = if (userShowPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (userShowPreview) strings.hidePreview else strings.showPreview,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }

                    // Preview Card with animated height transition
                    AnimatedVisibility(
                        visible = canShowPreview,
                        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.96f, animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 0.96f, animationSpec = tween(150))
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 6.dp,
                                shadowElevation = 8.dp,
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    IsolatedPreviewWebView(
                                        url = target.url,
                                        isSourceIncognito = target.isSourceIncognito,
                                        adBlockEngine = adBlockEngine,
                                        isAdBlockEnabled = isAdBlockEnabled,
                                        isDoNotTrack = isDoNotTrack
                                    )

                                    // Non-interactive transparent overlay: tap to open link
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) { onOpen() }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Context Menu Actions Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                            // 1. Open
                            ContextMenuRow(
                                title = strings.open,
                                icon = Icons.Filled.Explore,
                                onClick = onOpen
                            )

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                            // 2. Open in New Tab
                            ContextMenuRow(
                                title = strings.openInNewTab,
                                icon = Icons.Filled.AddBox,
                                onClick = onOpenInNewTab
                            )

                            // 3. Open in Incognito Tab (if source is not incognito)
                            if (!target.isSourceIncognito) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                                ContextMenuRow(
                                    title = strings.openInIncognitoTab,
                                    icon = Icons.Filled.Shield,
                                    iconTint = MaterialTheme.colorScheme.primary,
                                    onClick = onOpenInIncognitoTab
                                )
                            }

                            // 4. Download Linked File (if not blob or data URL)
                            if (!isBlobOrData) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                                ContextMenuRow(
                                    title = strings.downloadLinkedFile,
                                    icon = Icons.Filled.Download,
                                    onClick = onDownload
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                            // 5. Add / Remove Bookmark
                            ContextMenuRow(
                                title = if (isBookmarked) strings.removeFromBookmarks else strings.addToBookmarks,
                                icon = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                iconTint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                onClick = onToggleBookmark
                            )

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                            // 6. Copy Link
                            ContextMenuRow(
                                title = strings.copyLink,
                                icon = Icons.Filled.ContentCopy,
                                onClick = onCopyLink
                            )

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                            // 7. Share
                            ContextMenuRow(
                                title = strings.share,
                                icon = Icons.Filled.IosShare,
                                onClick = onShare
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContextMenuRow(
    title: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun IsolatedPreviewWebView(
    url: String,
    isSourceIncognito: Boolean,
    adBlockEngine: AdBlockEngine,
    isAdBlockEnabled: Boolean,
    isDoNotTrack: Boolean
) {
    var previewWebViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 8-second timeout for preview
    LaunchedEffect(url) {
        delay(8000)
        if (isLoading) {
            isLoading = false
            errorMessage = "Preview unavailable"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            previewWebViewInstance?.let { wv ->
                try {
                    wv.stopLoading()
                    wv.clearHistory()
                    wv.loadUrl("about:blank")
                    wv.onPause()
                    wv.removeAllViews()
                    wv.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            previewWebViewInstance = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = !isSourceIncognito
                        allowFileAccess = false
                        allowContentAccess = false
                        setSupportMultipleWindows(false)
                        javaScriptCanOpenWindowsAutomatically = false
                        mediaPlaybackRequiresUserGesture = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        cacheMode = if (isSourceIncognito) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
                    }

                    // Strict Cookie Isolation: do NOT call setAcceptCookie globally
                    android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, false)

                    setDownloadListener { downloadUrl, _, contentDisposition, mimetype, _ ->
                        isLoading = false
                        val filename = try {
                            android.webkit.URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)
                        } catch (e: Exception) {
                            "file"
                        }
                        errorMessage = "Download link: $filename"
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                            result?.cancel()
                            return true
                        }

                        override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                            result?.cancel()
                            return true
                        }

                        override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                            result?.cancel()
                            return true
                        }

                        override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                            callback?.invoke(origin, false, false)
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            errorMessage = null
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: android.webkit.SslErrorHandler?,
                            error: android.net.http.SslError?
                        ) {
                            handler?.cancel()
                            isLoading = false
                            errorMessage = "Can't preview this page (certificate problem)"
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            if (request?.isForMainFrame == true) {
                                isLoading = false
                                errorMessage = "Preview unavailable"
                            }
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val targetUri = request?.url ?: return false
                            val scheme = targetUri.scheme?.lowercase() ?: return false
                            // Only allow standard http/https navigation within preview; block external app intents
                            return !(scheme == "http" || scheme == "https")
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): android.webkit.WebResourceResponse? {
                            if (request != null) {
                                val host = request.url.host?.lowercase() ?: ""
                                if (isAdBlockEnabled && adBlockEngine.isAdDomain(host)) {
                                    return android.webkit.WebResourceResponse(
                                        "text/plain",
                                        "UTF-8",
                                        java.io.ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                                if (isDoNotTrack && adBlockEngine.isTrackerDomain(host)) {
                                    return android.webkit.WebResourceResponse(
                                        "text/plain",
                                        "UTF-8",
                                        java.io.ByteArrayInputStream(ByteArray(0))
                                    )
                                }
                            }
                            return super.shouldInterceptRequest(view, request)
                        }
                    }

                    loadUrl(url)
                    previewWebViewInstance = this
                }
            }
        )

        // Loading Overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        }

        // Error / Info Message Overlay
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
