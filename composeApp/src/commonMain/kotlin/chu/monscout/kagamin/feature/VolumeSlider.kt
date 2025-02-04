package chu.monscout.kagamin.feature

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.catomon.yukinotes.feature.Colors

@Composable
fun VolumeSlider(volumeChanged: (Float) -> Unit, modifier: Modifier = Modifier) {
    var volume by remember { mutableStateOf(0.5f) }
    val interactionSource = MutableInteractionSource()
    val colors = SliderDefaults.colors(
        Colors.noteBackground,
        Colors.bars,
        Colors.noteBackground,
        Colors.bars,
        Colors.bars,
        Colors.bars,
        Colors.bars,
        Colors.bars,
        Colors.bars,
        Colors.bars
    )
    Slider(
        value = volume,
        onValueChange = {
            volume = it
            volumeChanged(it)
        },
        colors = colors,
        modifier = modifier,
        interactionSource = interactionSource,
    )
}