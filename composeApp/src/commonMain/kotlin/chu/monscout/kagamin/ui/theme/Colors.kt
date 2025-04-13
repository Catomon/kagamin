package chu.monscout.kagamin.ui.theme

import androidx.compose.ui.graphics.Color
import chu.monscout.kagamin.loadSettings

object Colors {
    val themes = listOf(KagaminTheme.Violet, KagaminTheme.Pink,
        KagaminTheme.Blue, KagaminTheme.KagaminDark, KagaminTheme.White
    )

    var theme = themes.first()

    val surface get() = theme.surface
    val text get() = theme.font
    val text2 get() = theme.fontSecondary
    val background get() = theme.background
    val bars get() = theme.bars
    val barsTransparent get() = theme.barsTransparent

    init {
        theme =
            themes.find { it.name == loadSettings().theme } ?: themes.first()
    }

    private val lightRed = Color(245, 83, 95)
    var materialColors = androidx.compose.material.Colors(
        primary = bars,
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC6),
        secondaryVariant = Color(0xFF018786),
        background = background,
        surface = bars,
        error = lightRed,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.White,
        false
    )
}