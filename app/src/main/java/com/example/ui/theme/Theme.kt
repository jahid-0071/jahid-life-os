package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ObsidianDarkColorScheme = darkColorScheme(
    primary = AccentIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = AccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = AccentPurple,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianSurfaceBorder
)

private val AppleMinimalLightColorScheme = lightColorScheme(
    primary = Color(0xFF1E293B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightSurfaceBorder
)

@Composable
fun LifeOsTheme(
    darkTheme: Boolean = true, // Default to gorgeous Midnight Obsidian
    themeName: String = "Midnight Glass",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName) {
        "Apple Minimal" -> if (darkTheme) ObsidianDarkColorScheme else AppleMinimalLightColorScheme
        "Cyberpunk" -> darkColorScheme(
            primary = Color(0xFF00F5D4),
            secondary = Color(0xFFBD00FF),
            tertiary = Color(0xFFFF0055),
            background = Color(0xFF05050A),
            surface = Color(0xFF10101C),
            surfaceVariant = Color(0xFF1A1A2E),
            outline = Color(0xFF2A2A4A)
        )
        "Ocean" -> darkColorScheme(
            primary = Color(0xFF00B4D8),
            secondary = Color(0xFF0077B6),
            tertiary = Color(0xFF90E0EF),
            background = Color(0xFF03071E),
            surface = Color(0xFF0A192F),
            surfaceVariant = Color(0xFF112240),
            outline = Color(0xFF233554)
        )
        "Forest" -> darkColorScheme(
            primary = Color(0xFF10B981),
            secondary = Color(0xFF059669),
            tertiary = Color(0xFF34D399),
            background = Color(0xFF061510),
            surface = Color(0xFF0F261E),
            surfaceVariant = Color(0xFF193B2F),
            outline = Color(0xFF2B5342)
        )
        else -> ObsidianDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
