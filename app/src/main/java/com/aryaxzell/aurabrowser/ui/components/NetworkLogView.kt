package com.aryaxzell.aurabrowser.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryaxzell.aurabrowser.data.model.NetworkDecision
import com.aryaxzell.aurabrowser.data.model.NetworkLogEntry
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkLogView(
    logs: List<NetworkLogEntry>,
    onRequestResourceTimings: (onResult: (List<ResourceTiming>) -> Unit) -> Unit,
    onClearLogs: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedFilter by remember { mutableStateOf("All") } // All, Blocked, Failed, Doc, JS, CSS, Img, XHR, Other
    var searchQuery by remember { mutableStateOf("") }
    var selectedEntryForDetail by remember { mutableStateOf<NetworkLogEntry?>(null) }
    var enrichedTimingMap by remember { mutableStateOf<Map<String, ResourceTiming>>(emptyMap()) }

    // Enrich logs on sheet open
    LaunchedEffect(Unit) {
        onRequestResourceTimings { timings ->
            enrichedTimingMap = timings.associateBy { it.name }
        }
    }

    val filteredLogs = remember(logs, selectedFilter, searchQuery) {
        logs.filter { entry ->
            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Blocked" -> entry.decision == NetworkDecision.BLOCKED_AD || entry.decision == NetworkDecision.BLOCKED_TRACKER
                "Failed" -> entry.decision == NetworkDecision.FAILED
                else -> entry.requestType.equals(selectedFilter, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isEmpty() || entry.url.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    val totalCount = logs.size
    val blockedCount = logs.count { it.decision == NetworkDecision.BLOCKED_AD || it.decision == NetworkDecision.BLOCKED_TRACKER }
    val failedCount = logs.count { it.decision == NetworkDecision.FAILED }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Network Inspector",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$totalCount total • $blockedCount blocked • $failedCount failed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            val formattedLog = logs.joinToString("\n") {
                                "${it.method} ${it.url} [${it.decision}] (${it.requestType})"
                            }
                            copyToClipboard(context, "Network Log", formattedLog)
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy log", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text("Filter network requests...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter chips
            val categories = listOf("All", "Blocked", "Failed", "Doc", "JS", "CSS", "Img", "XHR", "Other")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedFilter == category,
                        onClick = { selectedFilter = category },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Log Items List
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (filteredLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No network entries recorded", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(filteredLogs) { entry ->
                        val enrichedTiming = enrichedTimingMap[entry.url]
                        NetworkLogRow(
                            entry = entry,
                            enrichedTiming = enrichedTiming,
                            onClick = { selectedEntryForDetail = entry }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedEntryForDetail?.let { entry ->
        val enrichedTiming = enrichedTimingMap[entry.url]
        AlertDialog(
            onDismissRequest = { selectedEntryForDetail = null },
            title = { Text("Request Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                LazyColumn {
                    item {
                        Text("URL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(entry.url, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Method & Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${entry.method} • ${entry.requestType} • Main Frame: ${entry.isMainFrame}", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Decision / Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text("${entry.decision}${if (entry.errorDescription != null) " (${entry.errorDescription})" else ""}", style = MaterialTheme.typography.bodySmall)

                        if (enrichedTiming != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Performance Timing (On-Demand)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text("Duration: ${enrichedTiming.durationMs?.let { "${it}ms" } ?: "-"}", style = MaterialTheme.typography.bodySmall)
                            Text("Transfer Size: ${enrichedTiming.transferSize?.let { "${it / 1024} KB" } ?: "Unknown (CORS)"}", style = MaterialTheme.typography.bodySmall)
                            Text("Protocol: ${enrichedTiming.protocol ?: "-"}", style = MaterialTheme.typography.bodySmall)
                            Text("Initiator: ${enrichedTiming.initiatorType ?: "-"}", style = MaterialTheme.typography.bodySmall)
                        }

                        if (entry.headers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Observed Request Headers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            entry.headers.forEach { (k, v) ->
                                Text("$k: $v", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    copyToClipboard(context, "URL", entry.url)
                }) {
                    Text("Copy URL")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEntryForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun NetworkLogRow(
    entry: NetworkLogEntry,
    enrichedTiming: ResourceTiming?,
    onClick: () -> Unit
) {
    val (badgeBg, badgeFg) = when (entry.decision) {
        NetworkDecision.ALLOWED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        NetworkDecision.BLOCKED_AD -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        NetworkDecision.BLOCKED_TRACKER -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        NetworkDecision.SERVED_FROM_CACHE -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        NetworkDecision.FAILED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
    }

    val displayPath = remember(entry.url) {
        try {
            val uri = URI(entry.url)
            val host = uri.host ?: ""
            val path = uri.path ?: ""
            "$host$path"
        } catch (e: Exception) {
            entry.url
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Method badge
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = entry.method,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayPath,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${entry.requestType}${enrichedTiming?.durationMs?.let { " • ${it}ms" } ?: ""}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Decision badge
        Surface(
            color = badgeBg,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = entry.decision.name.lowercase().replace("_", " "),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = badgeFg,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

data class ResourceTiming(
    val name: String,
    val durationMs: Long?,
    val transferSize: Long?,
    val protocol: String?,
    val initiatorType: String?
)

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
}
