package chu.monscout.kagamin.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.audio.DenpaTrack
import chu.monscout.kagamin.savePlaylist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Tracklist(state: KagaminViewModel, tracks: List<DenpaTrack>, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }
    val tracksIndex =
        remember(tracks) { tracks.mapIndexed { i, track -> (track.uri to i) }.toMap() }
    val currentTrack = state.currentTrack

    val listState = rememberLazyListState()

    Column(modifier) {
        if (currentTrack != null) {
            TrackItem(
                -1,
                state.currentTrack!!,
                tracklistManager,
                state = state,
                onClick = onClick@{
                    val curTrackIndex = tracksIndex[currentTrack.uri] ?: return@onClick
                    coroutineScope.launch {
                        listState.animateScrollToItem(curTrackIndex)
                    }
                }
            )
        } else {
            Box(modifier = Modifier.background(Colors.barsTransparent).height(32.dp).fillMaxWidth())
        }

        LazyColumn(Modifier.fillMaxWidth(), state = listState) {
            items(tracks.size, key = {
                tracks[it].uri
            }) { index ->
                val track = tracks[index]
                TrackItem(
                    index,
                    track,
                    tracklistManager,
                    state = state,
                    onClick = onClick@{
                        if (tracklistManager.isAnySelected) {
                            if (tracklistManager.isSelected(index, track))
                                tracklistManager.deselect(index, track)
                            else tracklistManager.select(index, track)
                            return@onClick
                        }
                        if (state.isLoadingSong != null) return@onClick
                        CoroutineScope(Dispatchers.Default).launch {
                            state.isLoadingSong = track
                            state.denpaPlayer.play(track)
                            state.isLoadingSong = null
                        }
                    },
                    modifier = Modifier
                )
            }
        }

        Box(Modifier.fillMaxSize().background(Colors.currentYukiTheme.listItemB))
    }
}

data class TracklistManager(
    val coroutineScope: CoroutineScope,
) {
    val selected: SnapshotStateMap<Int, DenpaTrack> = mutableStateMapOf()
    val isAnySelected get() = selected.isNotEmpty()

    fun select(index: Int, track: DenpaTrack) {
        selected[index] = track
    }

    fun isSelected(index: Int, track: DenpaTrack): Boolean {
        return selected.contains(index)
    }

    fun deselect(index: Int, track: DenpaTrack) {
        selected.remove(index)
    }

    fun deselectAll() {
        selected.clear()
    }

    fun contextMenuRemovePressed(
        state: KagaminViewModel,
        track: DenpaTrack
    ) {
        coroutineScope.launch {
            if (isAnySelected) {
                selected.values.forEach { track ->
                    state.isLoadingSong = track
                    state.denpaPlayer.removeFromPlaylist(track)
                    state.denpaPlayer.playlist.value =
                        state.denpaPlayer.playlist.value
                    savePlaylist(
                        state.currentPlaylistName,
                        state.denpaPlayer.playlist.value.toTypedArray()
                    )
                    //listState.scrollToItem(i, -60)
                    state.isLoadingSong = null
                }

                deselectAll()
            } else {
                state.isLoadingSong = track
                state.denpaPlayer.removeFromPlaylist(track)
                state.denpaPlayer.playlist.value =
                    state.denpaPlayer.playlist.value
                savePlaylist(
                    state.currentPlaylistName,
                    state.denpaPlayer.playlist.value.toTypedArray()
                )
                //listState.scrollToItem(i, -60)
                state.isLoadingSong = null
            }
        }
    }
}

@Composable
expect fun TrackItem(
    index: Int,
    track: DenpaTrack,
    tracklistManager: TracklistManager,
    state: KagaminViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)