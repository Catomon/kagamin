package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import kotlinx.coroutines.flow.StateFlow

interface PlaylistsManager {

    enum class PlayMode { ONCE, RANDOM, PLAYLIST, REPEAT_PLAYLIST, REPEAT_TRACK }

    val playlists: StateFlow<List<Playlist>>
    val currentPlaylist: StateFlow<Playlist>
    val queueState: StateFlow<List<AudioTrack>>
    val currentTrack: StateFlow<AudioTrack?>
    val playMode: StateFlow<PlayMode>

    fun updateCurrentPlaylist(playlist: Playlist)

    fun updatePlaylists(playlists: List<Playlist>)
    fun updatePlaylist(playlist: Playlist)
    fun addPlaylist(playlist: Playlist)
    fun removePlaylist(playlist: Playlist)

    fun addToQueue(track: AudioTrack)
    fun removeFromQueue(track: AudioTrack)
    fun addToQueue(track: List<AudioTrack>)
    fun removeFromQueue(track: List<AudioTrack>)
    fun clearQueue()

    fun setPlayMode(mode: PlayMode)

    suspend fun nextTrack(): AudioTrack?
    suspend fun prevTrack(): AudioTrack?
}
