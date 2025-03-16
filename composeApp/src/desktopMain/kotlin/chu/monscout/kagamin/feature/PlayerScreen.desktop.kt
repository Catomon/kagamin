package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.createDenpaTrack
import chu.monscout.kagamin.loadPlaylist
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.star64
import kagamin.composeapp.generated.resources.stars_background
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
        Image(
            painterResource(Res.drawable.stars_background),
            "Background",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.background2)
        )

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
                    currentTrack, denpaPlayer, Modifier.width(160.dp).fillMaxHeight()
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
    val denpaPlayer = state.denpaPlayer
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
        Image(
            painterResource(Res.drawable.stars_background),
            "Background",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.background2)
        )

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
                                    denpaPlayer,
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

@Composable
fun TinyPlayerScreen(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val denpaPlayer = state.denpaPlayer
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
        Image(
            painterResource(Res.drawable.stars_background),
            "Background",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.background2)
        )

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
                                    denpaPlayer,
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

@Composable
private fun AppName(modifier: Modifier = Modifier) {
    AppNameNormal(modifier)
}

@Composable
private fun AppNameNormal(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = "Kag",
            color = Colors.currentYukiTheme.playerButtonIcon,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
        )
        Image(
            painterResource(Res.drawable.star64),
            "App icon",
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon),
            modifier = Modifier.size(32.dp).offset(y = (-3).dp)
        )
        Text(
            text = "min",
            color = Colors.currentYukiTheme.playerButtonIcon,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
        )
    }
}

@Composable
private fun AppNameOutlined(modifier: Modifier = Modifier) {
    Row(modifier) {
        OutlinedText(
            text = "Kag",
            fillColor = Colors.currentYukiTheme.playerButtonIcon,
            outlineColor = Colors.currentYukiTheme.thinBorder,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
            outlineDrawStyle = Stroke(4f)
        )
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.thinBorder),
                modifier = Modifier.size(32.dp).offset(y = (-3).dp).graphicsLayer(
                        scaleX = 1.25f, scaleY = 1.25f
                    )
            )
            Image(
                painterResource(Res.drawable.star64),
                "App icon",
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon),
                modifier = Modifier.size(30.dp).offset(y = (-3).dp)
            )
        }
        OutlinedText(
            text = "min",
            fillColor = Colors.currentYukiTheme.playerButtonIcon,
            outlineColor = Colors.currentYukiTheme.thinBorder,
            fontSize = 18.sp,
            modifier = Modifier.height(32.dp),
            outlineDrawStyle = Stroke(4f)
        )
    }
}

