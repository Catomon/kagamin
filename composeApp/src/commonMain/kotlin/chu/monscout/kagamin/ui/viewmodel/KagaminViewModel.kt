package chu.monscout.kagamin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.audio.createAudioPlayer
import chu.monscout.kagamin.audio.createAudioTrack
import chu.monscout.kagamin.loadPlaylist
import chu.monscout.kagamin.loadSettings
import chu.monscout.kagamin.ui.components.getThumbnail
import chu.monscout.kagamin.ui.util.Tabs
import chu.monscout.kagamin.util.echoMsg
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KagaminViewModel(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    val audioPlayer = createAudioPlayer
    val playlist by audioPlayer.playlist
    val currentTrack by audioPlayer.currentTrack
    val playState by audioPlayer.playState
    val playMode by audioPlayer.playMode

    var trackThumbnail by mutableStateOf<ImageBitmap?>(null)
    var loadingThumbnail by mutableStateOf(false)

    var currentPlaylistName by mutableStateOf("default")

    var isLoadingPlaylistFile by mutableStateOf(false)
    var isLoadingSong by mutableStateOf<AudioTrack?>(null)
    var currentTab by mutableStateOf(Tabs.TRACKLIST)

    var settings by mutableStateOf(loadSettings())

    init {
        val lastPlaylistName = settings.lastPlaylistName.ifBlank { "default" }
        currentPlaylistName = lastPlaylistName
    }

    fun onPlayPause() {
        when (audioPlayer.playState.value) {
            AudioPlayer.PlayState.PLAYING -> audioPlayer.pause()
            AudioPlayer.PlayState.PAUSED -> audioPlayer.resume()
            AudioPlayer.PlayState.IDLE -> audioPlayer.resume()
        }
    }

    fun playTrack(track: AudioTrack) {
        TODO()
    }

    suspend fun updateThumbnail() {
        echoMsg("Updating thumbnail.")

        loadingThumbnail = true

        val currentTrack = currentTrack
        val trackThumbnailT = if (currentTrack != null) {
            try {
                withContext(ioDispatcher) {
                    getThumbnail(currentTrack)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }

        trackThumbnail = trackThumbnailT

        loadingThumbnail = false
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
            } else {
                currentPlaylistName = "default"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadingPlaylistFile = false
        }
    }
}