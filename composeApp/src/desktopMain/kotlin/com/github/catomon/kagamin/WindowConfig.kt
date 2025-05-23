package com.github.catomon.kagamin

object WindowConfig {
    const val WIDTH = 470
    const val HEIGHT = 340

    const val COMPACT_WIDTH = 208
    const val COMPACT_HEIGHT = 340

    const val TINY_WIDTH = 208
    const val TINY_HEIGHT = 208

    const val BOTTOM_CONTROLS_WIDTH = 600
    const val BOTTOM_CONTROLS_HEIGHT = 500

    var isTransparent = false

    val isTraySupported = androidx.compose.ui.window.isTraySupported
}