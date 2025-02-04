package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.catomon.yukinotes.feature.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.menu
import kagamin.composeapp.generated.resources.music_note
import kagamin.composeapp.generated.resources.playlists
import org.jetbrains.compose.resources.painterResource

@Composable
expect fun MinimizeButton(modifier: Modifier = Modifier)

@Composable
fun Sidebar(state: KagaminViewModel) {
    Column(Modifier.fillMaxHeight().width(32.dp).background(color = Colors.bars)) {
        MinimizeButton(modifier = Modifier.weight(0.15f))

        TextButton(
            modifier = Modifier.weight(0.15f),
            onClick = {

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
                if (state.currentTab == Tabs.TRACKLIST)
                    state.currentTab = Tabs.PLAYLISTS
                else state.currentTab = Tabs.TRACKLIST
            }
        ) {
            Image(
                painterResource(if (state.currentTab == Tabs.TRACKLIST) Res.drawable.music_note else Res.drawable.playlists),
                "Playlists/Tracklist tab swap button",
                modifier = Modifier.size(32.dp)
            )
        }

        if (state.currentTab == Tabs.PLAYLISTS || state.currentTab == Tabs.TRACKLIST) {
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
                            //unreachable
                        }
                    }
                }
            ) {
                Image(
                    painterResource(Res.drawable.add),
                    "Add button",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}