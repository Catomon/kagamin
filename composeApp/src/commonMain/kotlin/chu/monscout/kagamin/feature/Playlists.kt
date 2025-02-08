package chu.monscout.kagamin.feature

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.yukinotes.feature.Colors
import loadPlaylists
import removePlaylist
import savePlaylist

@Composable
fun Playlists(state: KagaminViewModel, modifier: Modifier = Modifier) {
    var playlists by remember { mutableStateOf(loadPlaylists()) }
    LazyColumn(
        modifier,
        state = rememberLazyListState(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(playlists.size) { i ->
            val playlist = playlists[i]
            ContextMenuArea(items = {
                listOf(
                    ContextMenuItem("Clear") {
                        savePlaylist(
                            playlist.first,
                            arrayOf()
                        )

                        if (state.currentPlaylistName == playlist.first)
                            state.denpaPlayer.playlist.value = mutableListOf()

                        playlists = loadPlaylists()
                    },
                    ContextMenuItem("Remove") {
                        removePlaylist(playlist.first)
                        if (state.currentPlaylistName == playlist.first)
                            state.currentPlaylistName = "default"
                        playlists = loadPlaylists()
                    },
                )
            }) {
                Column(
                    Modifier.background(color = if (i % 2 == 0) Colors.dividers else Colors.background)
                        .clickable {
                            state.currentPlaylistName = playlist.first
                            state.currentTab = Tabs.TRACKLIST
                        }.let {
                            if (state.currentPlaylistName == playlist.first) it.border(
                                2.dp,
                                Colors.bars
                            ) else it
                        }.padding(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            playlist.first, fontSize = 12.sp, color = Color.White,
                            maxLines = 1
                        )

//                    if (Random.nextInt(0, 4) == 1)
//                        Image(painterResource(Res.drawable.yt_ic), "")
                    }

                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            "Tracks: ${playlist.second.tracks.size}",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 10.sp,
                            color = Colors.noteText
                        )
                        Text(
                            "Duration: ???",
                            modifier = Modifier.weight(0.5f),
                            fontSize = 10.sp,
                            color = Colors.noteText
                        )
                    }
                }
            }
        }
    }
}