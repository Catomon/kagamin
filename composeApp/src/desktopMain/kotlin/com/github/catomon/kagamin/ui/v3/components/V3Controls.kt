package com.github.catomon.kagamin.ui.v3.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.ui.components.NormalSlider
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.pause_star
import kagamin.composeapp.generated.resources.play_star
import kagamin.composeapp.generated.resources.playlist
import kagamin.composeapp.generated.resources.prev
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_playlist
import kagamin.composeapp.generated.resources.repeat_single
import kagamin.composeapp.generated.resources.single
import kagamin.composeapp.generated.resources.volume
import org.jetbrains.compose.resources.painterResource

/** Two-layer icon: a slightly larger outline behind the tinted fill. */
@Composable
fun V3Icon(
    painter: Painter,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    outlineColor: Color? = null,
    outlineScale: Float = 1.18f,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        if (outlineColor != null) {
            Image(
                painter = painter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(outlineColor),
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = outlineScale, scaleY = outlineScale),
            )
        }
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = ColorFilter.tint(tint),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun V3ControlButton(
    painter: Painter,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    iconSize: Dp = 19.dp,
    contentColor: Color = V3Theme.colors.controlContent,
) {
    val colors = V3Theme.colors
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(colors.controlStart, colors.controlEnd)))
            .border(2.dp, colors.controlContent.copy(alpha = 0.22f), RoundedCornerShape(8.dp))
            .clickable(onClickLabel = description, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        V3Icon(
            painter = painter,
            tint = contentColor,
            size = iconSize,
            outlineColor = colors.headerOutline,
        )
    }
}

@Composable
fun V3PlayPauseButton(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val colors = V3Theme.colors
    val playState by viewModel.playState.collectAsState()
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(colors.controlStart, colors.controlEnd)))
            .border(2.dp, colors.controlContent.copy(alpha = 0.22f), shape)
            .clickable(onClickLabel = "Play or pause", onClick = viewModel::onPlayPause),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(playState) { state ->
            V3Icon(
                painter = painterResource(
                    if (state == AudioPlayerService.PlayState.PLAYING) Res.drawable.pause_star
                    else Res.drawable.play_star
                ),
                tint = colors.controlContent,
                size = size * 0.52f,
            )
        }
    }
}

@Composable
fun V3TransportControls(
    viewModel: KagaminViewModel,
    volume: Float,
    modifier: Modifier = Modifier,
) {
    val playMode by viewModel.playMode.collectAsState()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        V3ControlButton(
            painter = painterResource(
                when (playMode) {
                    PlaylistsManager.PlayMode.PLAYLIST -> Res.drawable.playlist
                    PlaylistsManager.PlayMode.REPEAT_PLAYLIST -> Res.drawable.repeat_playlist
                    PlaylistsManager.PlayMode.REPEAT_TRACK -> Res.drawable.repeat_single
                    PlaylistsManager.PlayMode.RANDOM -> Res.drawable.random
                    PlaylistsManager.PlayMode.ONCE -> Res.drawable.single
                }
            ),
            description = "Change play mode",
            onClick = { viewModel.togglePlayMode() },
        )

        V3ControlButton(
            painter = painterResource(Res.drawable.prev),
            description = "Previous track",
            onClick = viewModel::prevTrack,
        )

        V3PlayPauseButton(viewModel = viewModel)

        V3ControlButton(
            painter = painterResource(Res.drawable.next),
            description = "Next track",
            onClick = viewModel::nextTrack,
        )

        V3VolumeControl(
            volume = volume,
            onVolumeChange = viewModel::setVolume,
        )
    }
}

@Composable
fun V3VolumeControl(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = V3Theme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var lastVolume by remember { mutableStateOf(if (volume > 0f) volume else 0.3f) }

    Row(
        modifier = modifier
            .height(38.dp)
            .hoverable(interactionSource),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        V3ControlButton(
            painter = painterResource(Res.drawable.volume),
            description = if (volume > 0f) "Mute" else "Unmute",
            onClick = { onVolumeChange(if (volume > 0f) 0f else lastVolume) },
            contentColor = if (volume > 0f) colors.controlContent else colors.disabled,
        )

        AnimatedVisibility(isHovered) {
            NormalSlider(
                value = volume,
                onValueChange = {
                    lastVolume = it
                    onVolumeChange(it)
                },
                colors = v3SliderColors(),
                modifier = Modifier
                    .width(64.dp)
                    .padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun v3SliderColors(): SliderColors {
    val colors = V3Theme.colors
    return SliderDefaults.colors(
        thumbColor = colors.accentSoft,
        activeTrackColor = colors.accent,
        activeTickColor = colors.accent,
        inactiveTrackColor = colors.controlEnd,
        inactiveTickColor = colors.controlEnd,
        disabledThumbColor = colors.disabled,
        disabledActiveTrackColor = colors.disabled,
        disabledActiveTickColor = colors.disabled,
        disabledInactiveTrackColor = colors.controlEnd,
        disabledInactiveTickColor = colors.controlEnd,
    )
}
