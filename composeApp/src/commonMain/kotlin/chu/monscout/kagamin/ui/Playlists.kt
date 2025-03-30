package chu.monscout.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import chu.monscout.kagamin.PlaylistData
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.loadPlaylists
import chu.monscout.kagamin.removePlaylist
import chu.monscout.kagamin.savePlaylist

@Composable
fun Playlists(state: KagaminViewModel, modifier: Modifier = Modifier) {
    var playlists by remember { mutableStateOf(loadPlaylists()) }

    if (playlists.isEmpty()) {
        Box(
            modifier
                .background(Colors.currentYukiTheme.listItemB),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No playlists.",
                textAlign = TextAlign.Center,
                color = Colors.text2
            )
        }
    } else {
        Column(modifier) {
            Box(modifier = Modifier.background(Colors.barsTransparent).height(32.dp).fillMaxWidth().padding(horizontal = 4.dp), contentAlignment = Alignment.CenterStart) {
                Text(state.currentPlaylistName, fontSize = 12.sp)
            }

            LazyColumn(
                state = rememberLazyListState(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(playlists.size, key = {
                    playlists[it]
                }) { i ->
                    val playlist = playlists[i]
                    PlaylistItem(
                        playlist,
                        state,
                        playlists,
                        i,
                        remove = {
                            removePlaylist(playlist.first)
                            if (state.currentPlaylistName == playlist.first)
                                state.currentPlaylistName = "default"
                            playlists = loadPlaylists()
                        },
                        clear = {
                            savePlaylist(
                                playlist.first,
                                arrayOf()
                            )

                            if (state.currentPlaylistName == playlist.first)
                                state.denpaPlayer.playlist.value = mutableListOf()

                            playlists = loadPlaylists()
                        }
                    )
                }
            }

            Box(Modifier.fillMaxSize().background(Colors.currentYukiTheme.listItemB))
        }
    }
}

@Composable
expect fun PlaylistItem(
    playlist: Pair<String, PlaylistData>,
    state: KagaminViewModel,
    playlists: List<Pair<String, PlaylistData>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit
)