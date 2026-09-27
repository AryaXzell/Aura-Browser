package com.aryaxzell.aurabrowser.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector

data class WhatsNewEntry(
    val minVersionCode: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val versionName: String = "1.0"
)

object WhatsNewData {
    val entries = listOf(
        WhatsNewEntry(
            minVersionCode = 1,
            title = "Engine & Cold Start Acceleration",
            description = "Experience lightning-fast startup and smooth page loading with optimized memory caching.",
            icon = Icons.Default.Speed
        ),
        WhatsNewEntry(
            minVersionCode = 1,
            title = "iOS-Style Personalization",
            description = "Customize your browser with dynamic accent colors, custom wallpapers, and home widgets.",
            icon = Icons.Default.Palette
        ),
        WhatsNewEntry(
            minVersionCode = 1,
            title = "Built-in Shield & Privacy",
            description = "Block intrusive ads, trackers, and manage DNS settings directly from your privacy tab.",
            icon = Icons.Default.Shield
        ),
        WhatsNewEntry(
            minVersionCode = 100_000_000,
            title = "Release Pipeline & R8 Optimization",
            description = "Full release build pipeline with R8 code shrinking, resource stripping, and persistent keystore signing.",
            icon = Icons.Default.Build
        ),
        WhatsNewEntry(
            minVersionCode = 101_000_000,
            title = "Smart Version-Gated Onboarding",
            description = "Version-gated release notes automatically deliver key updates when updating across multiple versions.",
            icon = Icons.Default.AutoAwesome
        )
    )
}
