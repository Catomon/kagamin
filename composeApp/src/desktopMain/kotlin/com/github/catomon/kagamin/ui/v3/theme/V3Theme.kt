package com.github.catomon.kagamin.ui.v3.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import kagamin.composeapp.generated.resources.Fluttershy_Regular
import kagamin.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

val LocalV3Colors = staticCompositionLocalOf { V3Themes.default }
val LocalV3ThemeState = staticCompositionLocalOf<V3ThemeState> {
    error("No V3ThemeState provided")
}

class V3ThemeState(initial: V3Colors = V3Themes.default) {
    var colors by mutableStateOf(initial)
        private set

    fun select(theme: V3Colors) {
        colors = theme
    }

    fun next(): V3Colors {
        val index = V3Themes.all.indexOfFirst { it.name == colors.name }
        colors = V3Themes.all[(index + 1).mod(V3Themes.all.size)]
        return colors
    }
}

@Composable
fun rememberV3ThemeState(initial: V3Colors = V3Themes.default): V3ThemeState =
    remember { V3ThemeState(initial) }

@Composable
fun V3Theme(state: V3ThemeState, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalV3Colors provides state.colors,
        LocalV3ThemeState provides state,
    ) {
        MaterialTheme(
            colorScheme = state.colors.toColorScheme(),
            typography = v3Typography(),
            content = content,
        )
    }
}

private fun V3Colors.toColorScheme() = darkColorScheme(
    primary = accent,
    onPrimary = onAccent,
    secondary = accentSoft,
    onSecondary = onAccent,
    background = backgroundMid,
    onBackground = text,
    surface = panelStart,
    onSurface = panelContent,
    surfaceVariant = itemStart,
    onSurfaceVariant = itemContent,
    outline = panelBorderStart,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
private fun v3Typography(): Typography {
    val t = MaterialTheme.typography
    val fontFamily = FontFamily(Font(Res.font.Fluttershy_Regular))
    return t.copy(
        displayLarge = t.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = t.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = t.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = t.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = t.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = t.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = t.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = t.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = t.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = t.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = t.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = t.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = t.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = t.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = t.labelSmall.copy(fontFamily = fontFamily),
    )
}

object V3Theme {
    val colors: V3Colors
        @Composable get() = LocalV3Colors.current

    val state: V3ThemeState
        @Composable get() = LocalV3ThemeState.current
}
