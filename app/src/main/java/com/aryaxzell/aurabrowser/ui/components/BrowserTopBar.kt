package com.aryaxzell.aurabrowser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
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
            .background(MaterialTheme.colorScheme.surface)
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
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Address Bar pill
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("address_bar_container")
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(enabled = !isEditingUrl) {
                        onStartEditingUrl()
                    },
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
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

                    Spacer(modifier = Modifier.width(8.dp))

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
                            style = MaterialTheme.typography.bodyMedium,
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

            Spacer(modifier = Modifier.width(4.dp))

            // Action: Cancel when editing, or More Menu when browsing
            if (isEditingUrl) {
                IconButton(
                    onClick = onCancelEditingUrl,
                    modifier = Modifier.testTag("cancel_edit_url_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel URL edit",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
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
    onOpenSettings: () -> Unit,
    onOpenNewTab: (Boolean) -> Unit,
    onShareUrl: () -> Unit
) {
    var expanded = remember { androidx.compose.runtime.mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded.value = true },
            modifier = Modifier.testTag("top_menu_button")
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Browser options menu",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.clip(RoundedCornerShape(14.dp))
        ) {
            if (!activeTab.isHome) {
                DropdownMenuItem(
                    text = { Text(if (isBookmarked) "Remove Bookmark" else "Add Bookmark") },
                    onClick = {
                        expanded.value = false
                        onToggleBookmark()
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (activeTab.isDesktopMode) "Mobile Site" else "Desktop Site") },
                    onClick = {
                        expanded.value = false
                        onToggleDesktop()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Share Page") },
                    onClick = {
                        expanded.value = false
                        onShareUrl()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Reload") },
                    onClick = {
                        expanded.value = false
                        onReload()
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("New Tab") },
                onClick = {
                    expanded.value = false
                    onOpenNewTab(false)
                }
            )

            DropdownMenuItem(
                text = { Text("New Incognito Tab") },
                onClick = {
                    expanded.value = false
                    onOpenNewTab(true)
                }
            )

            DropdownMenuItem(
                text = { Text("Bookmarks") },
                onClick = {
                    expanded.value = false
                    onOpenBookmarks()
                }
            )

            DropdownMenuItem(
                text = { Text("History") },
                onClick = {
                    expanded.value = false
                    onOpenHistory()
                }
            )

            DropdownMenuItem(
                text = { Text("Settings") },
                onClick = {
                    expanded.value = false
                    onOpenSettings()
                }
            )
        }
    }
}
