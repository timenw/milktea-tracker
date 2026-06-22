package com.timenw.milkteatracker.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val MilkTeaBrown = Color(0xFF8D6E63)
val MilkTeaDark = Color(0xFF3E2723)
val MilkTeaCream = Color(0xFFD7CCC8)
val MilkTeaGold = Color(0xFFBCAAA4)
val MilkTeaPink = Color(0xFFF8BBD0)
val MilkTeaSafe = Color(0xFF4CAF50)
val MilkTeaWarning = Color(0xFFFF9800)
val MilkTeaDanger = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = MilkTeaGold, onPrimary = MilkTeaDark, primaryContainer = MilkTeaBrown, onPrimaryContainer = MilkTeaCream,
    secondary = MilkTeaPink, onSecondary = MilkTeaDark, secondaryContainer = MilkTeaDark, onSecondaryContainer = MilkTeaCream,
    background = Color(0xFF1A120B), onBackground = MilkTeaCream, surface = Color(0xFF2C1810), onSurface = MilkTeaCream,
    surfaceVariant = Color(0xFF3E2723), onSurfaceVariant = MilkTeaCream, error = MilkTeaDanger, outline = MilkTeaBrown
)

@Composable
fun MilkTeaTrackerTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect { val window = (view.context as Activity).window; window.statusBarColor = DarkColorScheme.background.toArgb(); WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false }
    }
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography(), content = content)
}
