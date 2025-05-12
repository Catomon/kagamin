package com.github.catomon.kagamin.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.savePlaylist
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class TracklistManager(
    private val coroutineScope: CoroutineScope,
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
expect fun TracklistHeader(
    currentTrack: AudioTrack,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    filterTracks: (String) -> Unit,
    modifier: Modifier = Modifier
)