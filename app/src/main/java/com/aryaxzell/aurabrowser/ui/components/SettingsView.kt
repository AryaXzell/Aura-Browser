package com.aryaxzell.aurabrowser.ui.components

import android.webkit.WebView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aryaxzell.aurabrowser.data.model.SearchEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    currentSearchEngine: SearchEngine,
    onSelectSearchEngine: (SearchEngine) -> Unit,
    userName: String,
    onUpdateUserName: (String) -> Unit,
    themeMode: String,
    onSelectThemeMode: (String) -> Unit,
    isAdBlockEnabled: Boolean,
    onToggleAdBlock: (Boolean) -> Unit,
    isDesktopModeDefault: Boolean,
    onToggleDesktopDefault: (Boolean) -> Unit,
    isJavaScriptEnabled: Boolean,
    onToggleJavaScript: (Boolean) -> Unit,
    isDoNotTrack: Boolean,
    onToggleDoNotTrack: (Boolean) -> Unit,
    onClearBrowsingData: (clearCache: Boolean, clearHistory: Boolean, clearCookies: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showNameEditDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close settings")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GENERAL SECTION
            SettingsSectionHeader(title = "General", icon = Icons.Default.Search)

            // Search Engine Selection
            Text(
                text = "Default Search Engine",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            SearchEngine.values().forEach { engine ->
                val isSelected = currentSearchEngine == engine
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else
                        Color.Transparent,
                    animationSpec = tween(200),
                    label = "search_engine_radio_bg"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable { onSelectSearchEngine(engine) }
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectSearchEngine(engine) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = engine.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Name on Homepage
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showNameEditDialog = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Greeting Name",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Shown on homepage greeting: $userName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // APPEARANCE SECTION
            SettingsSectionHeader(title = "Appearance", icon = Icons.Default.Palette)

            listOf("system" to "Follow System", "light" to "Light", "dark" to "Dark").forEach { (key, label) ->
                val isSelected = themeMode == key
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    else
                        Color.Transparent,
                    animationSpec = tween(200),
                    label = "theme_mode_radio_bg"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .clickable { onSelectThemeMode(key) }
                        .padding(vertical = 4.dp, horizontal = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectThemeMode(key) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // PRIVACY & SECURITY SECTION
            SettingsSectionHeader(title = "Privacy & Security", icon = Icons.Default.Security)

            // Ad & Tracker Blocker
            SettingsSwitchRow(
                title = "Block Ads & Trackers",
                subtitle = "Blocks intrusive ads and trackers for faster, lightweight browsing",
                checked = isAdBlockEnabled,
                onCheckedChange = onToggleAdBlock
            )

            // Do Not Track
            SettingsSwitchRow(
                title = "Do Not Track (DNT)",
                subtitle = "Sends Do Not Track header to websites",
                checked = isDoNotTrack,
                onCheckedChange = onToggleDoNotTrack
            )

            // Clear Browsing Data
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showClearDataDialog = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Clear Browsing Data",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Clear history, cache, or cookies",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // BROWSER SETTINGS SECTION
            SettingsSectionHeader(title = "Browser Engine", icon = Icons.Default.Web)

            // Default Desktop Site
            SettingsSwitchRow(
                title = "Always request Desktop Site",
                subtitle = "Loads desktop layout for all websites by default",
                checked = isDesktopModeDefault,
                onCheckedChange = onToggleDesktopDefault
            )

            // JavaScript
            SettingsSwitchRow(
                title = "Enable JavaScript",
                subtitle = "Required for modern interactive web pages",
                checked = isJavaScriptEnabled,
                onCheckedChange = onToggleJavaScript
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ABOUT / SYSTEM INFO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "About Aura Browser",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0 • Powered by Android System WebView",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Simple, clean, fast, beautiful.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Name Edit Dialog
    if (showNameEditDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showNameEditDialog = false },
            title = { Text("Edit Greeting Name") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    singleLine = true,
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            onUpdateUserName(tempName.trim())
                        }
                        showNameEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear Data Dialog
    if (showClearDataDialog) {
        var clearHistory by remember { mutableStateOf(true) }
        var clearCache by remember { mutableStateOf(true) }
        var clearCookies by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear Browsing Data") },
            text = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { clearHistory = !clearHistory }
                    ) {
                        Checkbox(checked = clearHistory, onCheckedChange = { clearHistory = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browsing history")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { clearCache = !clearCache }
                    ) {
                        Checkbox(checked = clearCache, onCheckedChange = { clearCache = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cached web content")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { clearCookies = !clearCookies }
                    ) {
                        Checkbox(checked = clearCookies, onCheckedChange = { clearCookies = it })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cookies & site data")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearBrowsingData(clearCache, clearHistory, clearCookies)
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize(animationSpec = tween(200)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
