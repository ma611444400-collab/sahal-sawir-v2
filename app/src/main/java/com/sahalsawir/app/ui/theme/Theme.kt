package com.sahalsawir.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SahalSawirColorScheme = darkColorScheme(
    primary = PrimarySlate,
    secondary = AccentTeal,
    tertiary = PremiumGold,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color(0xFF0F172A),
    onSecondary = DarkBackground,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = OutlineBorder,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SahalSawirColorScheme,
        typography = Typography,
        content = content
    )
}
