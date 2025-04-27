package com.github.catomon.kagamin.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import com.github.catomon.kagamin.LocalSnackbarHostState
import kagamin.composeapp.generated.resources.BadComic_Regular
import kagamin.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun KagaminTheme(content: @Composable () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalSnackbarHostState provides snackbarHostState,
    ) {
        MaterialTheme(
            colors = Colors.materialColors,
            typography = Typography(FontFamily(Font(Res.font.BadComic_Regular))),
            content = content
        )
    }
}
