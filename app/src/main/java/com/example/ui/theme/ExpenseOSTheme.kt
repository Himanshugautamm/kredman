package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.ThemeStyle

private val NothingColorScheme = darkColorScheme(
    primary = NothingRed,
    onPrimary = Color.White,
    background = NothingBlack,
    surface = NothingDarkCard,
    onBackground = NothingWhite,
    onSurface = NothingWhite,
    surfaceVariant = NothingDotGray,
    secondary = NothingAccentGray
)

private val LumiaColorScheme = darkColorScheme(
    primary = LumiaBlue,
    onPrimary = Color.White,
    secondary = LumiaOrange,
    tertiary = LumiaPurple,
    background = Color(0xFF0F141C),
    surface = Color(0xFF1B2230),
    onBackground = Color.White,
    onSurface = Color.White
)

private val GlassColorScheme = darkColorScheme(
    primary = Color(0xFF80D8FF),
    onPrimary = Color.Black,
    background = Color(0xFF0A0E1A),
    surface = Color(0x1F263859),
    onBackground = Color.White,
    onSurface = Color.White
)

private val AmoledColorScheme = darkColorScheme(
    primary = AmoledAccent,
    onPrimary = Color.Black,
    background = AmoledBlack,
    surface = AmoledCard,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF111115),
    onPrimary = Color.White,
    background = Color(0xFFF7F7F9),
    surface = Color.White,
    onBackground = Color(0xFF111115),
    onSurface = Color(0xFF111115),
    secondary = Color(0xFF6E6E78)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ExpenseOSTheme(
    style: ThemeStyle = ThemeStyle.NOTHING,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when (style) {
        ThemeStyle.NOTHING -> NothingColorScheme
        ThemeStyle.LUMIA -> LumiaColorScheme
        ThemeStyle.GLASS -> GlassColorScheme
        ThemeStyle.AMOLED -> AmoledColorScheme
        ThemeStyle.LIGHT -> LightColorScheme
        ThemeStyle.DARK -> DarkColorScheme
        ThemeStyle.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                NothingColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
