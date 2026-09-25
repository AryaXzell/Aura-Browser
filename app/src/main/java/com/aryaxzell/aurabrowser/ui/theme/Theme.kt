package com.aryaxzell.aurabrowser.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun getAuraColorScheme(darkTheme: Boolean, accentKey: String): ColorScheme {
    val (primaryColor, secondaryColor) = when (accentKey) {
        "green" -> if (darkTheme) Pair(AccentEmeraldGreenDark, Color(0xFF059669)) else Pair(AccentEmeraldGreen, Color(0xFF10B981))
        "purple" -> if (darkTheme) Pair(AccentSunsetVioletDark, Color(0xFF7C3AED)) else Pair(AccentSunsetViolet, Color(0xFF8B5CF6))
        "rose" -> if (darkTheme) Pair(AccentRubyRoseDark, Color(0xFFE11D48)) else Pair(AccentRubyRose, Color(0xFFF43F5E))
        "gold" -> if (darkTheme) Pair(AccentAmberGoldDark, Color(0xFFD97706)) else Pair(AccentAmberGold, Color(0xFFF59E0B))
        "cyan" -> if (darkTheme) Pair(AccentElectricCyanDark, Color(0xFF0891B2)) else Pair(AccentElectricCyan, Color(0xFF06B6D4))
        else -> if (darkTheme) Pair(AuraCyanDark, AuraSecondaryDark) else Pair(AuraCyanLight, AuraSecondaryLight)
    }

    return if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = AuraBgDark,
            surface = AuraSurfaceDark,
            surfaceVariant = AuraSurfaceVariantDark,
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            onPrimaryContainer = primaryColor,
            onPrimary = AuraBgDark,
            onSecondary = AuraBgDark,
            onBackground = AuraTextPrimaryDark,
            onSurface = AuraTextPrimaryDark,
            onSurfaceVariant = AuraTextSecondaryDark,
            outline = AuraBorderDark
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            background = AuraBgLight,
            surface = AuraSurfaceLight,
            surfaceVariant = AuraSurfaceVariantLight,
            primaryContainer = primaryColor.copy(alpha = 0.12f),
            onPrimaryContainer = primaryColor,
            onPrimary = AuraSurfaceLight,
            onSecondary = AuraSurfaceLight,
            onBackground = AuraTextPrimaryLight,
            onSurface = AuraTextPrimaryLight,
            onSurfaceVariant = AuraTextSecondaryLight,
            outline = AuraBorderLight
        )
    }
}

@Composable
private fun animatedColorScheme(target: ColorScheme): ColorScheme {
    val animSpec = tween<Color>(durationMillis = 350)
    return target.copy(
        primary = animateColorAsState(target.primary, animSpec, label = "primary").value,
        onPrimary = animateColorAsState(target.onPrimary, animSpec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(target.primaryContainer, animSpec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(target.onPrimaryContainer, animSpec, label = "onPrimaryContainer").value,
        secondary = animateColorAsState(target.secondary, animSpec, label = "secondary").value,
        onSecondary = animateColorAsState(target.onSecondary, animSpec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(target.secondaryContainer, animSpec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(target.onSecondaryContainer, animSpec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(target.tertiary, animSpec, label = "tertiary").value,
        onTertiary = animateColorAsState(target.onTertiary, animSpec, label = "onTertiary").value,
        background = animateColorAsState(target.background, animSpec, label = "background").value,
        onBackground = animateColorAsState(target.onBackground, animSpec, label = "onBackground").value,
        surface = animateColorAsState(target.surface, animSpec, label = "surface").value,
        onSurface = animateColorAsState(target.onSurface, animSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(target.surfaceVariant, animSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(target.onSurfaceVariant, animSpec, label = "onSurfaceVariant").value,
        outline = animateColorAsState(target.outline, animSpec, label = "outline").value,
        error = animateColorAsState(target.error, animSpec, label = "error").value,
        onError = animateColorAsState(target.onError, animSpec, label = "onError").value
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: String = "blue",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val targetScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getAuraColorScheme(darkTheme = darkTheme, accentKey = accentColor)
    }

    val animatedScheme = animatedColorScheme(targetScheme)

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = Typography,
        content = content
    )
}
