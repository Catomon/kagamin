package chu.monscout.kagamin

object WindowConfig {
    const val WIDTH = 516
    const val HEIGHT = 356

    const val COMPACT_WIDTH = 208
    const val COMPACT_HEIGHT = 356

    const val TINY_WIDTH = 208
    const val TINY_HEIGHT = 216

    var isTransparent = false

    val isTraySupported = androidx.compose.ui.window.isTraySupported
}