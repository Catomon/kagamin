package chu.monscout.kagamin.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.MultiFilePicker
import chu.monscout.kagamin.audio.createAudioTrack
import chu.monscout.kagamin.loadPlaylist
import chu.monscout.kagamin.ui.components.AddButton
import chu.monscout.kagamin.ui.AddTracksTab
import chu.monscout.kagamin.ui.components.AppName
import chu.monscout.kagamin.ui.components.BackgroundImage
import chu.monscout.kagamin.ui.BottomBar
import chu.monscout.kagamin.ui.CreatePlaylistTab
import chu.monscout.kagamin.ui.Playlists
import chu.monscout.kagamin.ui.Tracklist
import chu.monscout.kagamin.ui.components.CurrentTrackFrame2
import chu.monscout.kagamin.ui.util.Tabs
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.arrow_left
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun PlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier,
) {
    val audioPlayer = viewModel.audioPlayer
    val playlist = viewModel.playlist
    val currentTrack = viewModel.currentTrack
    val playState = viewModel.playState
    val playMode = viewModel.playMode
    val currentPlaylistName = viewModel.currentPlaylistName
    val showFilePicker = remember { mutableStateOf(false) }

    MultiFilePicker(showFilePicker, viewModel.audioPlayer, viewModel.currentPlaylistName)

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

    Box(modifier.background(color = Colors.behindBackground, shape = RoundedCornerShape(16.dp))) {
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
                    .background(color = Colors.backgroundTransparent)
                    .padding(horizontal = 12.dp)
                    .padding(top = 8.dp)
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable(onClickLabel = "Open options") {
                        navController.navigate(SettingsDestination.toString())
                    }, height = 50.dp, 36.sp)

                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .weight(0.99f)
                ) {
                    AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    }) {
                        when (it) {
                            Tabs.PLAYBACK -> {
                                CurrentTrackFrame2(
                                    viewModel.trackThumbnail,
                                    currentTrack,
                                    audioPlayer,
                                    Modifier
                                        .fillMaxSize().background(color = Colors.backgroundTransparent).padding(horizontal = 48.dp)
                                )
                            }

                            Tabs.PLAYLISTS -> {
                                Playlists(
                                    viewModel,
                                    Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize()
                                )
                            }

                            Tabs.TRACKLIST -> {
                                if (viewModel.playlist.isEmpty()) {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
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
                                        Modifier
                                            .align(Alignment.Center)
                                            .fillMaxSize()
                                    )
                                }
                            }

                            Tabs.OPTIONS -> TODO()

                            Tabs.ADD_TRACKS -> {
                                AddTracksTab(
                                    viewModel, Modifier
                                        .fillMaxHeight()
                                        .align(Alignment.Center)
                                )
                            }

                            Tabs.CREATE_PLAYLIST -> {
                                CreatePlaylistTab(
                                    viewModel, Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }

                    if (viewModel.currentTab != Tabs.PLAYBACK)
                        AddButton(
                            painterResource(
                                if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.arrow_left
                                else Res.drawable.add
                            ),
                            onClick = {
                                when (viewModel.currentTab) {
                                    Tabs.PLAYLISTS -> {
                                        viewModel.currentTab = Tabs.CREATE_PLAYLIST
                                    }

                                    Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                                        showFilePicker.value = true
                                    }

                                    else -> {
                                        viewModel.currentTab =
                                            if (viewModel.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                                    }
                                }

                            }, modifier = Modifier.align(Alignment.BottomEnd), if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) Color.White else Colors.theme.buttonIconSmall
                        )
                }
            }

            BottomBar(viewModel, navController)
        }
    }
}
