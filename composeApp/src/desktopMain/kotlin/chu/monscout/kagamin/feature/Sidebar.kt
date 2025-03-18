package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.LayoutManager
import chu.monscout.kagamin.LocalLayoutManager
import chu.monscout.kagamin.LocalWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.arrow_left
import kagamin.composeapp.generated.resources.drag
import kagamin.composeapp.generated.resources.menu
import kagamin.composeapp.generated.resources.minimize_window
import kagamin.composeapp.generated.resources.music_note
import kagamin.composeapp.generated.resources.playlists
import kagamin.composeapp.generated.resources.tiny_star_icon
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
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
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
                    color = if (state.currentTab == Tabs.PLAYBACK) Color.White else Colors.currentYukiTheme.smallButtonIcon,
                    Modifier.weight(0.333f)
                )
            }

            TracklistTabButton(
                {
                    if (state.currentTab != Tabs.TRACKLIST) {
                        state.currentTab = Tabs.TRACKLIST
                    }
                },
                color = if (state.currentTab == Tabs.TRACKLIST) Color.White else Colors.currentYukiTheme.smallButtonIcon,
                Modifier.weight(0.333f)
            )

            PlaylistsTabButton(
                {
                    if (state.currentTab != Tabs.PLAYLISTS) {
                        state.currentTab = Tabs.PLAYLISTS
                    }
                },
                color = if (state.currentTab == Tabs.PLAYLISTS) Color.White else Colors.currentYukiTheme.smallButtonIcon,
                Modifier.weight(0.333f)
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
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
        )
    }
}

@Composable
fun AddButton(state: KagaminViewModel, modifier: Modifier = Modifier) {
    TextButton(shape = CircleShape, modifier = modifier.padding(4.dp).size(32.dp).background(color = Colors.barsTransparent, shape = CircleShape).clip(
        CircleShape), onClick = {
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
    }) {
        ImageWithShadow(
            painterResource(
                if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.arrow_left
                else Res.drawable.add
            ),
            "Add button",
            modifier = Modifier.size(16.dp).graphicsLayer(scaleX = 1.25f, scaleY = 1.25f),
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
        )
    }
}

@Composable
private fun TracklistTabButton(
    onClick: () -> Unit,
    color: Color = Colors.currentYukiTheme.smallButtonIcon,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.music_note),
            "Tracklist tab",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
private fun PlaylistsTabButton(
    onClick: () -> Unit,
    color: Color = Colors.currentYukiTheme.smallButtonIcon,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.playlists),
            "Tracklist tab",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(color)
        )
    }
}

@Composable
private fun PlaybackTabButton(
    onClick: () -> Unit,
    color: Color = Colors.currentYukiTheme.smallButtonIcon,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier, onClick = onClick
    ) {
        ImageWithShadow(
            painterResource(Res.drawable.tiny_star_icon),
            "Tracklist tab",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(color)
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
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
        )
    }
}