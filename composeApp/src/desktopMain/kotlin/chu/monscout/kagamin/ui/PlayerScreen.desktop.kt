package chu.monscout.kagamin.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.createAudioTrack
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
        BackgroundImage()

        Row() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().background(color = Colors.barsTransparent)
            ) {
                AppName(Modifier.padding(horizontal = 12.dp).padding(top = 8.dp).height(32.dp)
                    .clickable {
                        if (navController.currentDestination?.route != SettingsDestination.toString())
                            navController.navigate(SettingsDestination.toString())
                    })

                CurrentTrackFrame(
                    currentTrack, audioPlayer, Modifier.width(160.dp).fillMaxHeight()
                )
            }


            Box(Modifier.weight(0.75f)) {
                AnimatedContent(targetState = state.currentTab, transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                }) {
                    when (it) {
                        Tabs.PLAYLISTS -> {
                            Playlists(
                                state,
                                Modifier.align(Alignment.Center)
                                    .fillMaxSize()//.padding(start = 4.dp, end = 4.dp)
                            )
                        }

                        Tabs.TRACKLIST -> {
                            if (state.playlist.isEmpty()) {
                                Box(
                                    Modifier.fillMaxSize()
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
                                        .fillMaxSize()//.padding(start = 16.dp, end = 16.dp)
                                )
                            }
                        }

                        Tabs.OPTIONS -> TODO()

                        Tabs.ADD_TRACKS -> {
                            AddTracksTab(state, Modifier.fillMaxSize().align(Alignment.Center))
                        }

                        Tabs.CREATE_PLAYLIST -> {
                            CreatePlaylistTab(state, Modifier.fillMaxSize().align(Alignment.Center))
                        }

                        Tabs.PLAYBACK -> {
                            error("not supposed for default layout")
                        }
                    }
                }

                if (state.currentTab != Tabs.PLAYBACK)
                       AddButton(painterResource(
                        if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.arrow_left
                        else Res.drawable.add
                    ), {
                        
                        when (state.currentTab) {
                            Tabs.PLAYLISTS -> {
                                state.currentTab = Tabs.CREATE_PLAYLIST
                            }

                            Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                                state.currentTab = Tabs.ADD_TRACKS
                            }

                            else -> {
                                state.currentTab =
                                    if (state.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                            }
                        }

                    }, Modifier.align(Alignment.BottomEnd)
                    )
            }

            Sidebar(state, navController)
        }
    }
}

@Composable
fun CompactPlayerScreen(
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
        BackgroundImage()

        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().weight(0.99f)
            ) {
                AppName(Modifier.background(color = Colors.barsTransparent)
                    .padding(horizontal = 12.dp).padding(top = 8.dp).height(32.dp).fillMaxWidth()
                    .clickable(onClickLabel = "Open options") {
                        navController.navigate(SettingsDestination.toString())
                    })

                Box(Modifier.weight(0.99f).fillMaxHeight()) {
                    AnimatedContent(targetState = state.currentTab, transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }) {
                        when (it) {
                            Tabs.PLAYBACK -> {
                                CurrentTrackFrame(
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

                    if (state.currentTab != Tabs.PLAYBACK)
                           AddButton(painterResource(
                        if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.arrow_left
                        else Res.drawable.add
                    ), {
                        
                        when (state.currentTab) {
                            Tabs.PLAYLISTS -> {
                                state.currentTab = Tabs.CREATE_PLAYLIST
                            }

                            Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                                state.currentTab = Tabs.ADD_TRACKS
                            }

                            else -> {
                                state.currentTab =
                                    if (state.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                            }
                        }

                    }, Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            Sidebar(state, navController)
        }
    }
}

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
        BackgroundImage()

        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().weight(0.99f)
            ) {
                AppName(Modifier.background(color = Colors.barsTransparent)
                    .padding(horizontal = 12.dp).padding(top = 8.dp).height(32.dp).fillMaxWidth()
                    .clickable {
                        navController.navigate(SettingsDestination.toString())
                    })

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

                    if (state.currentTab != Tabs.PLAYBACK)
                           AddButton(painterResource(
                        if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.arrow_left
                        else Res.drawable.add
                    ), {
                        
                        when (state.currentTab) {
                            Tabs.PLAYLISTS -> {
                                state.currentTab = Tabs.CREATE_PLAYLIST
                            }

                            Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                                state.currentTab = Tabs.ADD_TRACKS
                            }

                            else -> {
                                state.currentTab =
                                    if (state.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                            }
                        }

                    }, Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            Sidebar(state, navController)
        }
    }
}
