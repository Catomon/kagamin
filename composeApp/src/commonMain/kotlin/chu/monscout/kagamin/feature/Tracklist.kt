package chu.monscout.kagamin.feature

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import chu.monscout.kagamin.audio.DenpaTrack
import kotlinx.coroutines.CoroutineScope

@Composable
fun Tracklist(state: KagaminViewModel, tracks: List<DenpaTrack>, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val tracksManager = remember { TracksManager() }

    LazyColumn(modifier, state = rememberLazyListState()) {
        items(tracks.size) { index ->
            val track = tracks[index]

            TrackItem(index, tracksManager, track, clipboard, coroutineScope, state)
        }
    }
}

data class TracksManager(
    val selected: SnapshotStateMap<Int, DenpaTrack> = mutableStateMapOf<Int, DenpaTrack>()
) {

    val isAnySelected get() = selected.isNotEmpty()

    fun select(index: Int, track: DenpaTrack) {
        selected[index] = track
    }

    fun deselect(track: DenpaTrack) {

    }

    fun deselectAll() {
        selected.clear()
    }

    fun removeSelected() {

    }

    fun remove(track: DenpaTrack) {

    }
}

@Composable
expect fun TrackItem(
    index: Int,
    tracksManager: TracksManager,
    track: DenpaTrack,
    clipboard: ClipboardManager,
    coroutineScope: CoroutineScope,
    state: KagaminViewModel,
)