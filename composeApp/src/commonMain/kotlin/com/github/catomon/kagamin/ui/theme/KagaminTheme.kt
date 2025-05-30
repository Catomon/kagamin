package com.github.catomon.kagamin.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.data.loadSettings
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
            colorScheme = KagaminTheme.colorScheme,
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

    var colors = themes.first()

    val text get() = colors.text
    val textSecondary get() = colors.textSecondary
    val behindBackground get() = colors.behindBackground
    val background get() = colors.background
    val backgroundTransparent get() = colors.backgroundTransparent

    init {
        colors =
            themes.find { it.name == loadSettings().theme } ?: themes.first()
    }

    private val lightRed = Color(245, 83, 95)
    val colorScheme: ColorScheme
        @Composable get() = with(MaterialTheme.colorScheme) {
            copy(
                primary = colors.buttonIcon,
                primaryContainer = colors.buttonIconInactive,
                secondary = colors.buttonIconSmall,
                secondaryContainer = colors.buttonIconSmallSelected,
                background = background,
                surface = backgroundTransparent,
                error = lightRed,
                onPrimary = Color.White,
                onSecondary = Color.White,
                onBackground = Color.White,
                onSurface = Color.White,
                onError = Color.White,
                onPrimaryContainer = this.onPrimaryContainer,
                inversePrimary = this.inversePrimary,
                onSecondaryContainer = this.onSecondaryContainer,
                tertiary = this.tertiary,
                onTertiary = this.onTertiary,
                tertiaryContainer = this.tertiaryContainer,
                onTertiaryContainer = this.onTertiaryContainer,
                surfaceVariant = colors.backgroundTransparent,
                onSurfaceVariant = colors.buttonIcon,
                surfaceTint = this.surfaceTint,
                inverseSurface = this.inverseSurface,
                inverseOnSurface = this.inverseOnSurface,
                errorContainer = this.errorContainer,
                onErrorContainer = this.onErrorContainer,
                outline = colors.buttonIcon,
                outlineVariant = this.outlineVariant,
                scrim = this.scrim,
                surfaceBright = this.surfaceBright,
                surfaceDim = this.surfaceDim,
                surfaceContainer = this.surfaceContainer,
                surfaceContainerHigh = this.surfaceContainerHigh,
                surfaceContainerHighest = colors.behindBackground,
                surfaceContainerLow = this.surfaceContainerLow,
                surfaceContainerLowest = this.surfaceContainerLowest,
            )
        }

    val fontSize = 12.sp

    val fontFamily @Composable get() = FontFamily(Font(Res.font.BadComic_Regular))

    val typography
        @Composable get() = let {
            val t = MaterialTheme.typography
            val fontFamily = fontFamily
            t.copy(
                displayLarge = t.displayLarge.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                displayMedium = t.displayMedium.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                displaySmall = t.displaySmall.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                headlineLarge = t.headlineLarge.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                headlineMedium = t.headlineMedium.copy(
                    fontFamily = fontFamily,
                    color = colors.text,
                    fontSize = fontSize
                ),
                headlineSmall = t.headlineSmall.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                titleLarge = t.titleLarge.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                titleMedium = t.titleMedium.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                titleSmall = t.titleSmall.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                bodyLarge = t.bodyLarge.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                bodyMedium = t.bodyMedium.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                bodySmall = t.bodySmall.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                labelLarge = t.labelLarge.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                labelMedium = t.labelMedium.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize),
                labelSmall = t.labelSmall.copy(fontFamily = fontFamily, color = colors.text, fontSize = fontSize)
            )
        }
}