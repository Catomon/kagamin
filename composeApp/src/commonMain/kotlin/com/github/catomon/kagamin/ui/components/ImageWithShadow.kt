package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme

@Composable
@Deprecated("Not needed anymore. Delete later.", replaceWith = ReplaceWith("Icon"), level = DeprecationLevel.WARNING)
fun ImageWithShadow(
    painterResource: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter
) {
    val pad = with(LocalDensity.current) { 2.dp.toPx() }

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
//        Image(
//            painterResource,
//            contentDescription = contentDescription,
//            colorFilter = ColorFilter.tint(KagaminTheme.colors.shadow),
//            modifier = Modifier.graphicsLayer(translationY = pad).fillMaxSize()
//        )

        Image(
            painterResource,
            contentDescription = contentDescription,
            colorFilter = colorFilter,
            modifier = Modifier.fillMaxSize()
        )
    }
}
