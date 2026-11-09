package com.github.catomon.kagamin.ui2.theme

import androidx.compose.ui.graphics.Color

/**
 * Color slots for the new ui2 design system.
 *
 * Two palettes ship by default in [Ui2Themes]: Kagamin's own violet theme and
 * Tsukaremi's golden/purple theme. Add more by appending to [Ui2Themes.all].
 */
data class Ui2Colors(
    val name: String,
    val background: Color,
    val backgroundEnd: Color,
    val onBackground: Color,
    val header: Color,
    val headerContent: Color,
    val accent: Color,
    val onAccent: Color,
    val card: Color,
    val cardContent: Color,
    val cardBorder: Color,
    val outline: Color,
    val text: Color,
    val textSecondary: Color,
    val windowBorderStart: Color,
    val windowBorderEnd: Color,
    val glow: Color = Color.White,
)
