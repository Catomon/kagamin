package com.github.catomon.kagamin.audio

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlaylistsManagerImpl(
    override val currentTrack: StateFlow<AudioTrack?>,
    private val exoPlayer: ExoPlayer
) : PlaylistsManager {
    private val mutex = Mutex()

    private fun defaultPlaylist(): Playlist =
        _playlists.value.firstOrNull { it.id == "default" } ?: run {
            val pl = Playlist(
                "default",
                "default",
                emptyList()
            )
            addPlaylist(pl)
            pl
        }

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    override val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<Playlist>(defaultPlaylist())
    override val currentPlaylist: StateFlow<Playlist> = _currentPlaylist.asStateFlow()

    private val _queueState = MutableStateFlow<List<AudioTrack>>(emptyList())
    override val queueState: StateFlow<List<AudioTrack>> = _queueState.asStateFlow()


    private val _playMode = MutableStateFlow(PlaylistsManager.PlayMode.PLAYLIST)
    override val playMode: StateFlow<PlaylistsManager.PlayMode> = _playMode.asStateFlow()

    override fun changeCurrentPlaylist(playlist: Playlist) {
        _currentPlaylist.value = playlist
        _queueState.value = playlist.tracks
        updateExoPlayerQueue(playlist.tracks)
    }

    override fun updatePlaylists(playlists: List<Playlist>) {
        _playlists.value = playlists
    }

    override fun updatePlaylist(playlist: Playlist) {
        _playlists.value = _playlists.value.map { if (it.id == playlist.id) playlist else it }
        if (_currentPlaylist.value.id == playlist.id) {
            changeCurrentPlaylist(playlist)
        }
    }

    override fun addPlaylist(playlist: Playlist) {
        _playlists.value = _playlists.value.filter { it.id != playlist.id } + playlist
    }

    override fun removePlaylist(playlist: Playlist) {
        _playlists.value = _playlists.value.filter { it.id != playlist.id }

        if (_currentPlaylist.value.id == playlist.id)
            changeCurrentPlaylist(defaultPlaylist())
    }

    override fun addToQueue(track: AudioTrack) {
        _queueState.value = _queueState.value + track
        exoPlayer.addMediaItem(MediaItem.fromUri(track.uri))
    }

    override fun removeFromQueue(track: AudioTrack) {
        val index = _queueState.value.indexOfFirst { it.uri == track.uri }
        if (index >= 0) {
            _queueState.value = _queueState.value.filterIndexed { i, _ -> i != index }
            exoPlayer.removeMediaItem(index)
        }
    }

    override fun addToQueue(tracks: List<AudioTrack>) {
        _queueState.value = _queueState.value + tracks
        tracks.forEach { exoPlayer.addMediaItem(MediaItem.fromUri(it.uri)) }
    }

    override fun removeFromQueue(tracks: List<AudioTrack>) {
        val urisToRemove = tracks.map { it.uri }.toSet()
        val newQueue = _queueState.value.filter { it.uri !in urisToRemove }
        _queueState.value = newQueue
        updateExoPlayerQueue(newQueue)
    }

    override fun clearQueue() {
        _queueState.value = emptyList()
        exoPlayer.clearMediaItems()
    }

    override fun setPlayMode(mode: PlaylistsManager.PlayMode) {
        _playMode.value = mode
        exoPlayer.repeatMode = when (mode) {
            PlaylistsManager.PlayMode.REPEAT_TRACK -> Player.REPEAT_MODE_ONE
            PlaylistsManager.PlayMode.REPEAT_PLAYLIST -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer.shuffleModeEnabled = (mode == PlaylistsManager.PlayMode.RANDOM)
    }

    override suspend fun nextTrack(): AudioTrack? = mutex.withLock {
        exoPlayer.seekToNextMediaItem()
        _queueState.value.getOrNull(exoPlayer.currentMediaItemIndex)
    }

    override suspend fun prevTrack(): AudioTrack? = mutex.withLock {
        exoPlayer.seekToPreviousMediaItem()
        _queueState.value.getOrNull(exoPlayer.currentMediaItemIndex)
    }

    private fun updateExoPlayerQueue(tracks: List<AudioTrack>) {
        exoPlayer.clearMediaItems()
        tracks.forEach { exoPlayer.addMediaItem(MediaItem.fromUri(it.uri)) }
        exoPlayer.prepare()
    }
}
