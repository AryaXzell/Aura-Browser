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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
 
/**
 * Memilih warna teks/ikon ("on-color") yang kontras terbaik terhadap sebuah warna latar,
 * dengan mempertimbangkan luminance aktual warna tersebut alih-alih asumsi statis.
 * Ini menjamin keterbacaan konsisten di SELURUH 6 varian accent color, termasuk kombinasi
 * yang secara warna cukup terang/gelap ekstrem seperti Amber Gold atau Electric Cyan.
 */
private fun contrastingOnColor(backgroundColor: Color, darkTheme: Boolean): Color {
    val luminance = backgroundColor.luminance()
    return if (luminance > 0.5f) {
        AuraTextPrimaryLight
    } else {
        if (darkTheme) AuraTextPrimaryDark else Color.White
    }
}

fun getAuraColorScheme(darkTheme: Boolean, accentKey: String): ColorScheme {
    val (primaryColor, secondaryColor) = when (accentKey) {
        "green" -> if (darkTheme) Pair(AccentEmeraldGreenDark, Color(0xFF059669)) else Pair(AccentEmeraldGreen, Color(0xFF10B981))
        "purple" -> if (darkTheme) Pair(AccentSunsetVioletDark, Color(0xFF7C3AED)) else Pair(AccentSunsetViolet, Color(0xFF8B5CF6))
        "rose" -> if (darkTheme) Pair(AccentRubyRoseDark, Color(0xFFE11D48)) else Pair(AccentRubyRose, Color(0xFFF43F5E))
        "gold" -> if (darkTheme) Pair(AccentAmberGoldDark, Color(0xFFD97706)) else Pair(AccentAmberGold, Color(0xFFF59E0B))
        "cyan" -> if (darkTheme) Pair(AccentElectricCyanDark, Color(0xFF0891B2)) else Pair(AccentElectricCyan, Color(0xFF06B6D4))
        else -> if (darkTheme) Pair(AuraCyanDark, AuraSecondaryDark) else Pair(AuraCyanLight, AuraSecondaryLight)
    }
 
    val onPrimaryColor = contrastingOnColor(primaryColor, darkTheme)
    val onSecondaryColor = contrastingOnColor(secondaryColor, darkTheme)

    return if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = onPrimaryColor,
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = onSecondaryColor,
            secondaryContainer = AuraSecondaryContainerDark,
            onSecondaryContainer = AuraOnSecondaryContainerDark,
            tertiary = AuraTertiaryDark,
            onTertiary = AuraOnTertiaryDark,
            tertiaryContainer = AuraTertiaryContainerDark,
            onTertiaryContainer = AuraOnTertiaryContainerDark,
            background = AuraBgDark,
            onBackground = AuraTextPrimaryDark,
            surface = AuraSurfaceDark,
            onSurface = AuraTextPrimaryDark,
            surfaceVariant = AuraSurfaceVariantDark,
            onSurfaceVariant = AuraTextSecondaryDark,
            outline = AuraBorderDark,
            outlineVariant = AuraOutlineVariantDark,
            error = AuraErrorDark,
            onError = AuraOnErrorDark,
            errorContainer = AuraErrorContainerDark,
            onErrorContainer = AuraOnErrorContainerDark,
            inverseSurface = AuraInverseSurfaceDark,
            inverseOnSurface = AuraInverseOnSurfaceDark,
            inversePrimary = AuraInversePrimaryDark,
            scrim = AuraScrim
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = onPrimaryColor,
            primaryContainer = primaryColor.copy(alpha = 0.12f),
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = onSecondaryColor,
            secondaryContainer = AuraSecondaryContainerLight,
            onSecondaryContainer = AuraOnSecondaryContainerLight,
            tertiary = AuraTertiaryLight,
            onTertiary = AuraOnTertiaryLight,
            tertiaryContainer = AuraTertiaryContainerLight,
            onTertiaryContainer = AuraOnTertiaryContainerLight,
            background = AuraBgLight,
            onBackground = AuraTextPrimaryLight,
            surface = AuraSurfaceLight,
            onSurface = AuraTextPrimaryLight,
            surfaceVariant = AuraSurfaceVariantLight,
            onSurfaceVariant = AuraTextSecondaryLight,
            outline = AuraBorderLight,
            outlineVariant = AuraOutlineVariantLight,
            error = AuraErrorLight,
            onError = AuraOnErrorLight,
            errorContainer = AuraErrorContainerLight,
            onErrorContainer = AuraOnErrorContainerLight,
            inverseSurface = AuraInverseSurfaceLight,
            inverseOnSurface = AuraInverseOnSurfaceLight,
            inversePrimary = AuraInversePrimaryLight,
            scrim = AuraScrim
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
        tertiaryContainer = animateColorAsState(target.tertiaryContainer, animSpec, label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(target.onTertiaryContainer, animSpec, label = "onTertiaryContainer").value,
        background = animateColorAsState(target.background, animSpec, label = "background").value,
        onBackground = animateColorAsState(target.onBackground, animSpec, label = "onBackground").value,
        surface = animateColorAsState(target.surface, animSpec, label = "surface").value,
        onSurface = animateColorAsState(target.onSurface, animSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(target.surfaceVariant, animSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(target.onSurfaceVariant, animSpec, label = "onSurfaceVariant").value,
        outline = animateColorAsState(target.outline, animSpec, label = "outline").value,
        outlineVariant = animateColorAsState(target.outlineVariant, animSpec, label = "outlineVariant").value,
        error = animateColorAsState(target.error, animSpec, label = "error").value,
        onError = animateColorAsState(target.onError, animSpec, label = "onError").value,
        errorContainer = animateColorAsState(target.errorContainer, animSpec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(target.onErrorContainer, animSpec, label = "onErrorContainer").value,
        inverseSurface = animateColorAsState(target.inverseSurface, animSpec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(target.inverseOnSurface, animSpec, label = "inverseOnSurface").value,
        inversePrimary = animateColorAsState(target.inversePrimary, animSpec, label = "inversePrimary").value,
        scrim = animateColorAsState(target.scrim, animSpec, label = "scrim").value
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
