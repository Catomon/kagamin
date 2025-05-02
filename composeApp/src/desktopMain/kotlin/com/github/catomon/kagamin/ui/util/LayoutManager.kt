package com.github.catomon.kagamin.ui.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class LayoutManager(
    val currentLayout: MutableState<Layout> = mutableStateOf(Layout.Default)
) {
    enum class Layout {
        Default,
        Compact,
        Tiny,
    }
}