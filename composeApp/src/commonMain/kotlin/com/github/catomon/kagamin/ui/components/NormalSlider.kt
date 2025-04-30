package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier
) {
    Slider(
        modifier = modifier,
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        colors = colors,
        interactionSource = interactionSource,
        track = { sliderState ->
            val fraction = (sliderState.value - sliderState.valueRange.start) /
                    (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(colors.inactiveTrackColor, CircleShape)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .background(colors.activeTrackColor, CircleShape)
                )
            }
        },
        thumb = {
            Box(
                Modifier
                    .size(16.dp)
                    .background(colors.thumbColor, shape = CircleShape)
            )
        }
    )
}