package com.github.catomon.kagamin

import java.awt.Dimension

object WindowConfig {
    const val WIDTH = 470
    const val HEIGHT = 340

    const val COMPACT_WIDTH = 208
    const val COMPACT_HEIGHT = 340

    const val TINY_WIDTH = 180
    const val TINY_HEIGHT = 200

    const val BOTTOM_CONTROLS_WIDTH = 500
    const val BOTTOM_CONTROLS_HEIGHT = 500

    var isTransparent = false

    val isTraySupported = androidx.compose.ui.window.isTraySupported

    val minSize = Dimension(TINY_WIDTH, TINY_HEIGHT)
    val maxSize = Dimension(770, 770)
}