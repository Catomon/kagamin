package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.LocalWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.arrow_left
import kagamin.composeapp.generated.resources.drag
import kagamin.composeapp.generated.resources.menu
import kagamin.composeapp.generated.resources.minimize_window
import kagamin.composeapp.generated.resources.music_note
import kagamin.composeapp.generated.resources.playlists
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
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun Sidebar(state: KagaminViewModel, navController: NavHostController) {
    Column(
        Modifier.fillMaxHeight().width(32.dp).background(color = Colors.bars.copy(alpha = 0.5f)),
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
                modifier = Modifier.size(32.dp)
            )
        }

        TextButton(
            modifier = Modifier.weight(0.3f),
            onClick = {
                if (state.currentTab == Tabs.TRACKLIST || state.currentTab == Tabs.CREATE_PLAYLIST)
                    state.currentTab = Tabs.PLAYLISTS
                else state.currentTab = Tabs.TRACKLIST
            }
        ) {
            Image(
                painterResource(if (state.currentTab == Tabs.TRACKLIST || state.currentTab == Tabs.CREATE_PLAYLIST) Res.drawable.music_note else Res.drawable.playlists),
                "Playlists/Tracklist tab swap button",
                modifier = Modifier.size(32.dp)
            )
        }

        TextButton(
            modifier = Modifier.weight(0.3f),
            onClick = {
                when (state.currentTab) {
                    Tabs.PLAYLISTS -> {
                        state.currentTab = Tabs.CREATE_PLAYLIST
                    }

                    Tabs.TRACKLIST -> {
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
                modifier = Modifier.size(16.dp)
            )
        }

        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
            Image(
                painterResource(Res.drawable.drag),
                "drag window",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}