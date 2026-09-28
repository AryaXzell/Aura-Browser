package com.aryaxzell.aurabrowser.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.webkit.CookieManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

private const val MAX_SOURCE_BYTES = 2 * 1024 * 1024 // 2 MB limit
private const val CHUNK_MAX_CHARS = 2000 // Prevent single line freeze

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageSourceView(
    url: String,
    isDesktopMode: Boolean,
    renderedHtml: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Source (HTTP), 1: Rendered (DOM)
    var isWrapLines by remember { mutableStateOf(true) }

    var rawSourceText by remember { mutableStateOf<String?>(null) }
    var isTruncated by remember { mutableStateOf(false) }
    var sourceError by remember { mutableStateOf<String?>(null) }
    var isLoadingSource by remember { mutableStateOf(true) }

    // Fetch raw source via HttpURLConnection
    LaunchedEffect(url, isDesktopMode) {
        isLoadingSource = true
        sourceError = null
        try {
            val result = fetchUrlSource(url, isDesktopMode)
            rawSourceText = result.first
            isTruncated = result.second
        } catch (e: Exception) {
            sourceError = e.localizedMessage ?: "Failed to fetch page source"
        } finally {
            isLoadingSource = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Page Source",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isWrapLines = !isWrapLines }) {
                        Icon(
                            imageVector = Icons.Default.WrapText,
                            contentDescription = "Toggle wrap lines",
                            tint = if (isWrapLines) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            val activeText = if (selectedTab == 0) rawSourceText else renderedHtml
                            if (!activeText.isNullOrBlank()) {
                                copyOrShareText(context, activeText, isShare = false)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy source",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            val activeText = if (selectedTab == 0) rawSourceText else renderedHtml
                            if (!activeText.isNullOrBlank()) {
                                copyOrShareText(context, activeText, isShare = true)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share source",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tab selector
            SecondaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Server Source") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("DOM Rendered") }
                )
            }

            // Truncation banner
            if (isTruncated && selectedTab == 0) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Source code truncated (exceeds 2 MB limit)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E1E1E)) // Dark code editor background
            ) {
                if (selectedTab == 0) {
                    if (isLoadingSource) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Fetching source from server...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    } else if (sourceError != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Error: $sourceError",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFF6B6B),
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else if (rawSourceText.isNullOrEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Empty source", color = Color.Gray)
                        }
                    } else {
                        CodeViewerLazyList(
                            text = rawSourceText!!,
                            isWrapLines = isWrapLines
                        )
                    }
                } else {
                    if (renderedHtml.isNullOrBlank()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Rendered DOM not available (JavaScript may be disabled)", color = Color.Gray)
                        }
                    } else {
                        CodeViewerLazyList(
                            text = renderedHtml,
                            isWrapLines = isWrapLines
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeViewerLazyList(
    text: String,
    isWrapLines: Boolean
) {
    // Process text into line chunks
    val lineChunks = remember(text) {
        val lines = text.split("\n")
        val result = mutableListOf<Pair<Int, String>>()
        lines.forEachIndexed { lineIdx, lineStr ->
            if (lineStr.length <= CHUNK_MAX_CHARS) {
                result.add(Pair(lineIdx + 1, lineStr))
            } else {
                // Chunk long minified lines to avoid compose measure freezes
                var offset = 0
                while (offset < lineStr.length) {
                    val end = (offset + CHUNK_MAX_CHARS).coerceAtMost(lineStr.length)
                    val sub = lineStr.substring(offset, end)
                    result.add(Pair(lineIdx + 1, sub))
                    offset = end
                }
            }
        }
        result
    }

    val horizontalScrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (!isWrapLines) Modifier.horizontalScroll(horizontalScrollState) else Modifier
            )
            .padding(vertical = 8.dp)
    ) {
        itemsIndexed(lineChunks) { index, (lineNum, lineContent) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 1.dp)
            ) {
                Text(
                    text = lineNum.toString().padStart(4, ' '),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF6E7681)
                    ),
                    modifier = Modifier.width(36.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = lineContent,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFE6EDE3)
                    ),
                    softWrap = isWrapLines
                )
            }
        }
    }
}

private suspend fun fetchUrlSource(targetUrl: String, isDesktopMode: Boolean): Pair<String, Boolean> {
    return withContext(Dispatchers.IO) {
        var currentUrl = targetUrl
        var redirects = 0
        var connection: HttpURLConnection? = null
        val userAgent = if (isDesktopMode) {
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"
        } else {
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
        }

        while (redirects < 5) {
            val urlObj = URL(currentUrl)
            connection = urlObj.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.instanceFollowRedirects = false
            connection.setRequestProperty("User-Agent", userAgent)

            val cookies = CookieManager.getInstance().getCookie(currentUrl)
            if (!cookies.isNullOrBlank()) {
                connection.setRequestProperty("Cookie", cookies)
            }

            val status = connection.responseCode
            if (status in 300..399) {
                val loc = connection.getHeaderField("Location")
                if (!loc.isNullOrBlank()) {
                    currentUrl = if (loc.startsWith("http://") || loc.startsWith("https://")) loc else URL(urlObj, loc).toString()
                    redirects++
                    continue
                }
            }
            break
        }

        val conn = connection ?: throw IllegalStateException("Could not establish connection")
        val encoding = conn.contentEncoding
        val inputStream: InputStream = if (encoding != null && encoding.lowercase().contains("gzip")) {
            GZIPInputStream(conn.inputStream)
        } else {
            conn.inputStream
        }

        val buffer = ByteArray(4096)
        val outputStream = java.io.ByteArrayOutputStream()
        var totalBytesRead = 0
        var isTruncated = false

        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            if (totalBytesRead + bytesRead > MAX_SOURCE_BYTES) {
                val allowed = MAX_SOURCE_BYTES - totalBytesRead
                if (allowed > 0) {
                    outputStream.write(buffer, 0, allowed)
                }
                isTruncated = true
                break
            }
            outputStream.write(buffer, 0, bytesRead)
            totalBytesRead += bytesRead
        }

        val text = outputStream.toString("UTF-8")
        Pair(text, isTruncated)
    }
}

private fun copyOrShareText(context: Context, text: String, isShare: Boolean) {
    if (text.length > 500_000) {
        Toast.makeText(context, "Text is large (>500KB); Binder transaction limit may truncate clipboard/share", Toast.LENGTH_SHORT).show()
    }
    if (isShare) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text.take(500_000))
        }
        context.startActivity(Intent.createChooser(intent, "Share Source Code"))
    } else {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Page Source", text.take(500_000))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Page source copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
