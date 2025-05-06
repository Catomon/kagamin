package com.github.catomon.kagamin.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
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
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.components.getThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.formatTime
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.ConfirmWindowState
import com.github.catomon.kagamin.ui.windows.LocalConfirmWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun ThumbnailTrackItem(
    index: Int,
    track: AudioTrack,
    tracklistManager: TracklistManager,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val clipboard = LocalClipboardManager.current
    val confirmationWindow = LocalConfirmWindow.current
    val snackbar = LocalSnackbarHostState.current
    val isHeader = index == -1
    val backColor = if (isHeader) KagaminTheme.backgroundTransparent else
        if (index % 2 == 0) KagaminTheme.theme.listItemA else KagaminTheme.theme.listItemB

    var trackThumbnailUpdated by remember { mutableStateOf<ImageBitmap?>(null) }

    val height = 64.dp

    LaunchedEffect(track) {
        trackThumbnailUpdated = try {
            withContext(Dispatchers.IO) {
                getThumbnail(track.uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    ContextMenuArea(items = {
        ThumbnailTrackItemDefaults.contextMenuItems(
            isHeader,
            tracklistManager,
            index,
            track,
            clipboard,
            viewModel,
            confirmationWindow,
            snackbar
        )
    }) {
        Box(modifier.height(height)) {
            if (!isHeader) {
                TrackThumbnail(trackThumbnailUpdated, modifier = Modifier.fillMaxWidth().height(height), shape = RectangleShape)
            }

            ThumbnailTrackItemContent(
                height,
                index,
                viewModel,
                track,
                backColor,
                modifier = Modifier,
                onClick,
                isHeader,
                tracklistManager
            )
        }
    }
}


@Composable
private fun ThumbnailTrackItemContent(
    height: Dp,
    index: Int,
    viewModel: KagaminViewModel,
    track: AudioTrack,
    backColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
    isHeader: Boolean,
    tracklistManager: TracklistManager
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(height).fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        if (index > -1 && viewModel.currentTrack == track) {
            PlaybackStateButton(height, backColor, viewModel)
        }

        Box(
            modifier = modifier.fillMaxWidth().height(height)
                .background(color = backColor)
                .clickable {
                    onClick()
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                    if (!isHeader) {
//                        TrackThumbnail(trackThumbnailUpdated, modifier = Modifier.size(32.dp), shape = RectangleShape)
//                    }

                Text(
                    track.name,
                    fontSize = 10.sp,
                    color = if (isHeader) KagaminTheme.theme.buttonIcon else KagaminTheme.text,
                    maxLines = 1,
                    // overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.99f).let {
                        if (isHeader) it.basicMarquee(iterations = Int.MAX_VALUE)
                        else it
                    }
                )

                // duration text
                if (isHeader)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var timePastText by remember { mutableStateOf("-:-") }
                        var trackDurationText by remember { mutableStateOf("-:-") }

                        LaunchedEffect(track) {
                            trackDurationText = formatTime(track.duration)

                            while (true) {
                                if (viewModel.audioPlayer.playState.value == AudioPlayer.PlayState.PLAYING) {
                                    timePastText =
                                        formatTime(track.let { viewModel.audioPlayer.position })

                                    if (viewModel.audioPlayer.position < 1000)
                                        trackDurationText = formatTime(track.duration)
                                }
                                delay(250)
                            }
                        }

                        Text(
                            "$timePastText/$trackDurationText",
                            fontSize = 10.sp,
                            color = KagaminTheme.theme.buttonIcon
                        )
                    }
            }

            //selected icon
            Row(
                Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tracklistManager.selected.contains(index))
                    Icon(painterResource(Res.drawable.selected), null)
            }
        }
    }
}

@Composable
private fun PlaybackStateButton(
    height: Dp,
    backColor: Color,
    viewModel: KagaminViewModel
) {
    Box(
        Modifier
            .height(height)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                drawRect(color = backColor, size = size, blendMode = BlendMode.SrcOut)
                drawContent()
            }
            .clickable { viewModel.onPlayPause() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(height).background(
                KagaminTheme.backgroundTransparent,
                RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
            )
        ) {
            Image(
                painterResource(if (viewModel.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                if (viewModel.playState == AudioPlayer.PlayState.PAUSED) "play" else "pause",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(KagaminTheme.theme.buttonIcon)
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