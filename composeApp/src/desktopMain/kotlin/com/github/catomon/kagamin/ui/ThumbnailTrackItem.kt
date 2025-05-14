package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.ui.components.LikeSongButton
import com.github.catomon.kagamin.ui.components.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.components.TrackThumbnailProgressOverlay
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.ConfirmWindowState
import com.github.catomon.kagamin.ui.windows.LocalConfirmWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun ThumbnailTrackItem(
    index: Int,
    track: AudioTrack,
    tracklistManager: TracklistManager,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    isCurrentTrack: Boolean,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    val confirmationWindow = LocalConfirmWindow.current
    val snackbar = LocalSnackbarHostState.current
    val backgroundColor = KagaminTheme.colors.listItem

    val height = 64.dp

    var progress by remember(isCurrentTrack) { mutableFloatStateOf(0f) }
    val updateProgress = {
        progress = when {
            !isCurrentTrack -> 0f
            else -> if (track.duration > 0 && track.duration < Long.MAX_VALUE) viewModel.audioPlayer.position.toFloat() / track.duration else 0f
        }
    }

    LaunchedEffect(isCurrentTrack) {
        if (!isCurrentTrack) return@LaunchedEffect

        while (true) {
            if (viewModel.audioPlayer.playState.value == AudioPlayer.PlayState.PLAYING) updateProgress()
            delay(250)
        }
    }

    ContextMenuArea(items = {
        ThumbnailTrackItemDefaults.contextMenuItems(
            isCurrentTrack,
            tracklistManager,
            index,
            track,
            clipboard,
            viewModel,
            confirmationWindow,
            snackbar
        )
    }) {
        Row(modifier.height(height)) {
            AnimatedVisibility(index > -1 && viewModel.currentTrack == track) {
                PlaybackStateButton(height, backgroundColor, viewModel)
            }

            Column(
                Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(backgroundColor)
                    .clickable {
                        onClick()
                    }) {
                Row(Modifier.weight(1f)) {
//                    if (isCurrentTrack) {
//                        TrackThumbnailProgressOverlay(
//                            track.uri,
//                            modifier = Modifier.width(64.dp),
//                            shape = RoundedCornerShape(8.dp),
//                            progress = progress,
//                            controlProgress = false,
//                            height = ThumbnailCacheManager.SIZE.H64
//                        )
//                    } else {
                        TrackThumbnail(
                            track.uri,
                            modifier = Modifier.width(64.dp),
                            shape = RoundedCornerShape(8.dp),
                            height = ThumbnailCacheManager.SIZE.H64
                        )
//                    }

                    TrackItemBody(
                        viewModel = viewModel,
                        track = track,
                        isSelected = tracklistManager.selected.contains(index),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TrackItemBody(
    viewModel: KagaminViewModel,
    track: AudioTrack,
    isSelected: Boolean,
    modifier: Modifier,
) {

    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxWidth().onPointerEvent(PointerEventType.Exit) {
            isHovered = false
        }.onPointerEvent(PointerEventType.Enter) {
            isHovered = true
        },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                .align(Alignment.TopStart).padding(start = 4.dp)
        ) {
            Text(
                track.name,
                fontSize = 10.sp,
                color = KagaminTheme.text,
                maxLines = 1,
                // overflow = TextOverflow.Ellipsis,
                modifier = Modifier.focusable().basicMarquee(
                    iterations = Int.MAX_VALUE,
                    animationMode = MarqueeAnimationMode.WhileFocused
                )
            )

            Text(
                track.author,
                fontSize = 8.sp,
                color = KagaminTheme.textSecondary,
                maxLines = 1,
                // overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE)
            )
        }

        AnimatedVisibility(
            isHovered || viewModel.lovedSongs[track.uri] != null,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            LikeSongButton(viewModel, track, 32.dp)
        }

        Row(
            Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected)
                Icon(painterResource(Res.drawable.selected), null)
        }
    }
}

@Composable
fun PlaybackStateButton(
    height: Dp,
    backColor: Color,
    viewModel: KagaminViewModel
) {
    Box(
        Modifier
            .height(height)
//            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
//            .drawWithContent {
//                drawContent()
//                drawRect(color = backColor, size = size, blendMode = BlendMode.SrcOut)
//                drawContent()
//            }
            .clip(RoundedCornerShape(6.dp))
            .clickable { viewModel.onPlayPause() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(height)
//                .background(
//                KagaminTheme.backgroundTransparent,
//                RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
//            )
        ) {
            Image(
                painterResource(if (viewModel.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                if (viewModel.playState == AudioPlayer.PlayState.PAUSED) "play" else "pause",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
            )
        }
    }
}

object ThumbnailTrackItemDefaults {
    fun contextMenuItems(
        isHeader: Boolean,
        tracklistManager: TracklistManager,
        index: Int,
        track: AudioTrack,
        clipboard: ClipboardManager,
        viewModel: KagaminViewModel,
        confirmationWindow: MutableState<ConfirmWindowState>,
        snackbar: SnackbarHostState
    ) = listOf(
        ContextMenuItem("Select") {
            if (!isHeader)
                tracklistManager.select(index, track)
        },
        if (tracklistManager.isAnySelected) {
            ContextMenuItem("Deselect All") {
                tracklistManager.deselectAll()
            }
        } else {
            ContextMenuItem("Copy URI") {
                clipboard.setText(AnnotatedString(track.uri))
            }
        },
        ContextMenuItem(if (tracklistManager.isAnySelected) "Remove selected" else "Remove") {
            tracklistManager.contextMenuRemovePressed(viewModel, track)
        },
        ContextMenuItem(if (tracklistManager.selected.size <= 1) "Delete file" else "Delete files") {
            if (tracklistManager.selected.size < 1) {
                confirmationWindow.value = ConfirmWindowState(
                    true,
                    onConfirm = {
                        if (viewModel.currentTrack == track)
                            viewModel.audioPlayer.stop()

                        viewModel.viewModelScope.launch {
                            tracklistManager.deleteFile(track)
                            snackbar.showSnackbar("Deleting the file..")
                        }
                        tracklistManager.contextMenuRemovePressed(viewModel, track)
                    },
                    onCancel = {

                    },
                    onClose = {
                        confirmationWindow.value = ConfirmWindowState()
                    }
                )
            } else {
                confirmationWindow.value = ConfirmWindowState(
                    true,
                    onConfirm = {
                        if (tracklistManager.selected.any { it.value == track })
                            viewModel.audioPlayer.stop()

                        tracklistManager.deleteSelectedFiles()
                        tracklistManager.contextMenuRemovePressed(viewModel, track)

                        viewModel.viewModelScope.launch {
                            snackbar.showSnackbar("Deleting files..")
                        }
                    },
                    onCancel = {

                    },
                    onClose = {
                        confirmationWindow.value = ConfirmWindowState()
                    }
                )
            }
        },
    )
}