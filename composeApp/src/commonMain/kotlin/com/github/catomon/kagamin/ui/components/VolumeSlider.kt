package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.github.catomon.kagamin.ui.theme.KagaminTheme

@Composable
fun VolumeSlider(volume: Float, onVolumeChange: (Float) -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = SliderDefaults.colors(
        thumbColor = KagaminTheme.colors.buttonIcon,
        activeTrackColor =   KagaminTheme.colors.buttonIcon,
        activeTickColor = KagaminTheme.colors.buttonIcon,
        inactiveTrackColor = KagaminTheme.colors.buttonIconTransparent,
        inactiveTickColor = KagaminTheme.colors.buttonIconTransparent,
        disabledThumbColor = KagaminTheme.colors.buttonIconTransparent,
        disabledActiveTrackColor = KagaminTheme.colors.buttonIconTransparent,
        disabledActiveTickColor = KagaminTheme.colors.buttonIconTransparent,
        disabledInactiveTrackColor = KagaminTheme.colors.buttonIconTransparent,
        disabledInactiveTickColor = KagaminTheme.colors.buttonIconTransparent
    )

    val shadowColors = SliderDefaults.colors(
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
        KagaminTheme.colors.thinBorder,
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        NormalSlider(volume, onVolumeChange, shadowColors, interactionSource, modifier = Modifier.graphicsLayer(translationY = 1.5f))

        NormalSlider(
            volume,
            onVolumeChange,
            colors,
            interactionSource
        )
    }
}

