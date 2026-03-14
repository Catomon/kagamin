package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.playlist
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_playlist
import kagamin.composeapp.generated.resources.repeat_single
import kagamin.composeapp.generated.resources.single
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaybackModeToggleButton(
    playMode: PlaylistsManager.PlayMode,
    togglePlayMode: () -> PlaylistsManager.PlayMode,
    modifier: Modifier = Modifier
) {
    echoTrace { "PlaybackModeToggleButton" }

    val imageRes =
        when (playMode) {
            PlaylistsManager.PlayMode.PLAYLIST -> Res.drawable.playlist
            PlaylistsManager.PlayMode.REPEAT_PLAYLIST -> Res.drawable.repeat_playlist
            PlaylistsManager.PlayMode.REPEAT_TRACK -> Res.drawable.repeat_single
            PlaylistsManager.PlayMode.RANDOM -> Res.drawable.random
            PlaylistsManager.PlayMode.ONCE -> Res.drawable.single
        }

    Box(modifier = modifier.size(32.dp)) {
        IconButton({
            PopupText = when (togglePlayMode()) {
                PlaylistsManager.PlayMode.PLAYLIST -> "Playlist"
                PlaylistsManager.PlayMode.REPEAT_PLAYLIST -> "Repeat playlist"
                PlaylistsManager.PlayMode.REPEAT_TRACK -> "Repeat track"
                PlaylistsManager.PlayMode.RANDOM -> "Random"
                PlaylistsManager.PlayMode.ONCE -> "Single"
            }
        }, modifier = Modifier.size(32.dp)) {
            AnimatedContent(imageRes) {
                Icon(
                    painter = painterResource(imageRes),
                    contentDescription = "Toggle play mode",
                    tint = KagaminTheme.colors.buttonIcon
                )
            }
        }
    }
}

var PopupText by mutableStateOf("")

@Composable
fun PopupTextHost(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        PopupText(PopupText)
    }
}

@Composable
fun PopupText(text: String) {
    var offset by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(offset)
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        if (text.isEmpty()) {
            isVisible = false
            return@LaunchedEffect
        }

        if (isVisible) {
            isVisible = false
            delay(200)
        }
        offset = 1f
        isVisible = true
        delay(1500)
        offset = 0f
        isVisible = false

        PopupText = ""
    }

    AnimatedVisibility(
        isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.offset(y = -(16f * animatedOffset).dp)
    ) {
        Text(
            text,
            color = KagaminTheme.textSecondary,
            modifier = Modifier.background(
                color = KagaminTheme.colors.listItem,
                shape = RoundedCornerShape(8.dp)
            ).padding(10.dp),
            fontSize = 18.sp,
            softWrap = false, overflow = TextOverflow.Visible
        )
    }
}

@Composable
fun RepeatTrackPlaybackButton(
    viewModel: KagaminViewModel,
    buttonsSize: Dp = 32.dp,
) {
    echoTrace { "RepeatTrackPlaybackButton" }

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
            else ColorFilter.tint(KagaminTheme.colors.disabled)
        )
    }
}

@Composable
fun RepeatPlaylistPlaybackButton(
    viewModel: KagaminViewModel,
    buttonsSize: Dp = 32.dp,
) {
    echoTrace { "RepeatPlaylistPlaybackButton" }

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
            else ColorFilter.tint(KagaminTheme.colors.disabled)
        )
    }
}

@Composable
fun RandomPlaybackButton(
    viewModel: KagaminViewModel,
    buttonsSize: Dp = 32.dp
) {
    echoTrace { "RandomPlaybackButton" }

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
            else ColorFilter.tint(KagaminTheme.colors.disabled)
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
    echoTrace { "VolumeOptions" }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    var oldVolume by remember { mutableStateOf(volume) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.hoverable(interactionSource).sizeIn(maxWidth = 133.dp).height(40.dp)
    ) {
        IconButton({
            onVolumeChange(if (volume > 0f) 0f else oldVolume)
        }, modifier = Modifier.size(buttonsSize)) {
            ImageWithShadow(
                painterResource(Res.drawable.volume),
                "Volume",
                colorFilter = if (volume > 0f) ColorFilter.tint(KagaminTheme.colors.buttonIcon)
                else ColorFilter.tint(KagaminTheme.colors.disabled)
            )
        }

        AnimatedVisibility(isHovered) {
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
}
