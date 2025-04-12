package chu.monscout.kagamin.ui

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
import androidx.lifecycle.viewModelScope
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.savePlaylist
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun Tracklist(state: KagaminViewModel, tracks: List<AudioTrack>, modifier: Modifier = Modifier) {
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

                        if (track.uri.startsWith("http")) {
                             state.videoUrl = track.uri
                        } else {
                            state.videoUrl = ""
                            state.viewModelScope.launch {
                                state.isLoadingSong = track
                                state.audioPlayer.play(track) //todo state.playSong()
                                state.isLoadingSong = null
                            }
                        }
                    },
                    modifier = Modifier
                )
            }
        }

        Box(Modifier.fillMaxSize().background(Colors.currentYukiTheme.listItemB))
    }
}

class TracklistManager(
    val coroutineScope: CoroutineScope,
) {
    val selected: SnapshotStateMap<Int, AudioTrack> = mutableStateMapOf()
    val isAnySelected get() = selected.isNotEmpty()

    fun select(index: Int, track: AudioTrack) {
        selected[index] = track
    }

    fun isSelected(index: Int, track: AudioTrack): Boolean {
        return selected.contains(index)
    }

    fun deselect(index: Int, track: AudioTrack) {
        selected.remove(index)
    }

    fun deselectAll() {
        selected.clear()
    }

    fun deleteFile(track: AudioTrack): Boolean {
        print("Deleting file ${track.uri}.. ")
        try {
            if (File(track.uri).delete()) {
                println("ok.")
                return true
            }
            else {
                println("fail.")
                return false
            }

        } catch (e: Exception) {
            println("fail.")
            e.printStackTrace()
        }

        return false
    }

    fun deleteSelectedFiles() {
        selected.values.forEach { track ->
            deleteFile(track)
        }
    }

    fun contextMenuRemovePressed(
        state: KagaminViewModel,
        track: AudioTrack
    ) {
        coroutineScope.launch {
            if (isAnySelected) {
                selected.values.forEach { track ->
                    state.isLoadingSong = track
                    state.audioPlayer.removeFromPlaylist(track)
                    state.audioPlayer.playlist.value =
                        state.audioPlayer.playlist.value
                    savePlaylist(
                        state.currentPlaylistName,
                        state.audioPlayer.playlist.value.toTypedArray()
                    )
                    //listState.scrollToItem(i, -60)
                    state.isLoadingSong = null
                }

                deselectAll()
            } else {
                state.isLoadingSong = track
                state.audioPlayer.removeFromPlaylist(track)
                state.audioPlayer.playlist.value =
                    state.audioPlayer.playlist.value
                savePlaylist(
                    state.currentPlaylistName,
                    state.audioPlayer.playlist.value.toTypedArray()
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
    track: AudioTrack,
    tracklistManager: TracklistManager,
    state: KagaminViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)