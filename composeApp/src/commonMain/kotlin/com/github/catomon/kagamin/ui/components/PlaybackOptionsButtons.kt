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
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_playlist
import kagamin.composeapp.generated.resources.repeat_single
import kagamin.composeapp.generated.resources.volume
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaybackOptionsButtons(
    player: AudioPlayer<AudioTrack>,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp
) {
    var playMode by player.playMode
    var volume by player.volume
    var showVolumeSlider by remember { mutableStateOf(false) }
    var timer by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(showVolumeSlider, volume) {
        timer = 0f
        while (true) {
            timer += 0.1f
            if (timer >= 3f) {
                showVolumeSlider = false
            }

            delay(100)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(buttonsSize).fillMaxWidth()
        ) {
            RepeatTrackPlaybackButton(buttonsSize = buttonsSize, player = player)

            RepeatPlaylistPlaybackButton(buttonsSize = buttonsSize, player = player)

            RandomPlaybackButton(player, buttonsSize)
        }
    }
}

@Composable
fun RepeatTrackPlaybackButton(
    player: AudioPlayer<AudioTrack>,
    buttonsSize: Dp = 32.dp,
) {
    val playMode: AudioPlayer.PlayMode = player.playMode.value

    IconButton({
        player.playMode.value =
            if (playMode != AudioPlayer.PlayMode.REPEAT_TRACK) AudioPlayer.PlayMode.REPEAT_TRACK
            else AudioPlayer.PlayMode.PLAYLIST
    }, modifier = Modifier.size(buttonsSize)) {
        ImageWithShadow(
            painterResource(Res.drawable.repeat_single),
            "Toggle repeat track",
            colorFilter = if (playMode == AudioPlayer.PlayMode.REPEAT_TRACK) ColorFilter.tint(
                KagaminTheme.theme.buttonIcon
            )
            else ColorFilter.tint(KagaminTheme.theme.buttonIconTransparent)
        )
    }
}

@Composable
fun RepeatPlaylistPlaybackButton(
    player: AudioPlayer<AudioTrack>,
    buttonsSize: Dp = 32.dp,
) {
    val playMode: AudioPlayer.PlayMode = player.playMode.value

    IconButton({
        player.playMode.value =
            if (playMode != AudioPlayer.PlayMode.REPEAT_PLAYLIST) AudioPlayer.PlayMode.REPEAT_PLAYLIST
            else AudioPlayer.PlayMode.PLAYLIST
    }, modifier = Modifier.size(buttonsSize)) {
        ImageWithShadow(
            painterResource(Res.drawable.repeat_playlist),
            "Toggle repeat playlist",
            colorFilter = if (playMode == AudioPlayer.PlayMode.REPEAT_PLAYLIST) ColorFilter.tint(
                KagaminTheme.theme.buttonIcon
            )
            else ColorFilter.tint(KagaminTheme.theme.buttonIconTransparent)
        )
    }
}

@Composable
fun RandomPlaybackButton(
    player: AudioPlayer<AudioTrack>,
    buttonsSize: Dp = 32.dp
) {
    val playMode: AudioPlayer.PlayMode = player.playMode.value

    IconButton({
        player.playMode.value =
            if (playMode != AudioPlayer.PlayMode.RANDOM) AudioPlayer.PlayMode.RANDOM
            else AudioPlayer.PlayMode.PLAYLIST
    }, modifier = Modifier.size(buttonsSize)) {
        ImageWithShadow(
            painterResource(Res.drawable.random),
            "Toggle random mode",
            colorFilter = if (playMode == AudioPlayer.PlayMode.RANDOM) ColorFilter.tint(
                KagaminTheme.theme.buttonIcon
            )
            else ColorFilter.tint(KagaminTheme.theme.buttonIconTransparent)
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
                colorFilter = if (volume > 0f) ColorFilter.tint(KagaminTheme.theme.buttonIcon)
                else ColorFilter.tint(KagaminTheme.theme.buttonIconTransparent)
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
