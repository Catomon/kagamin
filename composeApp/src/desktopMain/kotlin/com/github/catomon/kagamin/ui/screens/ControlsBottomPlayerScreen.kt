package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.saveSettings
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.AddButton
import com.github.catomon.kagamin.ui.components.AppName
import com.github.catomon.kagamin.ui.components.PlaybackButtons
import com.github.catomon.kagamin.ui.components.RandomPlaybackButton
import com.github.catomon.kagamin.ui.components.RepeatPlaylistPlaybackButton
import com.github.catomon.kagamin.ui.components.RepeatTrackPlaybackButton
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.components.VolumeOptions
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun ControlsBottomPlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val audioPlayer = viewModel.audioPlayer
    val playlist = viewModel.playlist
    val currentTrack = viewModel.currentTrack
    val playState = viewModel.playState
    val playMode = viewModel.playMode
    val currentPlaylistName = viewModel.currentPlaylistName

    val tabTransition: (Tabs) -> ContentTransform = { tab ->
        when (tab) {
            Tabs.ADD_TRACKS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            Tabs.CREATE_PLAYLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.TRACKLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.PLAYLISTS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }

            else -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }
    }

    LaunchedEffect(currentPlaylistName) {
        viewModel.reloadPlaylist()

        saveSettings(viewModel.settings.copy(lastPlaylistName = currentPlaylistName))
    }

    LaunchedEffect(currentTrack) {
        viewModel.updateThumbnail()
    }

    Box(
        modifier.background(
            color = KagaminTheme.behindBackground,
            shape = RoundedCornerShape(16.dp)
        )
    ) {
        TrackThumbnail(
            viewModel.trackThumbnail,
            onSetProgress = {
                if (currentTrack != null)
                    audioPlayer.seek((currentTrack.duration * it).toLong())
            },
            0f,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            blur = true,
            controlProgress = false
        )

//        BackgroundImage()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.fillMaxHeight()
//                    .background(color = KagaminTheme.backgroundTransparent)
//            ) {
//                AppName(
//                    Modifier.padding(horizontal = 12.dp).height(25.dp)
//                        .graphicsLayer(translationY = 2f)
//                        .clip(RoundedCornerShape(8.dp))
//                        .clickable {
//                            if (navController.currentDestination?.route != SettingsDestination.toString())
//                                navController.navigate(SettingsDestination.toString())
//                        })
//
//                CurrentTrackFrame(
//                    viewModel, viewModel.trackThumbnail,
//                    currentTrack, audioPlayer, Modifier.width(160.dp).fillMaxHeight()
//                )
//            }

                Playlists(
                    viewModel,
                    Modifier.weight(0.35f) //.padding(start = 4.dp, end = 4.dp)
                )

                if (viewModel.playlist.isEmpty()) {
                    Box(
                        Modifier.weight(0.65f).fillMaxHeight()
                            .background(KagaminTheme.backgroundTransparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Drop files or folders here",
                            textAlign = TextAlign.Center,
                            color = KagaminTheme.textSecondary
                        )
                    }
                } else {
                    Tracklist(
                        viewModel,
                        viewModel.playlist,
                        Modifier.weight(0.65f)//.padding(start = 16.dp, end = 16.dp)
                    )
                }

//                AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
//                    tabTransition(viewModel.currentTab)
//                }) {
//                    when (it) {
//                        Tabs.PLAYLISTS -> {
//
//                        }
//
//                        Tabs.TRACKLIST -> {
//
//                        }
//
//                        Tabs.OPTIONS -> TODO()
//
//                        Tabs.ADD_TRACKS -> {
//                            AddTracksTab(viewModel, Modifier.fillMaxSize().align(Alignment.Center))
//                        }
//
//                        Tabs.CREATE_PLAYLIST -> {
//                            CreatePlaylistTab(
//                                viewModel,
//                                Modifier.fillMaxSize().align(Alignment.Center)
//                            )
//                        }
//
//                        Tabs.PLAYBACK -> {
//                            error("not supposed for default layout")
//                        }
//                    }
//                }

//            Sidebar(viewModel, navController)
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(40.dp)
                    .background(color = KagaminTheme.backgroundTransparent),
                contentAlignment = Alignment.Center
            ) {
                AppName(
                    Modifier.padding(horizontal = 12.dp).height(25.dp)
                        //.graphicsLayer(translationY = 2f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (navController.currentDestination?.route != SettingsDestination.toString())
                                navController.navigate(SettingsDestination.toString())
                        }.align(Alignment.CenterStart)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    RepeatPlaylistPlaybackButton(audioPlayer)

                    RepeatTrackPlaybackButton(audioPlayer)

                    PlaybackButtons(audioPlayer)

                    RandomPlaybackButton(audioPlayer)

                    VolumeOptions(
                        volume = audioPlayer.volume.value,
                        onVolumeChange = { newVolume ->
                            audioPlayer.volume.value = newVolume; audioPlayer.setVolume(newVolume)
                        },
                        modifier = Modifier.width(133.dp)
                    )

                    AddTrackOrPlaylistButton(viewModel)
                }
            }
        }
    }
}

@Composable
fun AddTrackOrPlaylistButton(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    AddButton(
        onClick = {
            viewModel.currentTab = Tabs.CREATE_PLAYLIST
            viewModel.createPlaylistWindow = !viewModel.createPlaylistWindow
        },
        modifier = modifier,
        color = if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) KagaminTheme.theme.buttonIconSmallSelected else KagaminTheme.theme.buttonIconSmall,
        size = 32.dp
    )
}
