package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SkynetColorScheme = darkColorScheme(
    primary = SkynetAccentBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = SkynetAccentGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = SkynetAccentGreen,
    onTertiary = Color.White,
    background = SkynetDarkBg,
    onBackground = SkynetTextMain,
    surface = SkynetPanelBg,
    onSurface = SkynetTextMain,
    surfaceVariant = SkynetSurfaceVariant,
    onSurfaceVariant = SkynetTextDim,
    outline = SkynetPanelBorder,
    error = SkynetAccentRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SkynetColorScheme,
        typography = Typography,
        content = content
    )
}
