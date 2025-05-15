package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.util.logMsg
import com.github.catomon.kagamin.util.logWarn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class PlaylistsManagerImpl(
    private val player: AudioPlayerService
) : PlaylistsManager {

    private val defaultPlaylist = Playlist("default", "default", emptyList())

    private val _playlists = MutableStateFlow<List<Playlist>>(listOf(defaultPlaylist))
    override val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _currentPlaylist = MutableStateFlow<Playlist>(defaultPlaylist)
    override val currentPlaylist: StateFlow<Playlist> = _currentPlaylist.asStateFlow()

    private val queue = ArrayDeque<AudioTrack>()
    private val _queueState = MutableStateFlow<List<AudioTrack>>(emptyList())
    override val queueState: StateFlow<List<AudioTrack>> = _queueState

    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    override val currentTrack: StateFlow<AudioTrack?> = _currentTrack.asStateFlow()

    private val _playMode = MutableStateFlow(PlaylistsManager.PlayMode.ONCE)
    override val playMode: StateFlow<PlaylistsManager.PlayMode> = _playMode.asStateFlow()

    private val mutex = Mutex()

    override fun updateCurrentPlaylist(playlist: Playlist) {
        _currentPlaylist.value = playlist
    }

    override fun updatePlaylists(playlists: List<Playlist>) {
        _playlists.value = playlists
    }

    override fun updatePlaylist(playlist: Playlist) {
        if (_playlists.value.any { it.id == playlist.id })
            addPlaylist(playlist)

        if (_currentPlaylist.value.id == playlist.id)
            _currentPlaylist.value = playlist
    }

    override fun addPlaylist(playlist: Playlist) {
        _playlists.value = _playlists.value.filter { it.id != playlist.id } + playlist
    }

    override fun removePlaylist(playlist: Playlist) {
        _playlists.value = _playlists.value.filter { it.id != playlist.id }
    }

    private fun updateQueueState() {
        _queueState.value = queue.toList()
    }

    override fun addToQueue(track: AudioTrack) {
        queue += track
        updateQueueState()
    }

    override fun addToQueue(track: List<AudioTrack>) {
        queue += track
        updateQueueState()
    }

    override fun removeFromQueue(track: AudioTrack) {
        queue -= track
        updateQueueState()
        if (_currentTrack.value == track) {
            _currentTrack.value = null
            player.stop()
        }
    }

    override fun removeFromQueue(track: List<AudioTrack>) {
        queue.removeAll(track)
        updateQueueState()
        if (_currentTrack.value != null && track.contains(_currentTrack.value)) {
            _currentTrack.value = null
            player.stop()
        }
    }

    override fun clearQueue() {
        queue.clear()
        updateQueueState()
    }

    override fun setPlayMode(mode: PlaylistsManager.PlayMode) {
        _playMode.value = mode
    }

    private var filePlayTried = 0

    override suspend fun nextTrack(): AudioTrack? = mutex.withLock {
        logMsg("Next track.")

        val oldTrack = currentTrack.value
        val track = if (queueState.value.isEmpty()) {
            if (currentPlaylist.value.tracks.isEmpty()) return null

            when (playMode.value) {
                PlaylistsManager.PlayMode.RANDOM -> currentPlaylist.value.tracks.random()
                PlaylistsManager.PlayMode.REPEAT_PLAYLIST -> {
                    val oldIndex = currentPlaylist.value.tracks.indexOf(oldTrack)
                    currentPlaylist.value.tracks.elementAtOrNull(
                        if (oldIndex < currentPlaylist.value.tracks.size - 1)
                            currentPlaylist.value.tracks.indexOf(oldTrack) + 1 else 0
                    )
                }

                PlaylistsManager.PlayMode.PLAYLIST, PlaylistsManager.PlayMode.ONCE ->
                    currentPlaylist.value.tracks.elementAtOrNull(
                        currentPlaylist.value.tracks.indexOf(
                            oldTrack
                        ) + 1
                    )

                PlaylistsManager.PlayMode.REPEAT_TRACK -> oldTrack
            }
        } else {
            queue.removeFirstOrNull().also {
                updateQueueState()
            }
        }

        _currentTrack.value = track

        val nextAudioTrack = track

        nextAudioTrack ?: run {
            player.stop()
            return null
        }

        if (!nextAudioTrack.uri.startsWith("http")) {
            if (filePlayTried >= currentPlaylist.value.tracks.size) {
                logWarn("Playlist has no files to play.")
                return null
            }
            filePlayTried++

            if (withContext(Dispatchers.IO) { !File(nextAudioTrack.uri).exists() }) {
                logWarn("Track file does not exist: ${nextAudioTrack.uri}")

                return nextTrack()
            }
        }

        filePlayTried = 0

        player.play(nextAudioTrack)

        return nextAudioTrack
    }

    override suspend fun prevTrack(): AudioTrack? = mutex.withLock {
        logMsg("Prev track.")

        val oldTrack = currentTrack.value
        val track = if (queue.isEmpty()) {
            if (currentPlaylist.value.tracks.isEmpty()) return null

            val oldIndex = currentPlaylist.value.tracks.indexOf(oldTrack)
            currentPlaylist.value.tracks.elementAtOrNull(
                if (oldIndex > 0 && oldIndex < currentPlaylist.value.tracks.size)
                    currentPlaylist.value.tracks.indexOf(oldTrack) - 1 else 0
            )
        } else oldTrack

        _currentTrack.value = track

        val nextAudioTrack = track

//        player.stop()
        player.play(nextAudioTrack ?: return null)

        return nextAudioTrack
    }
}