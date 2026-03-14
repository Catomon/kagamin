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
        Spacey,
    }

    constructor(layout: Layout): this(mutableStateOf(layout))
}