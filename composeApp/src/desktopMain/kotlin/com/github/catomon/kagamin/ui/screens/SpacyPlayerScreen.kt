package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.Menu
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.TracksDropTarget
import com.github.catomon.kagamin.ui.components.AppLogo
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.components.ImageWithShadow
import com.github.catomon.kagamin.ui.components.PlaybackModeToggleButton
import com.github.catomon.kagamin.ui.components.PopupText
import com.github.catomon.kagamin.ui.components.PopupTextHost
import com.github.catomon.kagamin.ui.components.TrackThumbnailWithProgressOverlay
import com.github.catomon.kagamin.ui.components.VolumeOptions
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.trackDropTargetBorder
import com.github.catomon.kagamin.ui.util.formatMillisToMinutesSeconds
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.prev
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun SpacyPlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    echoTrace { "SpacyPlayerScreen" }

    val currentTrack by viewModel.currentTrack.collectAsState()
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val position by viewModel.position.collectAsState()

    var isMenuOpen by remember { mutableStateOf(false) }

    val tracksDropTarget = remember {
        TracksDropTarget { tracksUris ->
            viewModel.viewModelScope.launch {
                val loadedTracks = viewModel.loadTracks(tracksUris)
                val loadedUris = loadedTracks.map { it.uri }
                viewModel.updatePlaylist(currentPlaylist.copy(tracks = loadedTracks + currentPlaylist.tracks.filter { it.uri !in loadedUris }))
            }
        }
    }

    Box(
        modifier.background(
            color = KagaminTheme.background
        )
    ) {
        Background(currentTrack, Modifier.matchParentSize())

        Column {
            //top
            Row(
                modifier = Modifier.height(height = 40.dp).fillMaxWidth()
                    .background(KagaminTheme.backgroundTransparent),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.size(24.dp))
                AppLogo(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .height(40.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            isMenuOpen = !isMenuOpen
                        }
                )

                MinimizeButton()
            }

            //main content in the center
            Row(Modifier.fillMaxWidth().weight(1f)) {
                Column(Modifier.weight(0.34f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(36.dp)
                            .fillMaxWidth()
                            .background(color = KagaminTheme.backgroundTransparent)
                            .offset(y = (-16).dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VolumeOptions(
                                volume = volume,
                                onVolumeChange = { newVolume ->
                                    viewModel.setVolume(newVolume)
                                }
                            )

                            PlaybackButtons(viewModel, buttonsSize = 32.dp)

                            PlaybackModeToggleButton(playMode, {
                                viewModel.togglePlayMode()
                            })
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.background(KagaminTheme.backgroundTransparent).fillMaxWidth()
                    ) {
                        CurrentTrackPaneSeekable(
                            currentTrack,
                            position,
                            seek = viewModel::seek,
                            modifier = Modifier.size(190.dp)
                        )

//                        Column {
//                            Row(
//                                modifier = modifier.height(32.dp * 1.5f),
//                                horizontalArrangement = Arrangement.SpaceAround,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                IconButton(
//                                    modifier = Modifier.size(32.dp),
//                                    onClick = {
//                                        viewModel.prevTrack()
//                                    }
//                                ) {
//                                    ImageWithShadow(
//                                        painterResource(Res.drawable.prev),
//                                        "Previous",
//                                        modifier = Modifier.size(32.dp),
//                                        colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
//                                    )
//                                }
//
//                                AnimatedPlayPauseButton(
//                                    viewModel,
//                                    Modifier.padding(horizontal = 12.dp)
//                                )
//
//                                IconButton(
//                                    modifier = Modifier.size(32.dp),
//                                    onClick = {
//                                        viewModel.nextTrack()
//                                    }
//                                ) {
//                                    ImageWithShadow(
//                                        painterResource(Res.drawable.next),
//                                        "Next",
//                                        modifier = Modifier.size(32.dp),
//                                        colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
//                                    )
//                                }
//                            }
//
//                            Row(horizontalArrangement = Arrangement.SpaceAround) {
//                                VolumeOptions(
//                                    volume = volume,
//                                    onVolumeChange = { newVolume ->
//                                        viewModel.setVolume(newVolume)
//                                    }
//                                )
//
//                                PlaybackModeToggleButton(playMode, {
//                                    viewModel.togglePlayMode()
//                                })
//                            }
//                        }
                    }

                    Playlists(
                        viewModel,
                        Modifier.weight(1f)
                    )
                }

                Tracklist(
                    viewModel,
                    Modifier.weight(0.66f)
                        .dragAndDropTarget(
                            { tracksDropTarget.shouldStartDaD(it) },
                            tracksDropTarget
                        )
                        .trackDropTargetBorder(tracksDropTarget.isTarget)
                )
            }

            //bottom
//            Row(
//                modifier = Modifier.height(height = 40.dp).fillMaxWidth().background(KagaminTheme.backgroundTransparent)
//            ) {
//                AnimatedPlayPauseButton(
//                    viewModel,
//                    Modifier.padding(all = 18.dp)
//                )
//            }
        }

        AnimatedVisibility(
            isMenuOpen,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Box(Modifier.fillMaxSize().clickable(null, null) {
                isMenuOpen = false
            }) {
                Menu(
                    currentTrack,
                    viewModel, navigateToSettings = {
                        if (navController.currentDestination?.route != SettingsDestination.toString())
                            navController.navigate(SettingsDestination.toString())
                    },
                    onClose = {
                        isMenuOpen = false
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                )
            }
        }

        AnimatedVisibility(
            viewModel.isLoading,
            modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp)
        ) {
            CircularProgressIndicator(Modifier.size(32.dp))
        }

        PopupTextHost(Modifier.matchParentSize())
    }
}

@Composable
private fun MinimizeButton() {
    val window = LocalWindow.current
    IconButton({
        window.isMinimized = true
    }, modifier = Modifier.size(24.dp)) {
        Box(
            Modifier.size(9.dp)
                .background(color = KagaminTheme.colors.buttonIcon, shape = CircleShape)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CurrentTrackPaneSeekable(
    track: AudioTrack?,
    position: Long,
    seek: (Long) -> Unit,
    modifier: Modifier = Modifier.size(200.dp)
) {
    echoTrace { "CurrentTrackPaneSeekable" }

    val interactionSource = remember { MutableInteractionSource() }
    val isThumbnailHovered by interactionSource.collectIsHoveredAsState()

    val progress by remember(track, position) {
        derivedStateOf {
            when (track) {
                null -> 0f
                else -> if (track.duration > 0 && track.duration < Long.MAX_VALUE) position.toFloat() / track.duration else -1f
            }
        }
    }
    var progressOnHover by remember { mutableStateOf(0f) }
    val progressTargetValue by remember(progress) {
        derivedStateOf {
            if (isThumbnailHovered) progressOnHover else progress
        }
    }
    val progressAnimated by animateFloatAsState(progressTargetValue)

    Column(modifier) {
        TrackThumbnailWithProgressOverlay(
            track,
            onSetProgress = {
                if (track != null) {
                    seek((track.duration * it).toLong())
                }
            },
            progress = progressAnimated,
            progressColor = KagaminTheme.colors.thumbnailProgressIndicator,
            modifier = Modifier.fillMaxSize().hoverable(interactionSource).onPointerEvent(
                PointerEventType.Move
            ) {
                progressOnHover = it.changes.first().position.x / size.width
            },
            shape = RoundedCornerShape(8.dp),
            size = ThumbnailCacheManager.SIZE.H150,
            shadow = false,
            controlProgress = true
        )

        if (track != null)
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    .padding(start = 4.dp)
            ) {
                Text(
                    track.title,
                    fontSize = 12.sp,
                    color = KagaminTheme.text,
                    maxLines = 1,
                    modifier = Modifier.let { if (isThumbnailHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it }
                )

                if (track.artist.isNotBlank())
                    Text(
                        track.artist,
                        fontSize = 10.sp,
                        color = KagaminTheme.textSecondary,
                        maxLines = 1,
                        modifier = Modifier.let { if (isThumbnailHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it },
                        lineHeight = 18.sp
                    )

//                Spacer(Modifier.weight(1f))

                if (track.duration >= 0)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        echoTrace { "TrackDurationText" }

                        val trackDurationText by remember(track) {
                            derivedStateOf {
                                "${
                                    if (isThumbnailHovered) formatMillisToMinutesSeconds((track.duration * progressAnimated).toLong())
                                    else formatMillisToMinutesSeconds(track.let { position })
                                }/${formatMillisToMinutesSeconds(track.duration)}"
                            }
                        }

                        Text(
                            trackDurationText,
                            fontSize = 10.sp,
                            color = KagaminTheme.colors.buttonIcon
                        )
                    }
            }
    }
}


@Composable
fun PlaybackButtons(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 24.dp
) {
    echoTrace { "PlaybackButtons" }

    val playState by viewModel.playState.collectAsState()

    Row(
        modifier = modifier.height(buttonsSize * 1.5f),//.background(Colors.noteBackground.copy(alpha = 0.75f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(buttonsSize),
            onClick = {
                viewModel.prevTrack()
            }
        ) {
            ImageWithShadow(
                painterResource(Res.drawable.prev),
                "Previous",
                modifier = Modifier.size(buttonsSize),
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
            )
        }

        AnimatedPlayPauseButton(viewModel, buttonSize = 32.dp)

        IconButton(
            modifier = Modifier.size(buttonsSize),
            onClick = {
                viewModel.nextTrack()
            }
        ) {
            ImageWithShadow(
                painterResource(Res.drawable.next),
                "Next",
                modifier = Modifier.size(buttonsSize),
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
            )
        }
    }
}
