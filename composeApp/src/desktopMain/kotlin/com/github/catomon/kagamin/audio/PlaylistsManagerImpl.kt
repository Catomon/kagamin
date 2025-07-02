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

    private val queue = ArrayDeque<AudioTrack>()
    private val _queueState = MutableStateFlow<List<AudioTrack>>(emptyList())
    override val queueState: StateFlow<List<AudioTrack>> = _queueState.asStateFlow()

    override val currentTrack: StateFlow<AudioTrack?> = player.currentTrack

    private val _playMode = MutableStateFlow(PlaylistsManager.PlayMode.PLAYLIST)
    override val playMode: StateFlow<PlaylistsManager.PlayMode> = _playMode.asStateFlow()

    private val mutex = Mutex()

    override fun changeCurrentPlaylist(playlist: Playlist) {
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

        if (_currentPlaylist.value.id == playlist.id)
            changeCurrentPlaylist(defaultPlaylist())
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
        if (currentTrack.value == track) {
            player.stop()
        }
    }

    override fun removeFromQueue(track: List<AudioTrack>) {
        queue.removeAll(track)
        updateQueueState()
        if (currentTrack.value != null && track.contains(currentTrack.value)) {
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
        val nextTrack = if (queueState.value.isEmpty()) {
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

        nextTrack ?: run {
            player.stop()
            return null
        }

        if (!nextTrack.uri.startsWith("http")) {
            if (filePlayTried >= currentPlaylist.value.tracks.size) {
                logWarn("Playlist has no files to play.")
                return null
            }
            filePlayTried++

            if (withContext(Dispatchers.IO) { !File(nextTrack.uri).exists() }) {
                logWarn("Track file does not exist: ${nextTrack.uri}")

                return nextTrack()
            }
        }

        filePlayTried = 0

        player.play(nextTrack)

        return nextTrack
    }

    override suspend fun prevTrack(): AudioTrack? = mutex.withLock {
        logMsg("Prev track.")

        val oldTrack = currentTrack.value
        val prevTrack = if (queue.isEmpty()) {
            if (currentPlaylist.value.tracks.isEmpty()) return null

            val oldIndex = currentPlaylist.value.tracks.indexOf(oldTrack)
            currentPlaylist.value.tracks.elementAtOrNull(
                if (oldIndex > 0 && oldIndex < currentPlaylist.value.tracks.size)
                    currentPlaylist.value.tracks.indexOf(oldTrack) - 1 else 0
            )
        } else oldTrack


        player.play(prevTrack ?: return null)

        return prevTrack
    }
}