package chu.monscout.kagamin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.createAudioPlayer
import chu.monscout.kagamin.createAudioTrack
import chu.monscout.kagamin.loadPlaylist
import chu.monscout.kagamin.loadSettings
import chu.monscout.kagamin.ui.util.Tabs

class KagaminViewModel : ViewModel() {
    val audioPlayer = createAudioPlayer
    val playlist by audioPlayer.playlist
    val currentTrack by audioPlayer.currentTrack
    val playState by audioPlayer.playState
    val playMode by audioPlayer.playMode
    var currentPlaylistName by mutableStateOf("default")
    var isLoadingPlaylistFile by mutableStateOf(false)
    var isLoadingSong by mutableStateOf<AudioTrack?>(null)
    var currentTab by mutableStateOf(Tabs.TRACKLIST)
    var videoUrl by mutableStateOf("")

    var settings by mutableStateOf(loadSettings())

    var height by mutableStateOf(0)
    var width by mutableStateOf(0)

    fun onPlayPause() {
        when (audioPlayer.playState.value) {
            AudioPlayer.PlayState.PLAYING -> audioPlayer.pause()
            AudioPlayer.PlayState.PAUSED -> audioPlayer.resume()
            AudioPlayer.PlayState.IDLE -> audioPlayer.resume()
        }
    }

    fun reloadPlaylist() {
        isLoadingPlaylistFile = true
        try {
            val trackUris = loadPlaylist(currentPlaylistName)?.tracks
            if (trackUris != null) {
                audioPlayer.playlist.value = mutableListOf()
                trackUris.forEach {
                    audioPlayer.addToPlaylist(createAudioTrack(it.uri, it.name))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadingPlaylistFile = false
        }
    }
}