package com.aryaxzell.aurabrowser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    isHome: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onNewTab: () -> Unit,
    onOpenTabSwitcher: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                enabled = canGoBack || !isHome,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = if (canGoBack || !isHome) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }

            // Forward Button
            IconButton(
                onClick = onForward,
                enabled = canGoForward,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Navigate forward",
                    tint = if (canGoForward) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                )
            }

            // New Tab or Home Button
            if (isHome) {
                IconButton(
                    onClick = onNewTab,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_new_tab_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Open new tab",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = onGoHome,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Go to home screen",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Tab Switcher Button (Rounded square with tab count)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        onClick = onOpenTabSwitcher
                    )
                    .testTag("nav_tab_switcher_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(5.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (tabCount > 99) ":)" else tabCount.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
