package chu.monscout.kagamin.feature

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import chu.monscout.kagamin.Colors

@Composable
fun VolumeSlider(volume: Float, volumeChanged: (Float) -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = MutableInteractionSource()
    val colors = SliderDefaults.colors(
        Colors.noteBackground,
        Colors.background,
        Colors.noteBackground,
        Colors.background,
        Colors.background,
        Colors.background,
        Colors.background,
        Colors.background,
        Colors.background,
        Colors.background
    )
    Slider(
        value = volume,
        onValueChange = {
            volumeChanged(it)
        },
        colors = colors,
        modifier = modifier,
        interactionSource = interactionSource,
    )
}