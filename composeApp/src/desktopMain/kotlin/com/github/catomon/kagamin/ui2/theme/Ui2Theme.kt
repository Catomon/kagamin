package com.github.catomon.kagamin.ui2.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalUi2Colors = staticCompositionLocalOf { Ui2Themes.Kagamin }

/**
 * Holds the active [Ui2Colors]. Call [toggle] / [next] / [select] to switch
 * themes at runtime; any composable reading [Ui2Theme.colors] recomposes.
 */
class Ui2ThemeState(initial: Ui2Colors = Ui2Themes.Kagamin) {
    var colors by mutableStateOf(initial)
        private set

    fun select(theme: Ui2Colors) {
        colors = theme
    }

    /** Cycles through [Ui2Themes.all]. */
    fun next() {
        val index = Ui2Themes.all.indexOfFirst { it.name == colors.name }
        colors = Ui2Themes.all[(index + 1).mod(Ui2Themes.all.size)]
    }

    /** Switches between the Kagamin and Tsukaremi palettes. */
    fun toggle() {
        colors = if (colors.name == Ui2Themes.Kagamin.name) Ui2Themes.Tsukaremi else Ui2Themes.Kagamin
    }
}

@Composable
fun rememberUi2ThemeState(initial: Ui2Colors = Ui2Themes.Kagamin): Ui2ThemeState =
    remember { Ui2ThemeState(initial) }

@Composable
fun Ui2Theme(state: Ui2ThemeState, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalUi2Colors provides state.colors) {
        MaterialTheme(colorScheme = state.colors.toColorScheme(), content = content)
    }
}

private fun Ui2Colors.toColorScheme() = darkColorScheme(
    primary = accent,
    onPrimary = onAccent,
    secondary = card,
    onSecondary = cardContent,
    background = background,
    onBackground = onBackground,
    surface = card,
    onSurface = cardContent,
    outline = cardBorder,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

/** Convenience accessor: `Ui2Theme.colors`. */
object Ui2Theme {
    val colors: Ui2Colors
        @Composable get() = LocalUi2Colors.current
}
