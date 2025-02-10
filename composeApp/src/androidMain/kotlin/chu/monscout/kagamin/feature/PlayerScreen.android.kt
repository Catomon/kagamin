package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.audio.DenpaTrackAndy
import chu.monscout.kagamin.createDenpaTrack
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.stars_background
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import chu.monscout.kagamin.loadPlaylist
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun PlayerScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    val denpaPlayer = state.denpaPlayer
    val playlist = state.playlist
    val currentTrack = state.currentTrack ?: DenpaTrackAndy(MediaItem.EMPTY)
    val playState = state.playState
    val playMode = state.playMode
    val currentPlaylistName = state.currentPlaylistName

    var showPlaylist by remember { mutableStateOf(false) }

    LaunchedEffect(currentPlaylistName) {
        CoroutineScope(Dispatchers.Default).launch {
            state.isLoadingPlaylistFile = true
            try {
                val trackUris = loadPlaylist(currentPlaylistName)?.tracks
                if (trackUris != null) {
                    denpaPlayer.playlist.value = mutableListOf()
                    trackUris.forEach {
                        denpaPlayer.addToPlaylist(createDenpaTrack(it.uri, it.name))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            state.isLoadingPlaylistFile = false
        }
    }

    Box(modifier.background(color = Colors.background)) {
        Image(
            painterResource(Res.drawable.stars_background), "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Colors.bars)
        )

        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            AnimatedContent(showPlaylist) {
                if (!it) {
                    CurrentTrackFrame(
                        currentTrack,
                        denpaPlayer,
                        Modifier
                            .fillMaxSize()
                            .background(color = Colors.bars.copy(alpha = 0.5f))
                    )
                } else {
                    Box(Modifier.fillMaxSize()) {
                        AnimatedContent(state.currentTab) {
                            when (it) {
                                Tabs.PLAYLISTS -> {
                                    Playlists(
                                        state,
                                        Modifier
                                            .align(Alignment.Center)
                                            .fillMaxSize()//.padding(start = 4.dp, end = 4.dp)
                                    )
                                }

                                Tabs.TRACKLIST -> {
                                    if (state.playlist.isEmpty()) {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Drop files or folders here",
                                                textAlign = TextAlign.Center,
                                                color = Colors.noteText
                                            )
                                        }
                                    } else {
                                        Tracklist(
                                            state,
                                            state.playlist,
                                            Modifier
                                                .align(Alignment.Center)
                                                .fillMaxSize()//.padding(start = 16.dp, end = 16.dp)
                                        )
                                    }
                                }

                                Tabs.OPTIONS -> TODO()

                                Tabs.ADD_TRACKS -> {
                                    AddTracksTab(
                                        state, Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center)
                                    )
                                }

                                Tabs.CREATE_PLAYLIST -> {
                                    CreatePlaylistTab(
                                        state,
                                        Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }

                //Sidebar(state, navController)
            }

            BottomBar(onPlaylistButtonClick = { showPlaylist = !showPlaylist }, Modifier.fillMaxWidth())
        }
    }
}
