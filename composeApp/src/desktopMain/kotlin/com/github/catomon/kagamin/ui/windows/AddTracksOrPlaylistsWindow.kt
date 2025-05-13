package com.github.catomon.kagamin.ui.windows

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.WindowConfig
import com.github.catomon.kagamin.WindowDraggableArea
import com.github.catomon.kagamin.kagaminWindowDecoration
import com.github.catomon.kagamin.ui.AddTracksTab
import com.github.catomon.kagamin.ui.CreatePlaylistTab
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.arrow_left
import org.jetbrains.compose.resources.painterResource

@Composable
fun ApplicationScope.AddTracksOrPlaylistsWindow(
    viewModel: KagaminViewModel, modifier: Modifier = Modifier
) {
    val audioPlayer = viewModel.audioPlayer
    val currentTrack = viewModel.currentTrack

    val tabTransition: (Tabs) -> ContentTransform = { tab ->
        when (tab) {
            Tabs.ADD_TRACKS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            Tabs.CREATE_PLAYLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            else -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }
    }

    Window(
        state = rememberWindowState(
            width = WindowConfig.WIDTH.dp, height = WindowConfig.HEIGHT.dp
        ),
        onCloseRequest = { viewModel.createPlaylistWindow = false },
        undecorated = true,
        transparent = true
    ) {
        KagaminTheme {
            WindowDraggableArea {
                Scaffold(
                    snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) },
                    modifier = modifier.kagaminWindowDecoration()
                ) {
                    Box {
                        TrackThumbnail(
                            currentTrack?.uri,
                            onSetProgress = {
                                if (currentTrack != null) audioPlayer.seek((currentTrack.duration * it).toLong())
                            },
                            0f,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            blur = true,
                            controlProgress = false
                        )

                        Box(
                            Modifier.fillMaxSize()
                        ) {
                            AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
                                tabTransition(viewModel.currentTab)
                            }) {
                                when (it) {
                                    Tabs.PLAYLISTS -> {
                                        CreatePlaylistTab(
                                            viewModel,
                                            Modifier.fillMaxSize().align(Alignment.Center)
                                        )
                                    }

                                    Tabs.TRACKLIST -> {
                                        AddTracksTab(
                                            viewModel,
                                            Modifier.fillMaxSize().align(Alignment.Center)
                                        )
                                    }

                                    Tabs.OPTIONS -> error("not supposed for not main window")

                                    Tabs.ADD_TRACKS -> {
                                        AddTracksTab(
                                            viewModel,
                                            Modifier.fillMaxSize().align(Alignment.Center)
                                        )
                                    }

                                    Tabs.CREATE_PLAYLIST -> {
                                        CreatePlaylistTab(
                                            viewModel,
                                            Modifier.fillMaxSize().align(Alignment.Center)
                                        )
                                    }

                                    Tabs.PLAYBACK -> {

                                    }
                                }
                            }

                            IconButton({
                                viewModel.createPlaylistWindow = false
                                viewModel.currentTab = Tabs.TRACKLIST
                            }, modifier = Modifier.align(Alignment.BottomEnd)) {
                                Icon(painterResource(Res.drawable.arrow_left), contentDescription = null, tint = KagaminTheme.colors.buttonIcon)
                            }
                        }
                    }
                }
            }
        }
    }
}
