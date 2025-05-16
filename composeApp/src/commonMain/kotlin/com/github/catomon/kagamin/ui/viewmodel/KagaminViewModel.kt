package com.github.catomon.kagamin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.PlaylistsLoader
import com.github.catomon.kagamin.data.loadSettings
import com.github.catomon.kagamin.data.saveSettings
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.util.echoWarn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

class KagaminViewModel(
    private val audioPlayerService: AudioPlayerService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val playState: StateFlow<AudioPlayerService.PlayState> = audioPlayerService.playState
    val currentTrack: StateFlow<AudioTrack?> = audioPlayerService.currentTrack
    val position: StateFlow<Long> = audioPlayerService.position
    val volume: StateFlow<Float> = audioPlayerService.volume

    private val playlistsManager: PlaylistsManager = audioPlayerService.playlistsManager

    val playlists: StateFlow<List<Playlist>> = playlistsManager.playlists
    val currentPlaylist: StateFlow<Playlist> = playlistsManager.currentPlaylist
    val queue: StateFlow<List<AudioTrack>> = playlistsManager.queueState

    //    val currentTrack: StateFlow<AudioTrack?> = playlistsManager.currentTrack
    val playMode: StateFlow<PlaylistsManager.PlayMode> = playlistsManager.playMode

    var isLoadingPlaylistFile by mutableStateOf(false)
    var isLoadingSong by mutableStateOf<AudioTrack?>(null)

    //todo move to player screen state
    var currentTab by mutableStateOf(Tabs.TRACKLIST)

    var settings by mutableStateOf(loadSettings())

    var lovedSongs = mutableStateMapOf<String, AudioTrack>()
        private set

    var createPlaylistWindow by mutableStateOf(false)

    init {
        viewModelScope.launch {
            reloadPlaylists()
            loadLastPlaylist()
        }
    }

    private fun loadLastPlaylist() {
        val lastPlaylistName = settings.lastPlaylistName.ifBlank { "default" }
        playlistsManager.updateCurrentPlaylist(playlists.value.firstOrNull { it.name == lastPlaylistName }
            ?: return)
    }

    fun play(track: AudioTrack) {
        viewModelScope.launch {
            audioPlayerService.play(track)
        }
    }

    fun pause() {
        audioPlayerService.pause()
    }

    fun resume() {
        audioPlayerService.resume()
    }

    fun stop() {
        audioPlayerService.stop()
    }

    fun seek(position: Long) {
        viewModelScope.launch {
            audioPlayerService.seek(position)
        }
    }

    fun setVolume(volume: Float) {
        audioPlayerService.setVolume(volume)
    }

    override fun onCleared() {
        super.onCleared()
    }

    suspend fun reloadPlaylists() = withContext(ioDispatcher) {
        val loadedPlaylists = PlaylistsLoader.loadPlaylists()
        if (loadedPlaylists.isNotEmpty())
            playlistsManager.updatePlaylists(loadedPlaylists)
        else
            PlaylistsLoader.savePlaylist(currentPlaylist.value) //if no playlists saved on the disk, save default playlist
    }

    fun onPlayPause() {
        when (playState.value) {
            AudioPlayerService.PlayState.PLAYING -> audioPlayerService.pause()
            AudioPlayerService.PlayState.PAUSED -> audioPlayerService.resume()
            AudioPlayerService.PlayState.IDLE -> nextTrack()
        }
    }

    /** Sets [currentPlaylist] to [playlist] **/
    fun updateCurrentPlaylist(playlist: Playlist) {
        playlistsManager.updateCurrentPlaylist(playlist)

        saveSettings(settings.copy(lastPlaylistName = playlist.name))
    }

    fun reloadPlaylist(playlist: Playlist) {
        val loadedPlaylist = PlaylistsLoader.loadPlaylist(playlist) ?: kotlin.run {
            echoWarn("Failed to load a playlist: ${playlist.name}")
            return
        }

        playlistsManager.updatePlaylist(loadedPlaylist)
    }

    fun createPlaylist(playlist: Playlist) {
        playlistsManager.addPlaylist(playlist)
        PlaylistsLoader.savePlaylist(playlist)
    }

    fun removePlaylist(playlist: Playlist) {
        if (playlist.id == "default") return

        playlistsManager.removePlaylist(playlist)
    }

    fun updatePlaylist(playlist: Playlist) {
        playlistsManager.updatePlaylist(playlist)
    }

    fun clearPlaylist(playlist: Playlist) {
        playlistsManager.updatePlaylist(playlist.copy(tracks = emptyList()))
    }

    fun shufflePlaylist(playlist: Playlist) {
        playlists.value.firstOrNull { it == playlist }?.let {
            playlistsManager.updatePlaylist(
                playlist.copy(
                    tracks = playlist.tracks.shuffled()
                )
            )
        }
    }

    fun nextTrack() {
        viewModelScope.launch {
            playlistsManager.nextTrack()
        }
    }

    fun prevTrack() {
        viewModelScope.launch {
            playlistsManager.prevTrack()
        }
    }

    fun setPlayMode(playMode: PlaylistsManager.PlayMode) {
        playlistsManager.setPlayMode(playMode)
    }

    fun exitApp() {
        settings = settings.copy(
            repeat = playMode.value == PlaylistsManager.PlayMode.REPEAT_TRACK,
            volume = volume.value,
            random = playMode.value == PlaylistsManager.PlayMode.RANDOM,
        )
        saveSettings(settings)
        exitProcess(0)
    }
}