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
import androidx.compose.runtime.LaunchedEffect
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
fun Tracklist(viewModel: KagaminViewModel, tracks: List<AudioTrack>, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }
    val index =
        remember(tracks) { tracks.mapIndexed { i, track -> (track.uri to i) }.toMap() }
    val currentTrack = viewModel.currentTrack

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[currentTrack?.uri] ?: 0)
    }

    Column(modifier) {
        if (currentTrack != null) {
            TrackItem(
                -1,
                viewModel.currentTrack!!,
                tracklistManager,
                viewModel = viewModel,
                onClick = onClick@{
                    val curTrackIndex = index[currentTrack.uri] ?: return@onClick
                    coroutineScope.launch {
                        listState.animateScrollToItem(curTrackIndex)
                    }
                }
            )
        } else {
            Box(modifier = Modifier.background(Colors.backgroundTransparent).height(32.dp).fillMaxWidth())
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
                    viewModel = viewModel,
                    onClick = onClick@{
                        if (tracklistManager.isAnySelected) {
                            if (tracklistManager.isSelected(index, track))
                                tracklistManager.deselect(index, track)
                            else tracklistManager.select(index, track)
                            return@onClick
                        }
                        if (viewModel.isLoadingSong != null) return@onClick

                        if (track.uri.startsWith("http")) {
                             viewModel.videoUrl = track.uri
                        } else {
                            viewModel.videoUrl = ""
                            viewModel.viewModelScope.launch {
                                viewModel.isLoadingSong = track
                                viewModel.audioPlayer.play(track)
                                viewModel.isLoadingSong = null
                            }
                        }
                    },
                    modifier = Modifier
                )
            }
        }

        Box(Modifier.fillMaxSize().background(Colors.theme.listItemB))
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
        viewModel: KagaminViewModel,
        track: AudioTrack
    ) {
        coroutineScope.launch {
            if (isAnySelected) {
                selected.values.forEach { track ->
                    viewModel.isLoadingSong = track
                    viewModel.audioPlayer.removeFromPlaylist(track)
                    viewModel.audioPlayer.playlist.value =
                        viewModel.audioPlayer.playlist.value
                    savePlaylist(
                        viewModel.currentPlaylistName,
                        viewModel.audioPlayer.playlist.value.toTypedArray()
                    )
                    //listState.scrollToItem(i, -60)
                    viewModel.isLoadingSong = null
                }

                deselectAll()
            } else {
                viewModel.isLoadingSong = track
                viewModel.audioPlayer.removeFromPlaylist(track)
                viewModel.audioPlayer.playlist.value =
                    viewModel.audioPlayer.playlist.value
                savePlaylist(
                    viewModel.currentPlaylistName,
                    viewModel.audioPlayer.playlist.value.toTypedArray()
                )
                //listState.scrollToItem(i, -60)
                viewModel.isLoadingSong = null
            }
        }
    }
}

@Composable
expect fun TrackItem(
    index: Int,
    track: AudioTrack,
    tracklistManager: TracklistManager,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)