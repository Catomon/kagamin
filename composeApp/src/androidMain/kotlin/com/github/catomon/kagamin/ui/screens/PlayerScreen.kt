package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.Sidebar
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.fetchAudioTracks
import com.github.catomon.kagamin.result
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.components.TrackThumbnailWithProgressOverlay
import com.github.catomon.kagamin.ui.theme.KagaminColors
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin_text_small
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.pause_star
import kagamin.composeapp.generated.resources.play_star
import kagamin.composeapp.generated.resources.prev
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_single
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlayerScreen(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    var tracks by remember { mutableStateOf<Map<String, List<AudioTrack>>>(emptyMap()) }
    var isScanning by remember { mutableStateOf(true) }
    val context = LocalContext.current
    var currentFolder by remember { mutableStateOf("") }

    val currentTrack by viewModel.currentTrack.collectAsState()

    val currentPlaylist by viewModel.currentPlaylist.collectAsState()

    LaunchedEffect(Unit) {
        //TODO
        viewModel.settings = viewModel.settings.copy(useTrackImageAsBackground = false, theme = KagaminColors.KagaminDark.name)
        viewModel.saveSettings()
    }

    LaunchedEffect(isScanning) {
        if (!isScanning) {
            currentFolder = "Music"
            viewModel.changeCurrentPlaylist(
                Playlist(
                    currentFolder,
                    currentFolder,
                    tracks[currentFolder] ?: emptyList(),
                )
            )
        }
    }

    LaunchedEffect(result) {
        tracks = fetchAudioTracks(context)

        isScanning = false
    }

    val tabTransition: (Tabs) -> ContentTransform = { tab ->
        when (tab) {
            Tabs.ADD_TRACKS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            Tabs.CREATE_PLAYLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.TRACKLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.PLAYLISTS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }

            else -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .background(KagaminTheme.colors.background), contentAlignment = Alignment.Center
    ) {
        Background(currentTrack, Modifier.matchParentSize())

        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                KagaminLogo(
                    Modifier
                        .height(64.dp)
                        .background(KagaminTheme.colors.backgroundTransparent), 100.dp
                )

                AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
                    tabTransition(viewModel.currentTab)
                }) {
                    when (it) {
                        Tabs.PLAYBACK -> {
                            CurrentTrackFrame(
                                viewModel,
                                currentTrack,
                                Modifier
                                    .fillMaxSize()
                                    .background(KagaminTheme.colors.backgroundTransparent)
                            )
                            //DODO
//                            CurrentTrackFrame(
//                                viewModel,
//                                currentTrack,
//                                Modifier.width(160.dp).fillMaxHeight()
//                                    .background(color = KagaminTheme.backgroundTransparent)
//                            )
                        }

                        Tabs.PLAYLISTS -> {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(KagaminTheme.colors.backgroundTransparent)
                            )
                            //TODO
//                            Playlists(
//                                viewModel,
//                                Modifier.align(Alignment.Center)
//                                    .fillMaxHeight()//.padding(start = 4.dp, end = 4.dp)
//                            )
                        }

                        Tabs.TRACKLIST -> {
                            Tracklist(
                                viewModel,
                                currentTrack,
                                tracks = currentPlaylist.tracks,
                                onPlay = { viewModel.play(it) },
                                Modifier
//                            .fillMaxWidth()
                                //   .weight(1f)
                            )
                        }

                        Tabs.OPTIONS -> TODO()

                        Tabs.ADD_TRACKS -> {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(KagaminTheme.colors.backgroundTransparent)
                            )
//                            AddTracksTab(
//                                viewModel, Modifier.fillMaxHeight().align(Alignment.Center)
//                            )
                        }

                        Tabs.CREATE_PLAYLIST -> {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(KagaminTheme.colors.backgroundTransparent)
                            )
//                            CreatePlaylistTab(
//                                viewModel, Modifier.fillMaxHeight().align(Alignment.Center)
//                            )
                        }
                    }
                }


//                if (currentFolder.isEmpty())
//                    AnimatedContent(isScanning, modifier = Modifier.weight(1f)) {
//                        if (it) {
//                            Text("Scanning...")
//                        } else {
//                            if (tracks.isEmpty()) {
//                                Text("Empty.")
//                            } else {
//                                LazyColumn(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .weight(1f)
//                                ) {
//                                    items(tracks.keys.size) {
//                                        Text(tracks.keys.elementAt(it), modifier = Modifier.clickable {
//                                            currentFolder = tracks.keys.elementAt(it)
//                                            viewModel.changeCurrentPlaylist(
//                                                Playlist(
//                                                    currentFolder,
//                                                    currentFolder,
//                                                    tracks[currentFolder] ?: emptyList(),
//                                                )
//                                            )
//                                        })
//                                    }
//                                }
//                            }
//                        }
//                    }
//                else
//                    Tracklist(
//                        viewModel,
//                        currentTrack,
//                        tracks = currentPlaylist.tracks,
//                        onPlay = { viewModel.play(it) },
//                        Modifier
////                            .fillMaxWidth()
//                         //   .weight(1f)
//                    )

//                BottomBar(
//                    trackName = currentTrack?.title ?: "",
//                    isPlaying = playState == AudioPlayerService.PlayState.PLAYING,
//                    isRepeat = playMode == PlaylistsManager.PlayMode.REPEAT_TRACK,
//                    isShuffle = playMode == PlaylistsManager.PlayMode.RANDOM,
//                    onPlayPauseClick = viewModel::onPlayPause,
//                    onPrevClick = viewModel::prevTrack,
//                    onNextClick = viewModel::nextTrack,
//                    onRepeatClick = { viewModel.setPlayMode(PlaylistsManager.PlayMode.REPEAT_TRACK) },
//                    onShuffleClick = { viewModel.setPlayMode(PlaylistsManager.PlayMode.RANDOM) },
//                    modifier = Modifier.fillMaxWidth()
//                )
            }

            Sidebar(viewModel)
        }


//        IconButton(
//            onClick = viewModel::onPlayPause,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .size(64.dp)
//                .background(
//                    KagaminTheme.colors.backgroundTransparent,
//                    CircleShape
//                ),
//        ) {
//            AnimatedContent(playState == AudioPlayerService.PlayState.PLAYING) { isPlaying ->
//                if (isPlaying) {
//                    Icon(
//                        painterResource(Res.drawable.pause_star),
//                        "Pause",
//                        modifier = Modifier.size(64.dp),
//                        tint = KagaminTheme.colors.buttonIcon
//                    )
//
//                } else {
//                    Icon(
//                        painterResource(Res.drawable.play_star),
//                        "Play",
//                        modifier = Modifier.size(64.dp),
//                        tint = KagaminTheme.colors.buttonIcon
//                    )
//                }
//            }
//        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CurrentTrackFrame(
    viewModel: KagaminViewModel,
    currentTrack: AudioTrack?, modifier: Modifier = Modifier
) {
    val playState by viewModel.playState.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val trackName = currentTrack?.title ?: ""
    val isPlaying = playState == AudioPlayerService.PlayState.PLAYING
    val isRepeat = playMode == PlaylistsManager.PlayMode.REPEAT_TRACK
    val isShuffle = playMode == PlaylistsManager.PlayMode.RANDOM
    val onPlayPauseClick = viewModel::onPlayPause
    val onPrevClick = viewModel::prevTrack
    val onNextClick = viewModel::nextTrack
    val onRepeatClick = { viewModel.setPlayMode(PlaylistsManager.PlayMode.REPEAT_TRACK) }
    val onShuffleClick = { viewModel.setPlayMode(PlaylistsManager.PlayMode.RANDOM) }

    val interactionSource = remember { MutableInteractionSource() }
    val isThumbnailHovered by interactionSource.collectIsHoveredAsState()

    val position by viewModel.position.collectAsState()
    val progress by remember(currentTrack) {
        derivedStateOf {
            when (currentTrack) {
                null -> 0f
                else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) position.toFloat() / currentTrack.duration else 0f
            }
        }
    }
    var progressOnHover by remember { mutableFloatStateOf(0f) }
    val progressTargetValue by remember(progress) {
        derivedStateOf {
            if (isThumbnailHovered) progressOnHover else progress
        }
    }
    val progressAnimated by animateFloatAsState(progressTargetValue)

    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TrackThumbnailWithProgressOverlay(
                currentTrack,
                onSetProgress = {
                    if (currentTrack != null) {
                        viewModel.seek((currentTrack.duration * it).toLong())
                    }
                },
                progress = progressAnimated,
                progressColor = KagaminTheme.colors.thumbnailProgressIndicator,
                modifier = Modifier
                    .padding(8.dp)
                    .size(250.dp)
                    .hoverable(interactionSource),
                size = 512,
                controlProgress = true
            )

            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ButtonIcon(
                        Res.drawable.prev,
                        onPrevClick,
                        size = 64.dp,
                        tint = KagaminTheme.colors.buttonIcon
                    )

                    TextButton(
                        onClick = onPlayPauseClick,
                        modifier = Modifier.size(100.dp)
                    ) {
                        AnimatedContent(isPlaying, modifier = Modifier.size(86.dp)) { isPlaying ->
                            if (isPlaying) {
                                Icon(
                                    painterResource(Res.drawable.pause_star),
                                    "Pause",
                                    modifier = Modifier.size(86.dp),
                                    tint = KagaminTheme.colors.buttonIcon
                                )

                            } else {
                                Icon(
                                    painterResource(Res.drawable.play_star),
                                    "Play",
                                    modifier = Modifier.size(86.dp),
                                    tint = KagaminTheme.colors.buttonIcon
                                )
                            }
                        }
                    }

                    ButtonIcon(
                        Res.drawable.next,
                        onNextClick,
                        size = 64.dp,
                        tint = KagaminTheme.colors.buttonIcon
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    ButtonIcon(
                        Res.drawable.repeat_single,
                        onRepeatClick,
                        size = 64.dp,
                        tint = if (isRepeat)
                            KagaminTheme.colors.buttonIcon
                        else KagaminTheme.colors.disabled
                    )

                    ButtonIcon(
                        Res.drawable.random,
                        onShuffleClick,
                        size = 64.dp,
                        tint = if (isShuffle)
                            KagaminTheme.colors.buttonIcon
                        else KagaminTheme.colors.disabled
                    )
                }
            }
        }
    }
}

@Composable
fun ButtonIcon(
    res: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = KagaminTheme.colors.buttonIconSmall,
    size: Dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Icon(
            painterResource(res),
            null,
            modifier = Modifier.size(size),
            tint = tint
        )
    }
}

@Composable
fun KagaminLogo(modifier: Modifier = Modifier, height: Dp = 32.dp) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Image(
            painterResource(Res.drawable.kagamin_text_small),
            null,
            colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon),
            modifier = Modifier
                .height(height)
                .fillMaxSize()
        )
    }
}
