package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.PlaylistsLoader
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.TracklistManager
import com.github.catomon.kagamin.ui.util.formatMillisToMinutesSeconds
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.online
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

object ThumbnailTrackItemDefaults {
    val colors = Colors()

    data class Colors(
        val background: Color = KagaminTheme.colors.listItem
    )
}

@Composable
fun ThumbnailTrackItem(
    index: Int,
    track: AudioTrack,
    tracklistManager: TracklistManager,
    viewModel: KagaminViewModel,
    isCurrentTrack: Boolean,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    colors: ThumbnailTrackItemDefaults.Colors = ThumbnailTrackItemDefaults.colors
) {
    echoTrace { "ThumbnailTrackItem" }

    val height = 80.dp

    Row(modifier.height(height)) {
        AnimatedVisibility(index > -1 && isCurrentTrack) {
            PlaybackStateButton(height, viewModel)
        }

        Column(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.background)
                .clickable {
                    onClick()
                }) {
            Row(Modifier.weight(1f)) {
                TrackThumbnail(
                    track,
                    modifier = Modifier.width(height),
                    shape = RoundedCornerShape(8.dp),
                    size = 150
                )

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
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .align(Alignment.TopStart)
                .padding(start = 4.dp)
        ) {
            Text(
                track.title,
                fontSize = 14.sp,
                color = KagaminTheme.text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it }
            )

            //if (size < 64)
//            Row(verticalAlignment = Alignment.CenterVertically) {
            if (track.artist.isNotBlank())
                Text(
                    track.artist,
                    fontSize = 12.sp,
                    color = KagaminTheme.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                    lineHeight = 16.sp
                )

//                Spacer(Modifier.width(4.dp))

//            if (track.duration >= 0)
//                Text(
//                    remember { formatMillisToMinutesSeconds(track.duration) },
//                    fontSize = 12.sp,
//                    color = KagaminTheme.textSecondary,
//                    maxLines = 1,
//                    modifier = Modifier.let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
//                    lineHeight = 16.sp
//                )
//            }
        }

        Row(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(
                isHovered || isLoved
            ) {
                LikeSongButton(
                    isLoved = isLoved,
                    onClick = {
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
                    },
                    buttonsSize = 32.dp
                )
            }

            if (remember { track.uri.startsWith("https") }) {
                Icon(
                    painterResource(Res.drawable.online),
                    null,
                    tint = KagaminTheme.colors.backgroundTransparent
                )
            }

            if (isSelected)
                Icon(
                    painterResource(Res.drawable.selected),
                    null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 4.dp),
                    tint = KagaminTheme.backgroundTransparent
                )
        }
    }
}

@Composable
fun PlaybackStateButton(
    height: Dp,
    viewModel: KagaminViewModel
) {
    val playSate by viewModel.playState.collectAsState()

    Box(
        Modifier
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .clickable { viewModel.onPlayPause() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(height)
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
