package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
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
        Image(
            painterResource(Res.drawable.minimize_window),
            "Minimize",
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
        )
    }
}

@Composable
fun Sidebar(
    state: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val layoutManager = LocalLayoutManager.current

    Column(
        modifier.fillMaxHeight().width(32.dp).background(color = Colors.barsTransparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MinimizeButton(modifier = Modifier.size(32.dp))

        TextButton(
            modifier = Modifier.weight(0.15f),
            onClick = {
                navController.navigate(SettingsDestination.toString())
            }
        ) {
            Image(
                painterResource(Res.drawable.menu),
                "Menu",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
            )
        }

        TextButton(
            modifier = Modifier.weight(0.3f),
            onClick = {
                state.currentTab = when (state.currentTab) {
                    Tabs.TRACKLIST, Tabs.CREATE_PLAYLIST -> Tabs.PLAYLISTS
                    Tabs.PLAYBACK -> Tabs.TRACKLIST
                    else -> if (layoutManager.currentLayout.value == LayoutManager.Layout.Default) Tabs.TRACKLIST else Tabs.PLAYBACK
                }
            }
        ) {
            Image(
                painterResource(
                    when (state.currentTab) {
                        Tabs.TRACKLIST, Tabs.CREATE_PLAYLIST -> Res.drawable.music_note
                        Tabs.PLAYBACK -> Res.drawable.playlists
                        else -> if (layoutManager.currentLayout.value == LayoutManager.Layout.Default) Res.drawable.playlists else Res.drawable.tiny_star_icon
                    }
                ),
                "Playlists/Tracklist tab swap button",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
            )
        }

        TextButton(
            modifier = Modifier.weight(0.3f),
            onClick = {
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
            }
        ) {
            Image(
                painterResource(
                    if (state.currentTab == Tabs.ADD_TRACKS || state.currentTab == Tabs.CREATE_PLAYLIST)
                        Res.drawable.arrow_left
                    else
                        Res.drawable.add
                ),
                "Add button",
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
            )
        }

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
            },
            modifier = Modifier.size(32.dp)
        ) {
            Image(
                painterResource(Res.drawable.drag),
                "drag window",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.smallButtonIcon)
            )
        }
    }
}