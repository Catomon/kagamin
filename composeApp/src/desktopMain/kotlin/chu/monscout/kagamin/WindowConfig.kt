package chu.monscout.kagamin

object WindowConfig {
    const val WIDTH = 500
    const val HEIGHT = 340

    const val COMPACT_WIDTH = 192
    const val COMPACT_HEIGHT = 340

    const val TINY_WIDTH = 192
    const val TINY_HEIGHT = 200

    var isTransparent = false

    val isTraySupported = androidx.compose.ui.window.isTraySupported
}