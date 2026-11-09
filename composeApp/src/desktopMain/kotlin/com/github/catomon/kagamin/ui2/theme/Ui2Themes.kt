package com.github.catomon.kagamin.ui2.theme

import androidx.compose.ui.graphics.Color

/**
 * The palettes available to ui2. [Kagamin] is derived from
 * [com.github.catomon.kagamin.ui.theme.KagaminColors.Violet]; [Tsukaremi] mirrors
 * the Tsukaremi app's purple header + golden window design.
 */
object Ui2Themes {

    /** Kagamin's own violet palette. */
    val Kagamin = Ui2Colors(
        name = "kagamin",
        background = Color(0xFF6C33C7),
        backgroundEnd = Color(0xFF5529A3),
        onBackground = Color.White,
        header = Color(0xFF5529A3),
        headerContent = Color.White,
        accent = Color(0xFFA473FF),
        onAccent = Color.White,
        card = Color(0xCDA774FF),
        cardContent = Color.White,
        cardBorder = Color(0xFF5529A3),
        outline = Color(0xFF5529A3),
        text = Color.White,
        textSecondary = Color(0xFFDECCFF),
        windowBorderStart = Color(0xFFA473FF),
        windowBorderEnd = Color(0xFFA473FF),
    )

    /** Tsukaremi's golden window + purple header. */
    val Tsukaremi = Ui2Colors(
        name = "tsukaremi",
        background = Color(0xFFFAC469),
        backgroundEnd = Color(0xFFF0A343),
        onBackground = Color(0xFF3E0A87),
        header = Color(0xFF7F57C6),
        headerContent = Color.White,
        accent = Color(0xFFF39534),
        onAccent = Color(0xFF3E0A87),
        card = Color(0xFF7F57C6),
        cardContent = Color.White,
        cardBorder = Color(0xFFF39534),
        outline = Color(0xFF7F57C6),
        text = Color.White,
        textSecondary = Color(0xFFFDEB9E),
        windowBorderStart = Color(0xFF7F57C6),
        windowBorderEnd = Color(0xFFF39534),
    )

    /** All palettes, in toggle order. */
    val all = listOf(
        Kagamin,
        KagaminPink,
        KagaminBlue,
        KagaminDark,
        KagaminWhite,
        KagaminGrey,
        Tsukaremi,
    )
}
