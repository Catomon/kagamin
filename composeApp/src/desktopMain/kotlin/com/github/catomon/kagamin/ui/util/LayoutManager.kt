package com.github.catomon.kagamin.ui.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class LayoutManager(
    val currentLayout: MutableState<Layout> = mutableStateOf(Layout.Spacey)
) {
    enum class Layout {
        Old,
        OldCompact,
        OldTiny,
        Compact,
        Spacey(pFontScale = 1.5f);

        constructor(pFontScale: Float = 1.25f) {
            fontScale = pFontScale
        }

        var fontScale: Float = 1f
    }

    constructor(layout: Layout): this(mutableStateOf(layout))
}