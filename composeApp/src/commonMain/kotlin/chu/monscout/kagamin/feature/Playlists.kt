package chu.monscout.kagamin.feature

import chu.monscout.kagamin.PlaylistData
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chu.monscout.kagamin.loadPlaylists
import chu.monscout.kagamin.removePlaylist
import chu.monscout.kagamin.savePlaylist

@Composable
fun Playlists(state: KagaminViewModel, modifier: Modifier = Modifier) {
    var playlists by remember { mutableStateOf(loadPlaylists()) }
    LazyColumn(
        modifier,
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