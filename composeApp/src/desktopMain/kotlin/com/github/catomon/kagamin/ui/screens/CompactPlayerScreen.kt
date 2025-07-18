package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.ui.AddTracksTab
import com.github.catomon.kagamin.ui.CreatePlaylistTab
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.AppLogo
import com.github.catomon.kagamin.ui.components.Background
import com.github.catomon.kagamin.ui.components.CurrentTrackFrame
import com.github.catomon.kagamin.ui.components.Sidebar
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel

@Composable
fun CompactPlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()

    val tabTransition: (Tabs) -> ContentTransform = { tab ->
        when (tab) {
            Tabs.ADD_TRACKS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            Tabs.CREATE_PLAYLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.TRACKLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.PLAYLISTS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }

            else -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }
    }

    val layoutManager = LocalLayoutManager.current

    Box(modifier) {
        Background(currentTrack, Modifier.fillMaxSize())

        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().weight(0.99f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                        .background(color = KagaminTheme.backgroundTransparent)
                ) {
//                    AppName(
//                        Modifier
//                            .height(25.dp).graphicsLayer(translationY = 2f)
//                            .clip(RoundedCornerShape(8.dp))
//                            .clickable(onClickLabel = "Open options") {
//                                navController.navigate(SettingsDestination.toString())
//                            })
                    AppLogo(  Modifier.padding(horizontal = 12.dp).height(30.dp)
                        .graphicsLayer(translationY = 2f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            layoutManager.currentLayout.value = LayoutManager.Layout.Default
                            if (navController.currentDestination?.route != SettingsDestination.toString())
                                navController.navigate(SettingsDestination.toString())
                        })
                }

                Box(Modifier.weight(0.99f).fillMaxHeight()) {
                    AnimatedContent(targetState = viewModel.currentTab, transitionSpec = {
                       tabTransition(viewModel.currentTab)
                    }) {
                        when (it) {
                            Tabs.PLAYBACK -> {
                                CurrentTrackFrame(
                                    viewModel,
                                    currentTrack,
                                    Modifier.width(160.dp).fillMaxHeight()
                                        .background(color = KagaminTheme.backgroundTransparent)
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
                                if (currentPlaylist.tracks.isEmpty()) {
                                    Box(
                                        Modifier.fillMaxHeight()
                                            .background(KagaminTheme.colors.backgroundTransparent),
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
                                        Modifier.align(Alignment.Center)
                                            .fillMaxHeight()
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

            Sidebar(viewModel)
        }
    }
}