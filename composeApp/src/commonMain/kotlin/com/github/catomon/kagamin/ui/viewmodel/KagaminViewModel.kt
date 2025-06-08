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
import com.github.catomon.kagamin.data.SortType
import com.github.catomon.kagamin.data.loadSettings
import com.github.catomon.kagamin.data.saveSettings
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.util.echoErr
import com.github.catomon.kagamin.util.logErr
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class KagaminViewModel(
    private val audioPlayerService: AudioPlayerService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main,
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

    var isLoading by mutableStateOf<Boolean>(false)

    //todo move to player screen state
    var currentTab by mutableStateOf(Tabs.TRACKLIST)

    var settings by mutableStateOf(loadSettings())

    var lovedSongs = mutableStateMapOf<String, AudioTrack>()
        private set

    var createPlaylistWindow by mutableStateOf(false)

    init {
        viewModelScope.launch {
            isLoading = true

            reloadPlaylists()
            loadLastPlaylist()

            // set preferences
            when {
                settings.random -> playlistsManager.setPlayMode(PlaylistsManager.PlayMode.RANDOM)
                settings.repeat -> playlistsManager.setPlayMode(PlaylistsManager.PlayMode.REPEAT_TRACK)
                settings.repeatPlaylist -> playlistsManager.setPlayMode(PlaylistsManager.PlayMode.REPEAT_PLAYLIST)
            }
            audioPlayerService.setCrossfade(settings.crossfade)
            setVolume(settings.volume)

            isLoading = false
        }
    }

    private fun loadLastPlaylist() {
        val lastPlaylistName = settings.lastPlaylistName.ifBlank { "default" }
        playlistsManager.changeCurrentPlaylist(playlists.value.firstOrNull { it.name == lastPlaylistName }
            ?: return)
    }

    fun play(track: AudioTrack) {
        viewModelScope.launch {
            var loading = false
            if (playState.value == AudioPlayerService.PlayState.IDLE) {
                loading = true
                isLoading = true
            }

            audioPlayerService.play(track)

            if (loading)
                isLoading = false
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

    suspend fun reloadPlaylists() {
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

    fun changeCurrentPlaylist(playlist: Playlist) {
        playlistsManager.changeCurrentPlaylist(playlist)
        saveSettings(settings.copy(lastPlaylistName = playlist.name))
    }

    fun reloadPlaylist(playlist: Playlist) {
        viewModelScope.launch(ioDispatcher) {
            val loadedPlaylist = PlaylistsLoader.loadPlaylist(playlist)
            if (loadedPlaylist != null) {
                withContext(mainDispatcher) {
                    playlistsManager.updatePlaylist(loadedPlaylist)
                }
            } else {
                logErr { "Failed to reload playlist: ${playlist.name}" }
            }
        }
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch(ioDispatcher) {
            isLoading = true

            if (PlaylistsLoader.exists(playlist.name)) {
                logErr { "Playlist with such name already exist: ${playlist.name}" }
                return@launch
            }

            val playlist = if (playlist.isOnline) {
                val url = playlist.url
                if (url.isNotEmpty()) {
                    val tracks = audioPlayerService.loadTracks(listOf(url))
                    val tracksUris = tracks.map { it.uri }
                    playlist.copy(tracks = playlist.tracks.filter { it.uri !in tracksUris } + tracks)
                } else playlist
            } else playlist

            if (PlaylistsLoader.savePlaylist(playlist)) {
                withContext(mainDispatcher) {
                    playlistsManager.addPlaylist(playlist)
                }
            } else {
                logErr { "Failed to create playlist: ${playlist.name}" }
            }

        }.invokeOnCompletion {
            isLoading = false
        }
    }

    suspend fun loadTracks(uri: String): List<AudioTrack> {
        isLoading = true
        val tracks = audioPlayerService.loadTracks(listOf(uri))
        isLoading = false
        return tracks
    }

    suspend fun loadTracks(uris: List<String>): List<AudioTrack> {
        isLoading = true
        val tracks = audioPlayerService.loadTracks(uris)
        isLoading = false
        return tracks
    }

    fun removePlaylist(playlist: Playlist) {
//        if (playlist.id == "default") return

        viewModelScope.launch(ioDispatcher) {
            if (PlaylistsLoader.removePlaylist(playlist))
                withContext(mainDispatcher) {
                    playlistsManager.removePlaylist(playlist)
                }
            else
                echoErr { "Failed to remove playlist: ${playlist.name}" }
        }
    }

    fun loadRemoteTracksToPlaylist(
        link: String,
        currentPlaylist: Playlist
    ) {
        viewModelScope.launch {
            isLoading = true

            val loadedTracks = loadTracks(link)
            val loadedUris = loadedTracks.map { it.uri }
            updatePlaylist(currentPlaylist.copy(tracks = loadedTracks + currentPlaylist.tracks.filter { it.uri !in loadedUris }))

            isLoading = false
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        playlistsManager.updatePlaylist(playlist)

        viewModelScope.launch(ioDispatcher) {
            PlaylistsLoader.savePlaylist(playlist)
        }
    }

    fun renamePlaylist(playlist: Playlist, newName: String) {
        check(newName.isNotBlank())

        val renamedPlaylist = playlist.copy(name = newName)
        playlistsManager.updatePlaylist(renamedPlaylist)

        viewModelScope.launch(ioDispatcher) {
            PlaylistsLoader.removePlaylist(playlist)
            PlaylistsLoader.savePlaylist(renamedPlaylist)
        }
    }

    fun clearPlaylist(playlist: Playlist) {
        val emptyPlaylist = playlist.copy(tracks = emptyList())
        playlistsManager.updatePlaylist(emptyPlaylist)

        viewModelScope.launch(ioDispatcher) {
            PlaylistsLoader.savePlaylist(emptyPlaylist)
        }
    }

    fun shufflePlaylist(playlist: Playlist) {
        updatePlaylist(
            playlist.copy(
                tracks = playlist.tracks.shuffled(),
                sortType = SortType.ORDER
            )
        )
    }

    fun nextTrack() {
        viewModelScope.launch {
            var loading = false
            if (playState.value == AudioPlayerService.PlayState.IDLE) {
                loading = true
                isLoading = true
            }

            playlistsManager.nextTrack()

            if (loading)
                isLoading = false
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

    fun togglePlayMode() {
        val playMode = when (playMode.value) {
            PlaylistsManager.PlayMode.PLAYLIST -> PlaylistsManager.PlayMode.REPEAT_PLAYLIST
            PlaylistsManager.PlayMode.REPEAT_PLAYLIST -> PlaylistsManager.PlayMode.RANDOM
            PlaylistsManager.PlayMode.REPEAT_TRACK -> PlaylistsManager.PlayMode.PLAYLIST
            PlaylistsManager.PlayMode.RANDOM -> PlaylistsManager.PlayMode.REPEAT_TRACK
            PlaylistsManager.PlayMode.ONCE -> error("not planned")
        }
        playlistsManager.setPlayMode(playMode)
    }

    var isSorting by mutableStateOf(false)

    fun toggleSorting() {
        if (isSorting) return
        viewModelScope.launch {
            isSorting = true
            val sortEntries = SortType.entries
            val sort = (sortEntries.indexOf(currentPlaylist.value.sortType) + 1).let { nextIndex ->
                sortEntries[if (nextIndex < sortEntries.size) nextIndex else 0]
            }

            val updatedPlaylist = currentPlaylist.value.copy(
                sortType = sort,
                tracks =
                    withContext(Dispatchers.Default) {
                        currentPlaylist.value.tracks.sorted(sort)
                    }
            )

            updatePlaylist(updatedPlaylist)

            isSorting = false
        }
    }

    private suspend fun List<AudioTrack>.sorted(sortType: SortType): List<AudioTrack> {
        val tracks = this
        return when (sortType) {
            SortType.ORDER -> tracks
            SortType.TITLE -> tracks.sortedBy { it.title }
            SortType.ARTIST -> tracks.sortedBy { it.artist }
            SortType.DURATION -> tracks.sortedByDescending { it.duration }
            SortType.DATE_TIME -> {
                val trackTimestamps = withContext(Dispatchers.IO) {
                    tracks.map { track ->
                        async {
                            val lastModified = try {
                                File(track.uri).takeIf { it.exists() }?.lastModified() ?: 0L
                            } catch (e: SecurityException) {
                                0L
                            }
                            track to lastModified
                        }
                    }.awaitAll()
                }

                trackTimestamps
                    .sortedByDescending { it.second }
                    .map { it.first }
            }
        }
    }

    fun setCrossfade(enabled: Boolean) {
        audioPlayerService.setCrossfade(enabled)
    }

    fun saveSettings() {
        settings = settings.copy(
            repeat = playMode.value == PlaylistsManager.PlayMode.REPEAT_TRACK,
            volume = volume.value,
            random = playMode.value == PlaylistsManager.PlayMode.RANDOM,
            repeatPlaylist = playMode.value == PlaylistsManager.PlayMode.REPEAT_PLAYLIST
        )
        saveSettings(settings)
    }
}