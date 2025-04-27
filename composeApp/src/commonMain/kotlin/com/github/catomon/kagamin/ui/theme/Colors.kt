package com.github.catomon.kagamin.ui.theme

import androidx.compose.ui.graphics.Color
import com.github.catomon.kagamin.loadSettings

object Colors {
    val themes = listOf(KagaminTheme.Violet, KagaminTheme.Pink,
        KagaminTheme.Blue, KagaminTheme.KagaminDark, KagaminTheme.White
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
    var materialColors = androidx.compose.material.Colors(
        primary = theme.buttonIcon,
        primaryVariant = theme.buttonIconTransparent,
        secondary = theme.buttonIconSmall,
        secondaryVariant = theme.buttonIconSmallSelected,
        background = background,
        surface = backgroundTransparent,
        error = lightRed,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White,
        false
    )
}