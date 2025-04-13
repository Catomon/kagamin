package chu.monscout.kagamin.ui.components

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
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.LayoutManager
import chu.monscout.kagamin.LocalLayoutManager
import chu.monscout.kagamin.LocalWindow
import chu.monscout.kagamin.ui.PlaybackTabButton
import chu.monscout.kagamin.ui.PlaylistsTabButton
import chu.monscout.kagamin.ui.TracklistTabButton
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import chu.monscout.kagamin.ui.screens.SettingsDestination
import chu.monscout.kagamin.ui.util.Tabs
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
            colorFilter = ColorFilter.tint(Colors.theme.smallButtonIcon)
        )
    }
}

@Composable
fun Sidebar(
    state: KagaminViewModel, navController: NavHostController, modifier: Modifier = Modifier
) {
    val layoutManager = LocalLayoutManager.current

    Column(
        modifier.fillMaxHeight().width(32.dp).background(color = Colors.barsTransparent),
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
                        if (state.currentTab != Tabs.PLAYBACK) {
                            state.currentTab = Tabs.PLAYBACK
                        }
                    },
                    color = if (state.currentTab == Tabs.PLAYBACK) Colors.theme.selectedButton else Colors.theme.smallButtonIcon,
                    Modifier.weight(0.333f)
                )
            }

            TracklistTabButton(
                {
                    if (state.currentTab != Tabs.TRACKLIST) {
                        state.currentTab = Tabs.TRACKLIST
                    }
                },
                color = if (state.currentTab == Tabs.TRACKLIST) Colors.theme.selectedButton else Colors.theme.smallButtonIcon,
                Modifier.weight(0.333f)
            )

            PlaylistsTabButton(
                {
                    if (state.currentTab != Tabs.PLAYLISTS) {
                        state.currentTab = Tabs.PLAYLISTS
                    }
                },
                color = if (state.currentTab == Tabs.PLAYLISTS) Colors.theme.selectedButton else Colors.theme.smallButtonIcon,
                Modifier.weight(0.333f)
            )

            AddButton(onClick = {
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
            },
                modifier = Modifier.weight(0.333f),
                color = if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST) Colors.theme.selectedButton else Colors.theme.smallButtonIcon
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
            colorFilter = ColorFilter.tint(Colors.theme.smallButtonIcon)
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
            colorFilter = ColorFilter.tint(Colors.theme.smallButtonIcon)
        )
    }
}