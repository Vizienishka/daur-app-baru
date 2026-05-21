package com.daur.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Brand Colors ──────────────────────────────────────────
val Primary       = Color(0xFF00694C)
val OnPrimary     = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFE0F2EE)
val Secondary     = Color(0xFF855400)
val OnSecondary   = Color(0xFFFFFFFF)
val Background    = Color(0xFFF8F9FA)
val Surface       = Color(0xFFFFFFFF)
val SurfaceContainer = Color(0xFFE7E8E9)
val OnSurface     = Color(0xFF1C1B1F)
val OnSurfaceVariant = Color(0xFF49454F)
val Outline       = Color(0xFF6D7A73)
val OutlineVariant = Color(0xFFCAC4D0)
val Error         = Color(0xFFBA1A1A)

private val DaurColorScheme = lightColorScheme(
    primary            = Primary,
    onPrimary          = OnPrimary,
    primaryContainer   = PrimaryContainer,
    secondary          = Secondary,
    onSecondary        = OnSecondary,
    background         = Background,
    surface            = Surface,
    onSurface          = OnSurface,
    onSurfaceVariant   = OnSurfaceVariant,
    outline            = Outline,
    outlineVariant     = OutlineVariant,
    error              = Error,
)

@Composable
fun DaurTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DaurColorScheme,
        typography  = Typography(),
        content     = content
    )
}
