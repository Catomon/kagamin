package com.github.catomon.yukinotes.feature

import LocalSnackbarHostState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import kagamin.composeapp.generated.resources.BadComic_Regular
import kagamin.composeapp.generated.resources.Res
import loadSettings
import org.jetbrains.compose.resources.Font

@Composable
fun YukiTheme(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        MaterialTheme(
            colors = Colors.yukiColors,
            typography = Typography(FontFamily(Font(Res.font.BadComic_Regular))),
            content = content
        )
    }
}

object Themes {
    val list = listOf(Violet, Pink, Blue)

    fun forName(name: String) = list.find { it.name == name }

    fun forNameOrFirst(name: String) = list.find { it.name == name } ?: list.first()

    object Pink : IYukiTheme {
        override val name: String = "yuki"
        override val background = Color(0xffe96c76)
        override val surface = Color(0xfff799b4)
        override val surfaceSecondary = Color(0xffee8890)
        override val font = Color(0xFFFFFFFF)
        override val fontSecondary = Color(0xFFFFE1EA)
        override val bars = Color(0xffdc5c73)
    }

    object Violet : IYukiTheme {
        override val name: String = "gami-kasa"
        override val background = Color(0xff6e4eaa)
        override val surface = Color(0xff916dd6)
        override val surfaceSecondary = Color(0xff8563cc)
        override val font = Color(0xFFFFFFFF)
        override val fontSecondary = Color(0xFFDECCFF)
        override val bars = Color(0xFF66419F)
    }

    object Blue : IYukiTheme {
        override val name: String = "nata"
        override val background = Color(0xff3a55af)
        override val surface = Color(0xff6197de)
        override val surfaceSecondary = Color(0xff3671c1)
        override val font = Color(0xFFFFFFFF)
        override val fontSecondary = Color(0xffd2e6ff)
        override val bars = Color(0xff0f2e93)
    }

    interface IYukiTheme {
        val name: String
        val background: Color
        val surface: Color
        val surfaceSecondary: Color
        val font: Color
        val fontSecondary: Color
        val bars: Color
    }
}

object Colors {
    var currentYukiTheme = Themes.list.first()

    private val violetDark = Color(50, 23, 131)
    private val violet = Color(111, 79, 171)
    private val lightRed = Color(245, 83, 95)

    var noteBackground = Color(0xff916dd6)
    var noteTextHeadline = Color(0xFFFFFFFF)
    var noteText = Color(0xFFDECCFF)
    var noteTextSmall = Color(0xFFDECCFF)
    var background = Color(0xff6e4eaa)
    var bars = Color(0xFF66419F)
    var dividers = Color(0xff8563cc)

    init {
        currentYukiTheme = Themes.list.find { it.name == loadSettings().theme } ?: Themes.list.first()
        updateTheme()
    }

    fun updateTheme() {
        noteBackground = currentYukiTheme.surface
        noteTextHeadline = currentYukiTheme.font
        noteText = currentYukiTheme.fontSecondary
        noteTextSmall = currentYukiTheme.fontSecondary
        background = currentYukiTheme.background
        bars = currentYukiTheme.bars
        dividers = currentYukiTheme.surfaceSecondary
    }

    val yukiColors = androidx.compose.material.Colors(
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