package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.PlaylistsLoader
import com.github.catomon.kagamin.ui.components.LikeSongButton
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.formatMillisToMinutesSeconds
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.ConfirmWindowState
import com.github.catomon.kagamin.ui.windows.LocalConfirmWindow
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
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
    echoTrace { "ThumbnailTrackItem" }

    val clipboard = LocalClipboardManager.current
    val confirmationWindow = LocalConfirmWindow.current
    val snackbar = LocalSnackbarHostState.current
    val backgroundColor = KagaminTheme.colors.listItem

    val height = 64.dp

//    val position by viewModel.position.collectAsState()
//
//    val progress by remember {
//        derivedStateOf {
//            when (currentTrack) {
//                null -> 0f
//                else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) position.toFloat() / currentTrack.duration else -1f
//            }
//        }
//    }

    ContextMenuArea(items = {
        ThumbnailTrackItemDefaults.contextMenuItems(
            isCurrentTrack,
            tracklistManager,
            index,
            track,
            clipboard,
            viewModel,
            confirmationWindow,
            snackbar,
            isCurrentTrack = isCurrentTrack
        )
    }) {
        Row(modifier.height(height)) {
            AnimatedVisibility(index > -1 && isCurrentTrack) {
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
                        track,
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

@Composable
private fun TrackItemBody(
    viewModel: KagaminViewModel,
    track: AudioTrack,
    isSelected: Boolean,
    modifier: Modifier,
) {
    echoTrace { "TrackItemBody" }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var updatingLike by remember { mutableStateOf(false) }
    var isLoved by remember(
        updatingLike,
        track
    ) {
        mutableStateOf(track?.let {
            viewModel.playlists.value
                .firstOrNull { playlist -> playlist.id == "loved" }?.tracks?.any { it.id == track.id }
        } ?: false)
    }

    Box(
        modifier = modifier.fillMaxWidth().hoverable(interactionSource),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                .align(Alignment.TopStart).padding(start = 4.dp)
        ) {
            Text(
                track.title,
                fontSize = 10.sp,
                color = KagaminTheme.text,
                maxLines = 1,
                modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it }
            )

            if (track.artist.isNotBlank())
                Text(
                    track.artist,
                    fontSize = 8.sp,
                    color = KagaminTheme.textSecondary,
                    maxLines = 1,
                    modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                    lineHeight = 16.sp
                )

            if (track.duration >= 0)
                Text(
                    remember { formatMillisToMinutesSeconds(track.duration) },
                    fontSize = 8.sp,
                    color = KagaminTheme.textSecondary,
                    maxLines = 1,
                    modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                    lineHeight = 16.sp
                )
        }

        AnimatedVisibility(
            isHovered || isLoved,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {

            LikeSongButton(isLoved, {
                viewModel.viewModelScope.launch {
                    if (updatingLike) return@launch
                    updatingLike = true
                    if (!isLoved) {
                        track?.let addToLoved@{ track ->
                            //get loved playlist or create new and then add the track to it and finally save playlist
                            viewModel.playlists.value
                                .firstOrNull { playlist -> playlist.id == "loved" }
                                ?.let { playlist ->
                                    if (playlist.tracks.any { it.id == track.id }) return@addToLoved
                                    viewModel.updatePlaylist(playlist.copy(tracks = playlist.tracks + track)); playlist
                                } ?: Playlist("loved", "loved", listOf(track))
                                .also { playlist ->
                                    viewModel.createPlaylist(
                                        playlist
                                    )
                                }.also {
                                    PlaylistsLoader.savePlaylist(it)
                                }

                        }
                    } else {
                        //remove the track from the loved playlist and then save playlist
                        track?.let { track ->
                            viewModel.playlists.value
                                .firstOrNull { playlist -> playlist.id == "loved" }
                                ?.let { playlist ->
                                    viewModel.updatePlaylist(playlist.copy(tracks = playlist.tracks - track))
                                    PlaylistsLoader.savePlaylist(playlist)
                                }
                        }
                    }
                    isLoved = viewModel.playlists.value
                        .firstOrNull { playlist -> playlist.id == "loved" }?.tracks?.any { it.id == track.id }
                        ?: false
                    updatingLike = false
                }
            }, 32.dp)
        }

        Row(
            Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected)
                Icon(painterResource(Res.drawable.selected), null, modifier = Modifier.size(20.dp), tint = Color.White)
        }
    }
}

@Composable
fun PlaybackStateButton(
    height: Dp,
    backColor: Color,
    viewModel: KagaminViewModel
) {
    val playSate by viewModel.playState.collectAsState()

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
                painterResource(if (playSate == AudioPlayerService.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                if (playSate == AudioPlayerService.PlayState.PAUSED) "Play" else "Pause",
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
        snackbar: SnackbarHostState,
        isCurrentTrack: Boolean
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
                        if (isCurrentTrack)
                            viewModel.stop()

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
                            viewModel.stop()

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