package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.audio.createAudioTrack
import com.github.catomon.kagamin.loadPlaylist
import com.github.catomon.kagamin.ui.AddTracksTab
import com.github.catomon.kagamin.ui.CreatePlaylistTab
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.AppName
import com.github.catomon.kagamin.ui.components.CurrentTrackFrame
import com.github.catomon.kagamin.ui.components.Sidebar
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CompactPlayerScreen(
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

    LaunchedEffect(currentPlaylistName) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModel.isLoadingPlaylistFile = true
            try {
                val trackUris = loadPlaylist(currentPlaylistName)?.tracks
                if (trackUris != null) {
                    audioPlayer.playlist.value = mutableListOf()
                    trackUris.forEach {
                        audioPlayer.addToPlaylist(createAudioTrack(it.uri, it.name))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.isLoadingPlaylistFile = false
        }
    }

    LaunchedEffect(currentTrack) {
        viewModel.updateThumbnail()
    }

    Box(modifier.background(color = Colors.behindBackground, shape = RoundedCornerShape(16.dp))) {
        TrackThumbnail(
            image = viewModel.trackThumbnail,
            onSetProgress = {
                if (currentTrack != null) {
                    audioPlayer.seek((currentTrack.duration * it).toLong())
                }
            },
            progress = 0f,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            blur = true,
            controlProgress = false
        )

//        BackgroundImage()

        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().weight(0.99f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Colors.backgroundTransparent)
                ) {
                    AppName(
                        Modifier
                            .height(25.dp).graphicsLayer(translationY = 2f)
                            .clickable(onClickLabel = "Open options") {
                                navController.navigate(SettingsDestination.toString())
                            })
                }

                Box(Modifier.weight(0.99f).fillMaxHeight()) {
                    AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }) {
                        when (it) {
                            Tabs.PLAYBACK -> {
                                CurrentTrackFrame(
                                    viewModel,
                                    viewModel.trackThumbnail,
                                    currentTrack,
                                    audioPlayer,
                                    Modifier.width(160.dp).fillMaxHeight()
                                        .background(color = Colors.backgroundTransparent)
                                )
                            }

                            Tabs.PLAYLISTS -> {
                                Playlists(
                                    viewModel,
                                    Modifier.align(Alignment.Center)
                                        .fillMaxHeight()//.padding(start = 4.dp, end = 4.dp)
                                )
                            }

                            Tabs.TRACKLIST -> {
                                if (viewModel.playlist.isEmpty()) {
                                    Box(
                                        Modifier.fillMaxHeight()
                                            .background(Colors.theme.listItemB),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Drop files or folders here",
                                            textAlign = TextAlign.Center,
                                            color = Colors.textSecondary
                                        )
                                    }
                                } else {
                                    Tracklist(
                                        viewModel,
                                        viewModel.playlist,
                                        Modifier.align(Alignment.Center)
                                            .fillMaxHeight()//.padding(start = 16.dp, end = 16.dp)
                                    )
                                }
                            }

                            Tabs.OPTIONS -> TODO()

                            Tabs.ADD_TRACKS -> {
                                AddTracksTab(
                                    viewModel, Modifier.fillMaxHeight().align(Alignment.Center)
                                )
                            }

                            Tabs.CREATE_PLAYLIST -> {
                                CreatePlaylistTab(
                                    viewModel, Modifier.fillMaxHeight().align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            Sidebar(viewModel, navController)
        }
    }
}