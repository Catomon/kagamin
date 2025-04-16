package chu.monscout.kagamin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import chu.monscout.kagamin.data.PlaylistData
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.loadPlaylists
import chu.monscout.kagamin.removePlaylist
import chu.monscout.kagamin.savePlaylist
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.coroutines.launch

@Composable
fun Playlists(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    var playlists by remember { mutableStateOf(loadPlaylists()) }
    val index =
        remember(playlists) { playlists.mapIndexed { i, pl -> (pl.first to i) }.toMap() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[viewModel.currentPlaylistName] ?: 0)
    }

    if (playlists.isEmpty()) {
        Box(
            modifier
                .background(Colors.theme.listItemB),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No playlists.",
                textAlign = TextAlign.Center,
                color = Colors.textSecondary
            )
        }
    } else {
        Column(modifier) {
            Box(
                modifier = Modifier.background(Colors.backgroundTransparent).height(32.dp)
                    .fillMaxWidth()
                    .clickable {
                        val curTrackIndex =
                            index[viewModel.currentPlaylistName] ?: return@clickable
                        coroutineScope.launch {
                            listState.animateScrollToItem(curTrackIndex)
                        }
                    }.padding(horizontal = 4.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    viewModel.currentPlaylistName,
                    fontSize = 10.sp,
                    color = Colors.theme.buttonIcon
                )
            }

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(playlists.size, key = {
                    playlists[it]
                }) { i ->
                    val playlist = playlists[i]
                    PlaylistItem(
                        playlist,
                        viewModel,
                        playlists,
                        i,
                        remove = {
                            removePlaylist(playlist.first)
                            if (viewModel.currentPlaylistName == playlist.first)
                                viewModel.currentPlaylistName = "default"
                            playlists = loadPlaylists()
                        },
                        clear = {
                            savePlaylist(
                                playlist.first,
                                arrayOf()
                            )

                            if (viewModel.currentPlaylistName == playlist.first)
                                viewModel.audioPlayer.playlist.value = mutableListOf()

                            playlists = loadPlaylists()
                        },
                        shuffle = {
                            savePlaylist(playlist.first, playlist.second.tracks.toList().shuffled())

                            playlists = loadPlaylists()

                            viewModel.reloadPlaylist()
                        }
                    )
                }
            }

            Box(Modifier.fillMaxSize().background(Colors.theme.listItemB))
        }
    }
}

@Composable
expect fun PlaylistItem(
    playlist: Pair<String, PlaylistData>,
    viewModel: KagaminViewModel,
    playlists: List<Pair<String, PlaylistData>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit,
    shuffle: () -> Unit
)