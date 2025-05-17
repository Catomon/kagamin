package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_playlist
import kagamin.composeapp.generated.resources.repeat_single
import kagamin.composeapp.generated.resources.volume
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaybackOptionsButtons(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(buttonsSize).fillMaxWidth()
        ) {
            RepeatTrackPlaybackButton(buttonsSize = buttonsSize, viewModel = viewModel)

            RepeatPlaylistPlaybackButton(buttonsSize = buttonsSize, viewModel = viewModel)

            RandomPlaybackButton(viewModel, buttonsSize)
        }
    }
}

@Composable
fun RepeatTrackPlaybackButton(
    viewModel: KagaminViewModel,
    buttonsSize: Dp = 32.dp,
) {
    val playMode by viewModel.playMode.collectAsState()

    IconButton({
        viewModel.setPlayMode(
            if (playMode != PlaylistsManager.PlayMode.REPEAT_TRACK) PlaylistsManager.PlayMode.REPEAT_TRACK
            else PlaylistsManager.PlayMode.PLAYLIST
        )
    }, modifier = Modifier.size(buttonsSize)) {
        ImageWithShadow(
            painterResource(Res.drawable.repeat_single),
            "Toggle repeat track",
            colorFilter = if (playMode == PlaylistsManager.PlayMode.REPEAT_TRACK) ColorFilter.tint(
                KagaminTheme.colors.buttonIcon
            )
            else ColorFilter.tint(KagaminTheme.colors.buttonIconTransparent)
        )
    }
}

@Composable
fun RepeatPlaylistPlaybackButton(
    viewModel: KagaminViewModel,
    buttonsSize: Dp = 32.dp,
) {
    val playMode by viewModel.playMode.collectAsState()

    IconButton({
        viewModel.setPlayMode(
            if (playMode != PlaylistsManager.PlayMode.REPEAT_PLAYLIST) PlaylistsManager.PlayMode.REPEAT_PLAYLIST
            else PlaylistsManager.PlayMode.PLAYLIST
        )
    }, modifier = Modifier.size(buttonsSize)) {
        ImageWithShadow(
            painterResource(Res.drawable.repeat_playlist),
            "Toggle repeat playlist",
            colorFilter = if (playMode == PlaylistsManager.PlayMode.REPEAT_PLAYLIST) ColorFilter.tint(
                KagaminTheme.colors.buttonIcon
            )
            else ColorFilter.tint(KagaminTheme.colors.buttonIconTransparent)
        )
    }
}

@Composable
fun RandomPlaybackButton(
    viewModel: KagaminViewModel,
    buttonsSize: Dp = 32.dp
) {
    val playMode by viewModel.playMode.collectAsState()

    IconButton({
        viewModel.setPlayMode(
            if (playMode != PlaylistsManager.PlayMode.RANDOM) PlaylistsManager.PlayMode.RANDOM
            else PlaylistsManager.PlayMode.PLAYLIST
        )
    }, modifier = Modifier.size(buttonsSize)) {
        ImageWithShadow(
            painterResource(Res.drawable.random),
            "Toggle random mode",
            colorFilter = if (playMode == PlaylistsManager.PlayMode.RANDOM) ColorFilter.tint(
                KagaminTheme.colors.buttonIcon
            )
            else ColorFilter.tint(KagaminTheme.colors.buttonIconTransparent)
        )
    }
}

@Composable
fun VolumeOptions(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp,
) {
    var oldVolume by remember { mutableStateOf(volume) }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        IconButton({
            onVolumeChange(if (volume > 0f) 0f else oldVolume)
        }, modifier = Modifier.size(buttonsSize)) {
            ImageWithShadow(
                painterResource(Res.drawable.volume),
                "volume",
                colorFilter = if (volume > 0f) ColorFilter.tint(KagaminTheme.colors.buttonIcon)
                else ColorFilter.tint(KagaminTheme.colors.buttonIconTransparent)
            )
        }

        VolumeSlider(
            volume,
            onVolumeChange = {
                oldVolume = it
                onVolumeChange(it)
            },
            Modifier.fillMaxWidth()
        )
    }
}
