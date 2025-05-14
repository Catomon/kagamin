package com.github.catomon.kagamin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.audio.createAudioPlayer
import com.github.catomon.kagamin.audio.createAudioTrack
import com.github.catomon.kagamin.data.PlaylistData
import com.github.catomon.kagamin.data.TrackData
import com.github.catomon.kagamin.loadPlaylist
import com.github.catomon.kagamin.loadPlaylists
import com.github.catomon.kagamin.loadSettings
import com.github.catomon.kagamin.savePlaylist
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.util.echoMsg
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class KagaminViewModel(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    ViewModel() {
    val audioPlayer = createAudioPlayer
    val playlist by audioPlayer.playlist
    val currentTrack by audioPlayer.currentTrack
    val playState by audioPlayer.playState
    val playMode by audioPlayer.playMode
//
//    var trackThumbnail by mutableStateOf<ImageBitmap?>(null)
//    var loadingThumbnail by mutableStateOf(false)

    var currentPlaylistName by mutableStateOf("default")

    var isLoadingPlaylistFile by mutableStateOf(false)
    var isLoadingSong by mutableStateOf<AudioTrack?>(null)

    //todo move to player screen state
    var currentTab by mutableStateOf(Tabs.TRACKLIST)

    var settings by mutableStateOf(loadSettings())

    var playlists: MutableStateFlow<Map<String, PlaylistData>> = MutableStateFlow(emptyMap())
        private set

    var lovedSongs = mutableStateMapOf<String, TrackData>()
        private set

    var createPlaylistWindow by mutableStateOf(false)

    init {
        val lastPlaylistName = settings.lastPlaylistName.ifBlank { "default" }
        currentPlaylistName = lastPlaylistName

        viewModelScope.launch {
            reloadPlaylists()
        }
    }

    suspend fun reloadPlaylists() = withContext(ioDispatcher) {
        playlists.value = loadPlaylists()

        val lovedPl = playlists.value["loved"] ?: return@withContext
        val lovedMap = lovedPl.tracks.associateBy { it.uri }

        withContext(Dispatchers.Main) {
            lovedSongs.clear()
            lovedSongs.putAll(lovedMap)
        }
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
//
//        loadingThumbnail = true
//
//        val currentTrack = currentTrack
//        val trackThumbnailUpdated = if (currentTrack != null) {
//            try {
//                withContext(ioDispatcher) {
//                    getThumbnail(currentTrack.uri)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        } else {
//            null
//        }
//
//        trackThumbnail = trackThumbnailUpdated
//
//        loadingThumbnail = false
    }

    fun reloadPlaylist() {
        isLoadingPlaylistFile = true
        try {
            val tracksData = loadPlaylist(currentPlaylistName)?.tracks
            if (tracksData != null) {
                audioPlayer.playlist.value = mutableListOf()
                tracksData.forEach {
                    audioPlayer.addToPlaylist(createAudioTrack(it))
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

    fun removePlaylist(playlistName: String) {
        if (playlistName == "default") return

        clearPlaylist(playlistName)
        com.github.catomon.kagamin.removePlaylist(playlistName)
        if (currentPlaylistName == playlistName) {
            currentPlaylistName = "default"
        }

        runBlocking {
            reloadPlaylists()
        }
    }

    fun clearPlaylist(playlistName: String) {
        savePlaylist(
            playlistName,
            arrayOf()
        )
        if (currentPlaylistName == playlistName)
            audioPlayer.playlist.value = mutableListOf()

        viewModelScope.launch {
            reloadPlaylists()
        }
    }

    fun shufflePlaylist(playlistName: String) {
        val playlist = playlists.value[playlistName] ?: return
        savePlaylist(
            playlistName,
            playlist.tracks.toList().shuffled()
        )

        runBlocking {
            reloadPlaylists()
            reloadPlaylist()
        }
    }
}