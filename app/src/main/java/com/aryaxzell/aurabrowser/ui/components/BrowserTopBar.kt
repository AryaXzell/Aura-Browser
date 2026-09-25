package com.aryaxzell.aurabrowser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryaxzell.aurabrowser.data.model.TabItem

@Composable
fun BrowserTopBar(
    activeTab: TabItem,
    isEditingUrl: Boolean,
    urlInput: String,
    onUrlInputChange: (String) -> Unit,
    onStartEditingUrl: () -> Unit,
    onSubmitUrl: (String) -> Unit,
    onCancelEditingUrl: () -> Unit,
    onReload: () -> Unit,
    onStop: () -> Unit,
    onToggleBookmark: () -> Unit,
    isBookmarked: Boolean,
    onToggleDesktop: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onOpenNewTab: (Boolean) -> Unit,
    onShareUrl: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val isHttps = activeTab.url.startsWith("https://")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .statusBarsPadding()
    ) {
        // Incognito banner indicator
        if (activeTab.isIncognito) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseSurface)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Incognito Mode Active",
                    tint = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Incognito Tab • History not saved",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Floating Pill Address Bar
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("address_bar_container")
                    .clip(CircleShape)
                    .clickable(enabled = !isEditingUrl) {
                        onStartEditingUrl()
                    },
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                shape = CircleShape,
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Security or Search icon
                    if (isEditingUrl) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else if (activeTab.isHome) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search or enter URL",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Search,
                            contentDescription = if (isHttps) "Secure connection" else "Not secure",
                            tint = if (isHttps) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (isEditingUrl) {
                        BasicTextField(
                            value = urlInput,
                            onValueChange = onUrlInputChange,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .testTag("address_text_field"),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    onSubmitUrl(urlInput)
                                }
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (urlInput.isEmpty()) {
                                        Text(
                                            text = "Search or type URL",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }

                        if (urlInput.isNotEmpty()) {
                            IconButton(
                                onClick = { onUrlInputChange("") },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("clear_url_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear input",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        val displayUrl = when {
                            activeTab.isHome -> "Search or enter URL"
                            activeTab.url.isNotBlank() -> {
                                activeTab.url.removePrefix("https://").removePrefix("http://").removePrefix("www.")
                            }
                            else -> "Search or enter URL"
                        }

                        Text(
                            text = displayUrl,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = if (activeTab.isHome) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (!activeTab.isHome) {
                            if (activeTab.isLoading) {
                                IconButton(
                                    onClick = onStop,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Stop loading",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = onReload,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reload page",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Floating Pill: Cancel when editing, or More Menu when browsing
            if (isEditingUrl) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                    tonalElevation = 4.dp,
                    shadowElevation = 6.dp,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                    )
                ) {
                    IconButton(
                        onClick = onCancelEditingUrl,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("cancel_edit_url_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel URL edit",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                TopBarMenu(
                    activeTab = activeTab,
                    isBookmarked = isBookmarked,
                    onReload = onReload,
                    onToggleBookmark = onToggleBookmark,
                    onToggleDesktop = onToggleDesktop,
                    onOpenBookmarks = onOpenBookmarks,
                    onOpenHistory = onOpenHistory,
                    onOpenDownloads = onOpenDownloads,
                    onOpenSettings = onOpenSettings,
                    onOpenNewTab = onOpenNewTab,
                    onShareUrl = onShareUrl
                )
            }
        }

        // Progress bar right below address bar
        AnimatedVisibility(
            visible = activeTab.isLoading && activeTab.progress in 1..99,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { activeTab.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun TopBarMenu(
    activeTab: TabItem,
    isBookmarked: Boolean,
    onReload: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleDesktop: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNewTab: (Boolean) -> Unit,
    onShareUrl: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
            )
        ) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("top_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Browser options menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // iOS-style contextual dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp, y = 6.dp),
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            modifier = Modifier
                .width(240.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!activeTab.isHome) {
                    IOSMenuItem(
                        text = if (isBookmarked) "Remove Bookmark" else "Add Bookmark",
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        iconTint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = {
                            expanded = false
                            onToggleBookmark()
                        }
                    )
                    IOSMenuItem(
                        text = if (activeTab.isDesktopMode) "Mobile Website" else "Desktop Website",
                        icon = Icons.Default.Laptop,
                        onClick = {
                            expanded = false
                            onToggleDesktop()
                        }
                    )
                    IOSMenuItem(
                        text = "Share Page",
                        icon = Icons.Default.Share,
                        onClick = {
                            expanded = false
                            onShareUrl()
                        }
                    )
                    IOSMenuItem(
                        text = "Reload",
                        icon = Icons.Default.Refresh,
                        onClick = {
                            expanded = false
                            onReload()
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                }

                IOSMenuItem(
                    text = "New Tab",
                    icon = Icons.Default.Add,
                    onClick = {
                        expanded = false
                        onOpenNewTab(false)
                    }
                )

                IOSMenuItem(
                    text = "New Incognito Tab",
                    icon = Icons.Default.Shield,
                    onClick = {
                        expanded = false
                        onOpenNewTab(true)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                IOSMenuItem(
                    text = "Bookmarks",
                    icon = Icons.Default.Bookmarks,
                    onClick = {
                        expanded = false
                        onOpenBookmarks()
                    }
                )

                IOSMenuItem(
                    text = "History",
                    icon = Icons.Default.History,
                    onClick = {
                        expanded = false
                        onOpenHistory()
                    }
                )

                IOSMenuItem(
                    text = "Downloads",
                    icon = Icons.Default.Download,
                    onClick = {
                        expanded = false
                        onOpenDownloads()
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                )

                IOSMenuItem(
                    text = "Settings",
                    icon = Icons.Default.Settings,
                    onClick = {
                        expanded = false
                        onOpenSettings()
                    }
                )
            }
        }
    }
}

@Composable
private fun IOSMenuItem(
    text: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            ),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(19.dp)
        )
    }
}
