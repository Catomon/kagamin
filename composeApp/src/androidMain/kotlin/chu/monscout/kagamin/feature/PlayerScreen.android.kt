package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.DenpaFilePicker
import chu.monscout.kagamin.createDenpaTrack
import chu.monscout.kagamin.loadPlaylist
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.arrow_left
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun PlayerScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier,
) {
    val denpaPlayer = state.denpaPlayer
    val playlist = state.playlist
    val currentTrack = state.currentTrack
    val playState = state.playState
    val playMode = state.playMode
    val currentPlaylistName = state.currentPlaylistName
    val showFilePicker = remember { mutableStateOf(false) }

    DenpaFilePicker(showFilePicker, state.denpaPlayer, state.currentPlaylistName)

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

    Box(modifier.background(color = Colors.background, shape = RoundedCornerShape(16.dp))) {
        BackgroundImage()

        Column(Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.99f)
                    .fillMaxWidth()
            ) {
                AppName(Modifier
                    .background(color = Colors.barsTransparent)
                    .padding(horizontal = 12.dp)
                    .padding(top = 8.dp)
                    .height(32.dp)
                    .fillMaxWidth()
                    .clickable(onClickLabel = "Open options") {
                        navController.navigate(SettingsDestination.toString())
                    })

                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .weight(0.99f)
                ) {
                    AnimatedContent(targetState = state.currentTab, transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }) {
                        when (it) {
                            Tabs.PLAYBACK -> {
                                CurrentTrackFrame(
                                    currentTrack,
                                    denpaPlayer,
                                    Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .width(160.dp)
                                        .background(color = Colors.barsTransparent)
                                )
                            }

                            Tabs.PLAYLISTS -> {
                                Playlists(
                                    state,
                                    Modifier
                                        .align(Alignment.Center)
                                        .fillMaxHeight()//.padding(start = 4.dp, end = 4.dp)
                                )
                            }

                            Tabs.TRACKLIST -> {
                                if (state.playlist.isEmpty()) {
                                    Box(
                                        Modifier
                                            .fillMaxHeight()
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
                                        Modifier
                                            .align(Alignment.Center)
                                            .fillMaxHeight()//.padding(start = 16.dp, end = 16.dp)
                                    )
                                }
                            }

                            Tabs.OPTIONS -> TODO()

                            Tabs.ADD_TRACKS -> {
                                AddTracksTab(
                                    state, Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.Center)
                                )
                            }

                            Tabs.CREATE_PLAYLIST -> {
                                CreatePlaylistTab(
                                    state, Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }

                    if (state.currentTab != Tabs.PLAYBACK)
                        AddButton(
                            painterResource(
                                if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.arrow_left
                                else Res.drawable.add
                            ),
                            onClick = {
                                when (state.currentTab) {
                                    Tabs.PLAYLISTS -> {
                                        state.currentTab = Tabs.CREATE_PLAYLIST
                                    }

                                    Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                                        showFilePicker.value = true
                                    }

                                    else -> {
                                        state.currentTab =
                                            if (state.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                                    }
                                }

                            }, modifier = Modifier.align(Alignment.BottomEnd)
                        )
                }
            }

            BottomBar(state, navController)
        }
    }
}
