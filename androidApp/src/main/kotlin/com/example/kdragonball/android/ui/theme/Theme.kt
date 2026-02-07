package com.example.kdragonball.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dragon Ball themed colors
val DragonBallOrange = Color(0xFFFF6B35)
val DragonBallBlue = Color(0xFF2A9DF4)
val DragonBallYellow = Color(0xFFFFD23F)
val DragonBallDarkOrange = Color(0xFFD94E2A)
val DragonBallLightOrange = Color(0xFFFFB084)
val DragonBallPurple = Color(0xFF6A4C93)
val DragonBallDarkBlue = Color(0xFF1E3A8A)
val DragonBallGold = Color(0xFFFFA500)

private val LightColorScheme =
    lightColorScheme(
        primary = DragonBallOrange,
        onPrimary = Color.White,
        primaryContainer = DragonBallLightOrange,
        onPrimaryContainer = DragonBallDarkOrange,
        secondary = DragonBallBlue,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFB3E5FC),
        onSecondaryContainer = DragonBallDarkBlue,
        tertiary = DragonBallYellow,
        onTertiary = Color.Black,
        background = Color(0xFFFFF8F0),
        onBackground = Color(0xFF2D2D2D),
        surface = Color.White,
        onSurface = Color(0xFF2D2D2D),
        surfaceVariant = Color(0xFFFFF4E8),
        onSurfaceVariant = Color(0xFF5D5D5D),
        error = Color(0xFFD32F2F),
        onError = Color.White
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = DragonBallOrange,
        onPrimary = Color.Black,
        primaryContainer = DragonBallDarkOrange,
        onPrimaryContainer = DragonBallLightOrange,
        secondary = DragonBallBlue,
        onSecondary = Color.Black,
        secondaryContainer = DragonBallDarkBlue,
        onSecondaryContainer = Color(0xFFB3E5FC),
        tertiary = DragonBallYellow,
        onTertiary = Color.Black,
        background = Color(0xFF1A1A1A),
        onBackground = Color(0xFFE8E8E8),
        surface = Color(0xFF2D2D2D),
        onSurface = Color(0xFFE8E8E8),
        surfaceVariant = Color(0xFF3D3D3D),
        onSurfaceVariant = Color(0xFFCCCCCC),
        error = Color(0xFFEF5350),
        onError = Color.Black
    )

@Composable
fun AppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
