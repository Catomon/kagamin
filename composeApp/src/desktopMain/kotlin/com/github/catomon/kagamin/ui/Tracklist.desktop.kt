package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.ui.util.formatTime
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
fun Tracklist(
    viewModel: KagaminViewModel,
    tracks: List<AudioTrack>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }
    val index =
        remember(tracks) { tracks.mapIndexed { i, track -> (track.uri to i) }.toMap() }
    val currentTrack = viewModel.currentTrack

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[currentTrack?.uri] ?: 0)
    }

    Column(modifier) {
        if (currentTrack != null) {
            TrackItem(
                -1,
                viewModel.currentTrack!!,
                tracklistManager,
                viewModel = viewModel,
                onClick = onClick@{
                    val curTrackIndex = index[currentTrack.uri] ?: return@onClick
                    coroutineScope.launch {
                        listState.animateScrollToItem(curTrackIndex)
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier.background(Colors.backgroundTransparent).height(32.dp)
                    .fillMaxWidth()
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().weight(2f).hoverable(interactionSource)
        ) {
            Column(Modifier.fillMaxSize()) {
                LazyColumn(Modifier.fillMaxWidth(), state = listState) {
                    items(tracks.size, key = {
                        tracks[it].uri
                    }) { index ->
                        val track = tracks[index]
                        TrackItem(
                            index,
                            track,
                            tracklistManager,
                            viewModel = viewModel,
                            onClick = onClick@{
                                if (tracklistManager.isAnySelected) {
                                    if (tracklistManager.isSelected(index, track))
                                        tracklistManager.deselect(index, track)
                                    else tracklistManager.select(index, track)
                                    return@onClick
                                }
                                if (viewModel.isLoadingSong != null) return@onClick

                                viewModel.viewModelScope.launch {
                                    viewModel.isLoadingSong = track
                                    viewModel.audioPlayer.play(track)
                                    viewModel.isLoadingSong = null
                                }
                            },
                            modifier = Modifier
                        )
                    }
                }

                Spacer(Modifier.fillMaxSize().weight(2f).background(Colors.theme.listItemB))
            }

            androidx.compose.animation.AnimatedVisibility(
                isHovered, modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
            ) {
                VerticalScrollbar(
                    modifier = Modifier
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(listState)
                )
            }
        }
    }
}

@Composable
actual fun TrackItem(
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
    val backColor = if (isHeader) Colors.backgroundTransparent else
        if (index % 2 == 0) Colors.theme.listItemA else Colors.theme.listItemB
    ContextMenuArea(items = {
        listOf(
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
    }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(32.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            if (index > -1 && viewModel.currentTrack == track) {
                Box(
                    Modifier.height(32.dp)//.clip(
                        //           RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                        .background(Colors.backgroundTransparent).clickable {
                            viewModel.onPlayPause()
                        }, contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(if (viewModel.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                        "track playback state icon",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Colors.theme.buttonIcon)
                    )
                }
            }

            Box(
                modifier = modifier.fillMaxWidth().height(32.dp)
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
                    Text(
                        track.name,
                        fontSize = 10.sp,
                        color = if (isHeader) Colors.theme.buttonIcon else Colors.text,
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
                                color = Colors.theme.buttonIcon
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
}