package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.github.catomon.kagamin.ui.theme.Colors

@Composable
fun VolumeSlider(volume: Float, volumeChanged: (Float) -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = MutableInteractionSource()
    val colors = SliderDefaults.colors(
        Colors.theme.buttonIcon,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIcon,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIconTransparent,
        Colors.theme.buttonIconTransparent
    )

    val shadowColors = SliderDefaults.colors(
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
        Colors.theme.thinBorder,
    )


    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Slider(
            value = volume,
            onValueChange = {

            },
            colors = shadowColors,
            interactionSource = interactionSource,
            modifier = Modifier.graphicsLayer(translationY = 1.5f)
        )

        Slider(
            value = volume,
            onValueChange = {
                volumeChanged(it)
            },
            colors = colors,
            interactionSource = interactionSource,
        )
    }
}