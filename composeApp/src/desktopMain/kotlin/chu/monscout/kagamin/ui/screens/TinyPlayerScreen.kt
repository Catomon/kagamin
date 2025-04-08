package chu.monscout.kagamin.ui.screens

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
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.createAudioTrack
import chu.monscout.kagamin.loadPlaylist
import chu.monscout.kagamin.ui.components.AddButton
import chu.monscout.kagamin.ui.AddTracksTab
import chu.monscout.kagamin.ui.components.AppName
import chu.monscout.kagamin.ui.components.CompactCurrentTrackFrame
import chu.monscout.kagamin.ui.CreatePlaylistTab
import chu.monscout.kagamin.ui.Playlists
import chu.monscout.kagamin.ui.components.Sidebar
import chu.monscout.kagamin.ui.Tracklist
import chu.monscout.kagamin.ui.components.TrackThumbnail
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.arrow_left
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun TinyPlayerScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val audioPlayer = state.audioPlayer
    val playlist = state.playlist
    val currentTrack = state.currentTrack
    val playState = state.playState
    val playMode = state.playMode
    val currentPlaylistName = state.currentPlaylistName

    LaunchedEffect(currentPlaylistName) {
        CoroutineScope(Dispatchers.Default).launch {
            state.isLoadingPlaylistFile = true
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
            state.isLoadingPlaylistFile = false
        }
    }

    Box(modifier.background(color = Colors.background, shape = RoundedCornerShape(16.dp))) {
        TrackThumbnail(
            currentTrack,
            audioPlayer,
            {},
            0f,
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
                    modifier = Modifier.fillMaxWidth().background(color = Colors.barsTransparent)
                ) {
                    AppName(Modifier
                        .height(25.dp).graphicsLayer(translationY = 2f)
                        .clickable {
                            navController.navigate(SettingsDestination.toString())
                        })
                }

                Box(Modifier.weight(0.99f).fillMaxHeight()) {
                    AnimatedContent(targetState = state.currentTab, transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }) {
                        when (it) {
                            Tabs.PLAYBACK -> {
                                CompactCurrentTrackFrame(
                                    currentTrack,
                                    audioPlayer,
                                    Modifier.width(160.dp).fillMaxHeight()
                                        .background(color = Colors.barsTransparent)
                                )
                            }

                            Tabs.PLAYLISTS -> {
                                Playlists(
                                    state,
                                    Modifier.align(Alignment.Center)
                                        .fillMaxHeight()//.padding(start = 4.dp, end = 4.dp)
                                )
                            }

                            Tabs.TRACKLIST -> {
                                if (state.playlist.isEmpty()) {
                                    Box(
                                        Modifier.fillMaxHeight()
                                            .background(Colors.currentYukiTheme.listItemB),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Drop files or folders here",
                                            textAlign = TextAlign.Center,
                                            color = Colors.text2
                                        )
                                    }
                                } else {
                                    Tracklist(
                                        state,
                                        state.playlist,
                                        Modifier.align(Alignment.Center)
                                            .fillMaxHeight()//.padding(start = 16.dp, end = 16.dp)
                                    )
                                }
                            }

                            Tabs.OPTIONS -> TODO()

                            Tabs.ADD_TRACKS -> {
                                AddTracksTab(
                                    state, Modifier.fillMaxHeight().align(Alignment.Center)
                                )
                            }

                            Tabs.CREATE_PLAYLIST -> {
                                CreatePlaylistTab(
                                    state, Modifier.fillMaxHeight().align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            Sidebar(state, navController)
        }
    }
}