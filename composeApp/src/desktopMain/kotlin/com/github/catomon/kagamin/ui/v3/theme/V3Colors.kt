package com.github.catomon.kagamin.ui.v3.theme

import androidx.compose.ui.graphics.Color

/**
 * Color slots for the Tsukaremi-inspired v3 design.
 *
 * The layout uses opaque gradients instead of the translucent surfaces of the
 * older layouts, so most slots are meant to be used at full alpha.
 */
data class V3Colors(
    val name: String,
    val displayName: String,

    // Window / page background
    val backgroundStart: Color,
    val backgroundMid: Color,
    val backgroundEnd: Color,

    // Title bar
    val headerStart: Color,
    val headerEnd: Color,
    val headerContent: Color,
    val headerOutline: Color,

    // Side panels
    val panelStart: Color,
    val panelEnd: Color,
    val panelBorderStart: Color,
    val panelBorderEnd: Color,
    val panelContent: Color,
    val panelContentSecondary: Color,

    // List entries
    val itemStart: Color,
    val itemEnd: Color,
    val itemContent: Color,
    val itemContentSecondary: Color,
    val itemSelectedStart: Color,
    val itemSelectedEnd: Color,
    val itemSelectedContent: Color,
    val itemSelectedContentSecondary: Color,
    val itemSelectedOutline: Color,
    val itemSelectedIcon: Color,

    // Accents and text over the page background
    val accent: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val text: Color,
    val textSecondary: Color,
    val outline: Color,
    val disabled: Color,

    // Player controls
    val controlStart: Color,
    val controlEnd: Color,
    val controlContent: Color,
    val playStart: Color,
    val playEnd: Color,
    val playContent: Color,

    // Window frame
    val windowBorderStart: Color,
    val windowBorderEnd: Color,
    val glow: Color,

    // Falling stars
    val starColors: List<Color>,
)
