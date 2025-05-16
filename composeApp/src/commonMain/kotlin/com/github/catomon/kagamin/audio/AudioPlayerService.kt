package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.AudioTrack
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerService {

    enum class PlayState { PLAYING, PAUSED, IDLE }

    val playState: StateFlow<PlayState>
    val currentTrack: StateFlow<AudioTrack?>
    val volume: StateFlow<Float>
    val position: StateFlow<Long>
    val crossfade: StateFlow<Boolean>

    val playlistsManager: PlaylistsManager

    suspend fun play(track: AudioTrack): Result<Boolean>

    fun pause()

    fun resume()

    fun stop()

    suspend fun seek(position: Long)
    fun setVolume(volume: Float)
}

expect fun getAudioPlayerServiceImpl(): AudioPlayerService