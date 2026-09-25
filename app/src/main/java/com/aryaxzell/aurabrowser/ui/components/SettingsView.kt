package com.aryaxzell.aurabrowser.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selectedTab by remember { mutableIntStateOf(0) }
    var showNameEditDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val tabs = listOf("General", "Privacy", "Browser")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // iOS Modal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // iOS Segmented Control
            IOSSegmentedControl(
                tabs = tabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab content with iOS grouped cards
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "settings_tab_transition"
                ) { currentTab ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp)
                    ) {
                        when (currentTab) {
                            0 -> GeneralTabContent(
                                currentSearchEngine = currentSearchEngine,
                                onSelectSearchEngine = onSelectSearchEngine,
                                userName = userName,
                                onOpenEditName = { showNameEditDialog = true }
                            )
                            1 -> PrivacyTabContent(
                                isAdBlockEnabled = isAdBlockEnabled,
                                onToggleAdBlock = onToggleAdBlock,
                                isDoNotTrack = isDoNotTrack,
                                onToggleDoNotTrack = onToggleDoNotTrack,
                                onOpenClearData = { showClearDataDialog = true }
                            )
                            2 -> BrowserTabContent(
                                themeMode = themeMode,
                                onSelectThemeMode = onSelectThemeMode,
                                isDesktopModeDefault = isDesktopModeDefault,
                                onToggleDesktopDefault = onToggleDesktopDefault,
                                isJavaScriptEnabled = isJavaScriptEnabled,
                                onToggleJavaScript = onToggleJavaScript
                            )
                        }
                    }
                }
            }
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

// -------------------------------------------------------------
// TAB CONTENT IMPLEMENTATIONS (iOS Inset Grouped Card Style)
// -------------------------------------------------------------

@Composable
private fun GeneralTabContent(
    currentSearchEngine: SearchEngine,
    onSelectSearchEngine: (SearchEngine) -> Unit,
    userName: String,
    onOpenEditName: () -> Unit
) {
    // Card 1: Default Search Engine
    IOSCardGroup(
        header = "DEFAULT SEARCH ENGINE",
        footer = "Queries typed into the address bar will search with the selected provider."
    ) {
        SearchEngine.values().forEachIndexed { index, engine ->
            val isSelected = currentSearchEngine == engine
            IOSSettingsRow(
                icon = Icons.Default.Search,
                iconBgColor = Color(0xFF007AFF), // iOS System Blue
                title = engine.displayName,
                trailingContent = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                },
                onClick = { onSelectSearchEngine(engine) }
            )
            if (index < SearchEngine.values().size - 1) {
                IOSHairlineDivider()
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 2: Personalization
    IOSCardGroup(
        header = "PERSONALIZATION",
        footer = "Your greeting name appears on the browser home screen."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Person,
            iconBgColor = Color(0xFFFF9500), // iOS System Orange
            title = "Greeting Name",
            subtitle = userName,
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            onClick = onOpenEditName
        )
    }
}

@Composable
private fun PrivacyTabContent(
    isAdBlockEnabled: Boolean,
    onToggleAdBlock: (Boolean) -> Unit,
    isDoNotTrack: Boolean,
    onToggleDoNotTrack: (Boolean) -> Unit,
    onOpenClearData: () -> Unit
) {
    // Card 1: Content Blocking & Protection
    IOSCardGroup(
        header = "CONTENT & PRIVACY PROTECTION",
        footer = "Aura Browser blocks known analytics and ad trackers to protect your privacy and speed up loading."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Shield,
            iconBgColor = Color(0xFF34C759), // iOS System Green
            title = "Block Ads & Trackers",
            subtitle = "Filters intrusive ads and trackers",
            trailingContent = {
                Switch(
                    checked = isAdBlockEnabled,
                    onCheckedChange = onToggleAdBlock
                )
            }
        )

        IOSHairlineDivider()

        IOSSettingsRow(
            icon = Icons.Default.Security,
            iconBgColor = Color(0xFF007AFF), // iOS System Blue
            title = "Do Not Track (DNT)",
            subtitle = "Sends DNT request header to websites",
            trailingContent = {
                Switch(
                    checked = isDoNotTrack,
                    onCheckedChange = onToggleDoNotTrack
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 2: Browsing Data Management
    IOSCardGroup(
        header = "BROWSING DATA",
        footer = "Remove stored browsing history, cached files, or saved cookies from your device."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.DeleteOutline,
            iconBgColor = Color(0xFFFF3B30), // iOS System Red
            title = "Clear Browsing Data",
            subtitle = "History, cache, and site cookies",
            titleColor = MaterialTheme.colorScheme.error,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = onOpenClearData
        )
    }
}

@Composable
private fun BrowserTabContent(
    themeMode: String,
    onSelectThemeMode: (String) -> Unit,
    isDesktopModeDefault: Boolean,
    onToggleDesktopDefault: (Boolean) -> Unit,
    isJavaScriptEnabled: Boolean,
    onToggleJavaScript: (Boolean) -> Unit
) {
    // Card 1: Appearance
    IOSCardGroup(
        header = "APPEARANCE"
    ) {
        val themes = listOf(
            "system" to "Follow System",
            "light" to "Light",
            "dark" to "Dark"
        )
        themes.forEachIndexed { index, (key, label) ->
            val isSelected = themeMode == key
            IOSSettingsRow(
                icon = Icons.Default.Palette,
                iconBgColor = Color(0xFFAF52DE), // iOS System Purple
                title = label,
                trailingContent = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                },
                onClick = { onSelectThemeMode(key) }
            )
            if (index < themes.size - 1) {
                IOSHairlineDivider()
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 2: Web Engine
    IOSCardGroup(
        header = "WEB ENGINE",
        footer = "Configure how the embedded WebView renders web pages."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Laptop,
            iconBgColor = Color(0xFF5856D6), // iOS System Indigo
            title = "Desktop Site by Default",
            subtitle = "Always request full desktop pages",
            trailingContent = {
                Switch(
                    checked = isDesktopModeDefault,
                    onCheckedChange = onToggleDesktopDefault
                )
            }
        )

        IOSHairlineDivider()

        IOSSettingsRow(
            icon = Icons.Default.Code,
            iconBgColor = Color(0xFFFF9500), // iOS System Orange
            title = "Enable JavaScript",
            subtitle = "Required for modern web features",
            trailingContent = {
                Switch(
                    checked = isJavaScriptEnabled,
                    onCheckedChange = onToggleJavaScript
                )
            }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 3: About
    IOSCardGroup(
        header = "ABOUT"
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Info,
            iconBgColor = Color(0xFF8E8E93), // iOS System Gray
            title = "Aura Browser",
            subtitle = "Version 1.0 • Android System WebView",
            trailingContent = {
                Text(
                    text = "v1.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

// -------------------------------------------------------------
// REUSABLE iOS-STYLE COMPONENTS
// -------------------------------------------------------------

@Composable
private fun IOSSegmentedControl(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                        )
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun IOSCardGroup(
    header: String? = null,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!header.isNullOrBlank()) {
            Text(
                text = header,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 14.dp, bottom = 6.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }

        if (!footer.isNullOrBlank()) {
            Text(
                text = footer,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.padding(start = 14.dp, top = 6.dp, end = 14.dp)
            )
        }
    }
}

@Composable
private fun IOSSettingsRow(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingContent: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // iOS Vibrant Icon Squircle
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        trailingContent()
    }
}

@Composable
private fun IOSHairlineDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    )
}
