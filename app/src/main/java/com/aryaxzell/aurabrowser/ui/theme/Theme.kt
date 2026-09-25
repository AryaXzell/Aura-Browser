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

private val DarkColorScheme = darkColorScheme(
    primary = AuraCyanDark,
    secondary = AuraSecondaryDark,
    background = AuraBgDark,
    surface = AuraSurfaceDark,
    surfaceVariant = AuraSurfaceVariantDark,
    onPrimary = AuraBgDark,
    onSecondary = AuraBgDark,
    onBackground = AuraTextPrimaryDark,
    onSurface = AuraTextPrimaryDark,
    onSurfaceVariant = AuraTextSecondaryDark,
    outline = AuraBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = AuraCyanLight,
    secondary = AuraSecondaryLight,
    background = AuraBgLight,
    surface = AuraSurfaceLight,
    surfaceVariant = AuraSurfaceVariantLight,
    onPrimary = AuraSurfaceLight,
    onSecondary = AuraSurfaceLight,
    onBackground = AuraTextPrimaryLight,
    onSurface = AuraTextPrimaryLight,
    onSurfaceVariant = AuraTextSecondaryLight,
    outline = AuraBorderLight
)

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
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val targetScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val animatedScheme = animatedColorScheme(targetScheme)

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = Typography,
        content = content
    )
}
