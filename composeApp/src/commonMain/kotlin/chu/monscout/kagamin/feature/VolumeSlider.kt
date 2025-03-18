package chu.monscout.kagamin.feature

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import chu.monscout.kagamin.Colors

@Composable
fun VolumeSlider(volume: Float, volumeChanged: (Float) -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = MutableInteractionSource()
    val colors = SliderDefaults.colors(
        Colors.currentYukiTheme.playerButtonIcon,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIcon,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIconTransparent,
        Colors.currentYukiTheme.playerButtonIconTransparent
    )

    val shadowColors = SliderDefaults.colors(
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
        Colors.currentYukiTheme.thinBorder,
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