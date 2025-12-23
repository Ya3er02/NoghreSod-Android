package com.noghre.sod.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import android.content.Context

private val primaryColor = Color(0xFF32B8C6)
private val primaryDark = Color(0xFF1A6873)
private val secondary = Color(0xFF5E5240)
private val tertiaryColor = Color(0xFFCA7E58)

private val errorColor = Color(0xFFC0152F)
private val errorDark = Color(0xFFFF5459)

private val lightColorScheme = lightColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB3E8F0),
    onPrimaryContainer = Color(0xFF002022),
    secondary = secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDEC8BE),
    onSecondaryContainer = Color(0xFF27160C),
    tertiary = tertiaryColor,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDCC4),
    onTertiaryContainer = Color(0xFF2F1500),
    error = errorColor,
    errorContainer = Color(0xFFF9DEDC),
    onError = Color.White,
    onErrorContainer = errorColor,
    background = Color(0xFFFCFCF9),
    onBackground = Color(0xFF134252),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF134252),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF626C71),
    outline = Color(0xFF6A7471),
    outlineVariant = Color(0xFFB8B8B8),
    inverseSurface = Color(0xFF0D3B45),
    inverseOnSurface = Color(0xFFEBF4F7),
)

private val darkColorScheme = darkColorScheme(
    primary = primaryColor,
    onPrimary = Color(0xFF003638),
    primaryContainer = Color(0xFF1A6873),
    onPrimaryContainer = Color(0xFFB3E8F0),
    secondary = secondary,
    onSecondary = Color(0xFF2F1500),
    secondaryContainer = Color(0xFF6F5D54),
    onSecondaryContainer = Color(0xFFFFDBD0),
    tertiary = tertiaryColor,
    onTertiary = Color(0xFF120700),
    tertiaryContainer = Color(0xFF8B6239),
    onTertiaryContainer = Color(0xFFFFDCC4),
    error = errorDark,
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF001D1F),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF134252),
    onBackground = Color(0xFFEBF4F7),
    surface = Color(0xFF262828),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF474747),
    onSurfaceVariant = Color(0xFFC7C8C8),
    outline = Color(0xFF909191),
    outlineVariant = Color(0xFF474747),
    inverseSurface = Color(0xFFEBF4F7),
    inverseOnSurface = Color(0xFF0D3B45),
)

@Composable
fun NoghreSodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> {
            // Use dynamic dark theme if available (Android 12+)
            darkColorScheme
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !darkTheme -> {
            // Use dynamic light theme if available (Android 12+)
            lightColorScheme
        }
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NoghreSodTypography,
        shapes = NoghreSodShapes,
        content = content
    )
}
