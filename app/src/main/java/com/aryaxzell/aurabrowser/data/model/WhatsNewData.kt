package com.aryaxzell.aurabrowser.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector

data class WhatsNewEntry(
    val minVersionCode: Int,
    val title: String,
    val description: String,
    val icon: ImageVector
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
        )
    )
}
