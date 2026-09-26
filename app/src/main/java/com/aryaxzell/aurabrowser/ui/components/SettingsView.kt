package com.aryaxzell.aurabrowser.ui.components

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Language
import java.io.File
import java.io.FileOutputStream
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryaxzell.aurabrowser.BuildConfig
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
    accentColor: String,
    onSelectAccentColor: (String) -> Unit,
    showShortcuts: Boolean,
    onToggleShowShortcuts: (Boolean) -> Unit,
    showRecentHistory: Boolean,
    onToggleShowRecentHistory: (Boolean) -> Unit,
    tabSwitcherLayout: String,
    onSelectTabSwitcherLayout: (String) -> Unit,
    bottomBarItems: List<String>,
    onUpdateBottomBarItems: (List<String>) -> Unit,
    wallpaperUri: String?,
    isWallpaperBlurEnabled: Boolean,
    onUpdateWallpaperUri: (String?) -> Unit,
    onUpdateWallpaperBlur: (Boolean) -> Unit,
    homeIconUri: String? = null,
    onUpdateHomeIconUri: (String?) -> Unit = {},
    isAdBlockEnabled: Boolean,
    onToggleAdBlock: (Boolean) -> Unit,
    isDesktopModeDefault: Boolean,
    onToggleDesktopDefault: (Boolean) -> Unit,
    isJavaScriptEnabled: Boolean,
    onToggleJavaScript: (Boolean) -> Unit,
    isDoNotTrack: Boolean,
    onToggleDoNotTrack: (Boolean) -> Unit,
    dnsProvider: String = "system",
    dnsCustomValue: String = "",
    onUpdateDnsProvider: (String) -> Unit = {},
    onUpdateDnsCustomValue: (String) -> Unit = {},
    onClearBrowsingData: (clearCache: Boolean, clearHistory: Boolean, clearCookies: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showNameEditDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val tabs = listOf("General", "Appearance", "Privacy", "Browser")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(200)) + slideInVertically(
                initialOffsetY = { it / 10 },
                animationSpec = tween(200)
            ),
            exit = fadeOut(animationSpec = tween(150))
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
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
                            .weight(1f)
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
                                    1 -> AppearanceTabContent(
                                        themeMode = themeMode,
                                        onSelectThemeMode = onSelectThemeMode,
                                        accentColor = accentColor,
                                        onSelectAccentColor = onSelectAccentColor,
                                        tabSwitcherLayout = tabSwitcherLayout,
                                        onSelectTabSwitcherLayout = onSelectTabSwitcherLayout,
                                        bottomBarItems = bottomBarItems,
                                        onUpdateBottomBarItems = onUpdateBottomBarItems,
                                        wallpaperUri = wallpaperUri,
                                        isWallpaperBlurEnabled = isWallpaperBlurEnabled,
                                        onUpdateWallpaperUri = onUpdateWallpaperUri,
                                        onUpdateWallpaperBlur = onUpdateWallpaperBlur,
                                        homeIconUri = homeIconUri,
                                        onUpdateHomeIconUri = onUpdateHomeIconUri,
                                        showShortcuts = showShortcuts,
                                        onToggleShowShortcuts = onToggleShowShortcuts,
                                        showRecentHistory = showRecentHistory,
                                        onToggleShowRecentHistory = onToggleShowRecentHistory
                                    )
                                    2 -> PrivacyTabContent(
                                        isAdBlockEnabled = isAdBlockEnabled,
                                        onToggleAdBlock = onToggleAdBlock,
                                        isDoNotTrack = isDoNotTrack,
                                        onToggleDoNotTrack = onToggleDoNotTrack,
                                        dnsProvider = dnsProvider,
                                        dnsCustomValue = dnsCustomValue,
                                        onUpdateDnsProvider = onUpdateDnsProvider,
                                        onUpdateDnsCustomValue = onUpdateDnsCustomValue,
                                        onOpenClearData = { showClearDataDialog = true }
                                    )
                                    3 -> BrowserTabContent(
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
        val context = LocalContext.current
        var clearHistory by remember { mutableStateOf(true) }
        var clearCache by remember { mutableStateOf(true) }
        var clearCookies by remember { mutableStateOf(false) }
        val sizeInfo = remember(showClearDataDialog) { calculateBrowsingDataSize(context) }

        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear Browsing Data") },
            text = {
                Column {
                    Text(
                        text = "Total stored data: ${sizeInfo.totalSizeStr}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
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
                        Column {
                            Text("Cached web content")
                            Text(
                                text = "Size: ${sizeInfo.cacheSizeStr}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
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
    dnsProvider: String,
    dnsCustomValue: String,
    onUpdateDnsProvider: (String) -> Unit,
    onUpdateDnsCustomValue: (String) -> Unit,
    onOpenClearData: () -> Unit
) {
    val context = LocalContext.current
    var dataSizeInfo by remember { mutableStateOf(calculateBrowsingDataSize(context)) }

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

    // Card: Secure & Private DNS
    var showDnsDialog by remember { mutableStateOf(false) }

    IOSCardGroup(
        header = "SECURE & PRIVATE DNS",
        footer = "Encrypt your DNS queries with Secure DNS (DoH/DoT) to prevent snooping and tampering by your ISP."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Language,
            iconBgColor = Color(0xFF5856D6), // iOS System Purple
            title = "Secure DNS Provider",
            subtitle = when(dnsProvider) {
                "system" -> "Default (System)"
                "cloudflare" -> "Cloudflare (1.1.1.1)"
                "google" -> "Google DNS (8.8.8.8)"
                "adguard" -> "AdGuard DNS"
                "custom" -> if (dnsCustomValue.isBlank()) "Custom (Not set)" else "Custom: $dnsCustomValue"
                else -> dnsProvider
            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when(dnsProvider) {
                            "system" -> "System"
                            "cloudflare" -> "Cloudflare"
                            "google" -> "Google"
                            "adguard" -> "AdGuard"
                            "custom" -> "Custom"
                            else -> "System"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
            onClick = { showDnsDialog = true }
        )
    }

    if (showDnsDialog) {
        var selectedProvider by remember { mutableStateOf(dnsProvider) }
        var customValueInput by remember { mutableStateOf(dnsCustomValue) }

        AlertDialog(
            onDismissRequest = { showDnsDialog = false },
            title = { Text("Pilih Secure DNS") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val dnsList = listOf(
                        "system" to "Sistem (Bawaan)",
                        "cloudflare" to "Cloudflare (1.1.1.1)",
                        "google" to "Google Public DNS",
                        "adguard" to "AdGuard DNS",
                        "custom" to "DNS Kustom (IP / DoH)"
                    )

                    dnsList.forEach { (prov, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedProvider = prov }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = (selectedProvider == prov),
                                onClick = { selectedProvider = prov }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    if (selectedProvider == "custom") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customValueInput,
                            onValueChange = { customValueInput = it },
                            label = { Text("IP atau Domain DNS (DoH)") },
                            placeholder = { Text("https://example.com/dns-query") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdateDnsProvider(selectedProvider)
                        onUpdateDnsCustomValue(customValueInput)
                        showDnsDialog = false
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDnsDialog = false }) {
                    Text("Batal")
                }
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
            subtitle = "Stored: ${dataSizeInfo.totalSizeStr} (History, Cache & Cookies)",
            titleColor = MaterialTheme.colorScheme.error,
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = dataSizeInfo.totalSizeStr,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            onClick = {
                dataSizeInfo = calculateBrowsingDataSize(context)
                onOpenClearData()
            }
        )
    }
}

@Composable
private fun AppearanceTabContent(
    themeMode: String,
    onSelectThemeMode: (String) -> Unit,
    accentColor: String,
    onSelectAccentColor: (String) -> Unit,
    tabSwitcherLayout: String,
    onSelectTabSwitcherLayout: (String) -> Unit,
    bottomBarItems: List<String>,
    onUpdateBottomBarItems: (List<String>) -> Unit,
    wallpaperUri: String?,
    isWallpaperBlurEnabled: Boolean,
    onUpdateWallpaperUri: (String?) -> Unit,
    onUpdateWallpaperBlur: (Boolean) -> Unit,
    homeIconUri: String? = null,
    onUpdateHomeIconUri: (String?) -> Unit = {},
    showShortcuts: Boolean,
    onToggleShowShortcuts: (Boolean) -> Unit,
    showRecentHistory: Boolean,
    onToggleShowRecentHistory: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var pendingCropBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val homeIconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val downsampled = decodeDownsampledBitmap(context, uri, maxDimension = 1024)
                pendingCropBitmap = downsampled
            } catch (e: Exception) {
                // Ignore decode failure
            }
        }
    }

    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some providers might not support persistable permissions
            }
            onUpdateWallpaperUri(uri.toString())
        }
    }

    // Card 1: Theme Mode
    IOSCardGroup(
        header = "THEME MODE",
        footer = "Choose between dark, light, or automatically matching your system theme."
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

    // Card 2: Accent Color Palette
    IOSCardGroup(
        header = "ACCENT COLOR",
        footer = "Customize buttons, icons, highlights, and navigation accents throughout Aura Browser."
    ) {
        val accentList = listOf(
            Triple("blue", "Aura Blue", Color(0xFF0284C7)),
            Triple("green", "Emerald Green", Color(0xFF059669)),
            Triple("purple", "Sunset Violet", Color(0xFF7C3AED)),
            Triple("rose", "Ruby Rose", Color(0xFFE11D48)),
            Triple("gold", "Amber Gold", Color(0xFFD97706)),
            Triple("cyan", "Electric Cyan", Color(0xFF0891B2))
        )

        accentList.forEachIndexed { index, (key, label, color) ->
            val isSelected = accentColor == key
            IOSSettingsRow(
                icon = Icons.Default.ColorLens,
                iconBgColor = color,
                title = label,
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                },
                onClick = { onSelectAccentColor(key) }
            )
            if (index < accentList.size - 1) {
                IOSHairlineDivider()
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 3: Tab Switcher Layout
    IOSCardGroup(
        header = "TAB SWITCHER LAYOUT",
        footer = "Choose between standard 2-column grid and vertical full-width list view for open tabs."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.GridView,
            iconBgColor = Color(0xFF5856D6),
            title = "Layout Mode",
            subtitle = if (tabSwitcherLayout == "grid") "Grid View" else "List View",
            trailingContent = {
                Box(modifier = Modifier.width(150.dp)) {
                    IOSSegmentedControl(
                        tabs = listOf("Grid", "List"),
                        selectedIndex = if (tabSwitcherLayout == "grid") 0 else 1,
                        onTabSelected = { index ->
                            onSelectTabSwitcherLayout(if (index == 0) "grid" else "list")
                        }
                    )
                }
            }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 4: Bottom Navigation Bar Customization
    IOSCardGroup(
        header = "BOTTOM BAR CUSTOMIZATION",
        footer = "Select up to 5 actions to display on the floating bottom navigation bar."
    ) {
        val availableItems = listOf(
            "back" to "Back Button",
            "forward" to "Forward Button",
            "home" to "Home / New Tab",
            "tabs" to "Tab Switcher",
            "downloads" to "Downloads",
            "bookmarks" to "Bookmarks",
            "history" to "History"
        )

        availableItems.forEachIndexed { index, (key, label) ->
            val isChecked = key in bottomBarItems
            val canCheck = isChecked || bottomBarItems.size < 5

            IOSSettingsRow(
                icon = Icons.Default.Apps,
                iconBgColor = Color(0xFF007AFF),
                title = label,
                subtitle = if (!isChecked && bottomBarItems.size >= 5) "Max 5 items reached" else null,
                trailingContent = {
                    Switch(
                        checked = isChecked,
                        enabled = canCheck,
                        onCheckedChange = { checked ->
                            val updated = if (checked) {
                                if (bottomBarItems.size < 5) bottomBarItems + key else bottomBarItems
                            } else {
                                bottomBarItems - key
                            }
                            onUpdateBottomBarItems(updated)
                        }
                    )
                }
            )
            if (index < availableItems.size - 1) {
                IOSHairlineDivider()
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 5: Homepage Background & Wallpaper
    IOSCardGroup(
        header = "HOMEPAGE BACKGROUND",
        footer = "Personalize your new tab home page with a photo from your device gallery."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Wallpaper,
            iconBgColor = Color(0xFFFF2D55),
            title = "Choose Wallpaper",
            subtitle = if (wallpaperUri != null) "Custom wallpaper set" else "Using default background",
            onClick = {
                wallpaperPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Pick wallpaper",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        if (wallpaperUri != null) {
            IOSHairlineDivider()

            IOSSettingsRow(
                icon = Icons.Default.BlurOn,
                iconBgColor = Color(0xFF5AC8FA),
                title = "Blur Wallpaper",
                subtitle = "Soft blur for better readability",
                trailingContent = {
                    Switch(
                        checked = isWallpaperBlurEnabled,
                        onCheckedChange = onUpdateWallpaperBlur
                    )
                }
            )

            IOSHairlineDivider()

            IOSSettingsRow(
                icon = Icons.Default.Delete,
                iconBgColor = Color(0xFFFF3B30),
                title = "Remove Wallpaper",
                subtitle = "Reset to default background",
                titleColor = MaterialTheme.colorScheme.error,
                onClick = { onUpdateWallpaperUri(null) }
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 6: Custom Home Icon
    IOSCardGroup(
        header = "HOME ICON",
        footer = "Customize the main icon displayed on your home page with a cropped 1:1 image."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Image,
            iconBgColor = Color(0xFFAF52DE),
            title = "Custom Home Icon",
            subtitle = if (homeIconUri != null) "Custom icon set" else "Using default Aura logo",
            onClick = {
                homeIconPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Pick custom icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        if (homeIconUri != null) {
            IOSHairlineDivider()

            IOSSettingsRow(
                icon = Icons.Default.RestartAlt,
                iconBgColor = Color(0xFFFF3B30),
                title = "Reset to Default Icon",
                subtitle = "Remove custom icon",
                titleColor = MaterialTheme.colorScheme.error,
                onClick = { onUpdateHomeIconUri(null) }
            )
        }
    }

    pendingCropBitmap?.let { bitmap ->
        ImageCropDialog(
            sourceBitmap = bitmap,
            onConfirm = { croppedBitmap ->
                val savedPath = saveCroppedIconToInternalStorage(context, croppedBitmap)
                onUpdateHomeIconUri(savedPath)
                pendingCropBitmap = null
            },
            onDismiss = { pendingCropBitmap = null }
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card 6: Home Screen Widgets
    IOSCardGroup(
        header = "HOME SCREEN WIDGETS",
        footer = "Choose which sections to display on your new tab home page."
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Dashboard,
            iconBgColor = Color(0xFF007AFF),
            title = "Speed Dial Shortcuts",
            subtitle = "Show favorite site quick launch icons",
            trailingContent = {
                Switch(
                    checked = showShortcuts,
                    onCheckedChange = onToggleShowShortcuts
                )
            }
        )

        IOSHairlineDivider()

        IOSSettingsRow(
            icon = Icons.Default.History,
            iconBgColor = Color(0xFFFF9500),
            title = "Recent Browsing History",
            subtitle = "Show recent pages on home screen",
            trailingContent = {
                Switch(
                    checked = showRecentHistory,
                    onCheckedChange = onToggleShowRecentHistory
                )
            }
        )
    }
}

@Composable
private fun BrowserTabContent(
    isDesktopModeDefault: Boolean,
    onToggleDesktopDefault: (Boolean) -> Unit,
    isJavaScriptEnabled: Boolean,
    onToggleJavaScript: (Boolean) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    // Card 1: Web Engine
    IOSCardGroup(
        header = "WEB ENGINE",
        footer = "Configure how embedded web pages are rendered."
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

    // Card 2: About
    IOSCardGroup(
        header = "ABOUT"
    ) {
        IOSSettingsRow(
            icon = Icons.Default.Info,
            iconBgColor = Color(0xFF8E8E93), // iOS System Gray
            title = "Aura Browser",
            subtitle = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            trailingContent = {
                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        IOSHairlineDivider()

        IOSSettingsRow(
            icon = Icons.Default.Person,
            iconBgColor = Color(0xFF34C759), // iOS System Green
            title = "Created by aryaxzell",
            subtitle = "github.com/aryaxzell",
            onClick = {
                try {
                    uriHandler.openUri("https://github.com/aryaxzell")
                } catch (e: Exception) {
                    // Fallback if no external browser/handler
                }
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open GitHub profile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
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

private fun decodeDownsampledBitmap(context: android.content.Context, uri: android.net.Uri, maxDimension: Int): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val ratio = minOf(
                maxDimension.toFloat() / info.size.width,
                maxDimension.toFloat() / info.size.height,
                1f
            )
            decoder.setTargetSize(
                (info.size.width * ratio).toInt().coerceAtLeast(1),
                (info.size.height * ratio).toInt().coerceAtLeast(1)
            )
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: throw IllegalStateException("Unable to decode image")
    }
}

private fun saveCroppedIconToInternalStorage(context: android.content.Context, bitmap: Bitmap): String {
    val fileName = "home_icon_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, fileName)

    context.filesDir.listFiles { f -> f.name.startsWith("home_icon_") }?.forEach { it.delete() }

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return file.absolutePath
}

private data class BrowsingDataSizeInfo(
    val cacheSizeStr: String,
    val totalSizeStr: String,
    val totalSizeBytes: Long
)

private fun calculateBrowsingDataSize(context: android.content.Context): BrowsingDataSizeInfo {
    var cacheBytes = 0L
    var totalBytes = 0L

    try {
        val cDirSize = getFolderSize(context.cacheDir)
        cacheBytes += cDirSize
        totalBytes += cDirSize
    } catch (_: Exception) {}

    try {
        val ccDirSize = getFolderSize(context.codeCacheDir)
        cacheBytes += ccDirSize
        totalBytes += ccDirSize
    } catch (_: Exception) {}

    try {
        val webViewDir = File(context.applicationInfo.dataDir, "app_webview")
        if (webViewDir.exists()) {
            totalBytes += getFolderSize(webViewDir)
        }
    } catch (_: Exception) {}

    try {
        context.databaseList().forEach { dbName ->
            val dbFile = context.getDatabasePath(dbName)
            if (dbFile.exists()) {
                totalBytes += dbFile.length()
            }
        }
    } catch (_: Exception) {}

    return BrowsingDataSizeInfo(
        cacheSizeStr = formatBytes(cacheBytes),
        totalSizeStr = formatBytes(totalBytes),
        totalSizeBytes = totalBytes
    )
}

private fun getFolderSize(folder: File?): Long {
    if (folder == null || !folder.exists()) return 0L
    var length = 0L
    val files = folder.listFiles() ?: return 0L
    for (file in files) {
        length += if (file.isDirectory) getFolderSize(file) else file.length()
    }
    return length
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0.0 KB"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    val value = bytes / Math.pow(1024.0, digitGroups.toDouble())
    return String.format(java.util.Locale.US, "%.1f %s", value, units[digitGroups])
}
