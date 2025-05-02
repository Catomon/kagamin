package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
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
fun MinimizeButton(modifier: Modifier = Modifier) {
    val window = LocalWindow.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clip(RoundedCornerShape(12.dp)).clickable { window.isMinimized = true }
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.minimize_window),
            "Minimize",
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(KagaminTheme.theme.buttonIconSmall)
        )
    }
}

@Composable
fun Sidebar(
    viewModel: KagaminViewModel, navController: NavHostController, modifier: Modifier = Modifier
) {
    val layoutManager = LocalLayoutManager.current

    Column(
        modifier.fillMaxHeight().width(32.dp)
            .background(color = KagaminTheme.backgroundTransparent),
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
                    color = if (viewModel.currentTab == Tabs.PLAYBACK) KagaminTheme.theme.buttonIconSmallSelected else KagaminTheme.theme.buttonIconSmall,
                    Modifier.weight(0.333f)
                )
            }

            TracklistTabButton(
                {
                    if (viewModel.currentTab != Tabs.TRACKLIST) {
                        viewModel.currentTab = Tabs.TRACKLIST
                    }
                },
                color = if (viewModel.currentTab == Tabs.TRACKLIST) KagaminTheme.theme.buttonIconSmallSelected else KagaminTheme.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            PlaylistsTabButton(
                {
                    if (viewModel.currentTab != Tabs.PLAYLISTS) {
                        viewModel.currentTab = Tabs.PLAYLISTS
                    }
                },
                color = if (viewModel.currentTab == Tabs.PLAYLISTS) KagaminTheme.theme.buttonIconSmallSelected else KagaminTheme.theme.buttonIconSmall,
                Modifier.weight(0.333f)
            )

            AddButton(
                onClick = {
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
                color = if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) KagaminTheme.theme.buttonIconSmallSelected else KagaminTheme.theme.buttonIconSmall
            )
        }

        SwapLayoutButton(layoutManager)
    }
}

@Composable
private fun SwapLayoutButton(layoutManager: LayoutManager) {
    IconButton(
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
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(KagaminTheme.theme.buttonIconSmall)
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
            colorFilter = ColorFilter.tint(KagaminTheme.theme.buttonIconSmall)
        )
    }
}