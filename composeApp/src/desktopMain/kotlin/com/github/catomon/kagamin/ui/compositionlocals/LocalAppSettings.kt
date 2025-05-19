package com.github.catomon.kagamin.ui.compositionlocals

import androidx.compose.runtime.compositionLocalOf
import com.github.catomon.kagamin.data.AppSettings

val LocalAppSettings = compositionLocalOf<AppSettings> {
    error("no LocalAppSettings provided")
}