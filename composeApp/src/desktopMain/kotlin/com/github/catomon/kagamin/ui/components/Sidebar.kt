package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.LayoutManager
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.ui.PlaybackTabButton
import com.github.catomon.kagamin.ui.PlaylistsTabButton
import com.github.catomon.kagamin.ui.TracklistTabButton
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.screens.SettingsDestination
import com.github.catomon.kagamin.ui.util.Tabs
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.drag
import kagamin.composeapp.generated.resources.menu
import kagamin.composeapp.generated.resources.minimize_window
import org.jetbrains.compose.resources.painterResource

@Composable
fun MinimizeButton(modifier: Modifier) {
    val window = LocalWindow.current
    TextButton({
        window.isMinimized = true
    }, modifier = modifier) {
        ImageWithShadow(
            painterResource(Res.drawable.minimize_window),
            "Minimize",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(Colors.theme.buttonIconSmall)
        )
    }
}

@Composable
fun Sidebar(
    viewModel: KagaminViewModel, navController: NavHostController, modifier: Modifier = Modifier
) {
    val layoutManager = LocalLayoutManager.current

    Column(
        modifier.fillMaxHeight().width(32.dp).background(color = Colors.backgroundTransparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MinimizeButton(modifier = Modifier.size(32.dp))

        Column(
            Modifier.width(32.dp).weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (layoutManager.currentLayout.value != LayoutManager.Layout.Default) {
                PlaybackTabButton(
                    {
                        if (viewModel.currentTab != Tabs.PLAYBACK) {
                            viewModel.currentTab = Tabs.PLAYBACK
                        }
                    },
                    color = if (viewModel.currentTab == Tabs.PLAYBACK) Colors.theme.buttonIconSmallSelected else Colors.theme.buttonIconSmall,
                    Modifier.weight(0.333f)
                )
            }

            TracklistTabButton(
                {
                    if (viewModel.currentTab != Tabs.TRACKLIST) {
                        viewModel.currentTab = Tabs.TRACKLIST
                    }
                },
                color = if (viewModel.currentTab == Tabs.TRACKLIST) Colors.theme.buttonIconSmallSelected else Colors.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            PlaylistsTabButton(
                {
                    if (viewModel.currentTab != Tabs.PLAYLISTS) {
                        viewModel.currentTab = Tabs.PLAYLISTS
                    }
                },
                color = if (viewModel.currentTab == Tabs.PLAYLISTS) Colors.theme.buttonIconSmallSelected else Colors.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            AddButton(onClick = {
                when (viewModel.currentTab) {
                    Tabs.PLAYLISTS -> {
                        viewModel.currentTab = Tabs.CREATE_PLAYLIST
                    }

                    Tabs.TRACKLIST, Tabs.PLAYBACK -> {
                        viewModel.currentTab = Tabs.ADD_TRACKS
                    }

                    else -> {
                        viewModel.currentTab =
                            if (viewModel.currentTab == Tabs.ADD_TRACKS) Tabs.TRACKLIST else Tabs.PLAYLISTS
                    }
                }
            },
                modifier = Modifier.weight(0.333f),
                color = if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) Colors.theme.buttonIconSmallSelected else Colors.theme.buttonIconSmall
            )
        }

        SwapLayoutButton(layoutManager)
    }
}

@Composable
private fun SwapLayoutButton(layoutManager: LayoutManager) {
    TextButton(
        onClick = {
            when (layoutManager.currentLayout.value) {
                LayoutManager.Layout.Default -> {
                    layoutManager.currentLayout.value = LayoutManager.Layout.Compact
                }

                LayoutManager.Layout.Compact -> {
                    layoutManager.currentLayout.value = LayoutManager.Layout.Tiny
                }

                LayoutManager.Layout.Tiny -> {
                    layoutManager.currentLayout.value = LayoutManager.Layout.Default
                }
            }
        }, modifier = Modifier.size(32.dp)
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.drag),
            "drag window",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(Colors.theme.buttonIconSmall)
        )
    }
}

@Composable
private fun MenuButton(navController: NavHostController, modifier: Modifier) {
    TextButton(modifier = modifier, onClick = {
        navController.navigate(SettingsDestination.toString())
    }) {
        ImageWithShadow(
            painterResource(Res.drawable.menu),
            "Menu",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(Colors.theme.buttonIconSmall)
        )
    }
}