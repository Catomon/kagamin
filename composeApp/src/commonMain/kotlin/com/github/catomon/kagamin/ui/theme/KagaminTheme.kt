package com.github.catomon.kagamin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.loadSettings
import kagamin.composeapp.generated.resources.BadComic_Regular
import kagamin.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun KagaminTheme(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        MaterialTheme(
            colorScheme = KagaminTheme.materialColors,
            typography = KagaminTheme.typography,
            content = content
        )
    }
}

object KagaminTheme {
    val themes = listOf(
        KagaminColors.Violet, KagaminColors.Pink,
        KagaminColors.Blue, KagaminColors.KagaminDark, KagaminColors.White
    )

    var theme = themes.first()

    val text get() = theme.text
    val textSecondary get() = theme.textSecondary
    val behindBackground get() = theme.behindBackground
    val background get() = theme.background
    val backgroundTransparent get() = theme.backgroundTransparent

    init {
        theme =
            themes.find { it.name == loadSettings().theme } ?: themes.first()
    }

    private val lightRed = Color(245, 83, 95)
    val materialColors
        @Composable get() = MaterialTheme.colorScheme.copy(
            primary = theme.buttonIcon,
            primaryContainer = theme.buttonIconTransparent,
            secondary = theme.buttonIconSmall,
            secondaryContainer = theme.buttonIconSmallSelected,
            background = background,
            surface = backgroundTransparent,
            error = lightRed,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.White,
        )

    val fontFamily @Composable get() = FontFamily(Font(Res.font.BadComic_Regular))

    val typography
        @Composable get() = let {
            val t = MaterialTheme.typography
            val fontFamily = fontFamily
            t.copy(
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
                labelSmall = t.labelSmall.copy(fontFamily = fontFamily)
            )
        }
}