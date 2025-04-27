package com.github.catomon.kagamin.audio

import androidx.compose.runtime.MutableState
import java.util.LinkedList

interface AudioPlayer<T : AudioTrack> {

    enum class PlayMode {
        ONCE,
        RANDOM,
        PLAYLIST,
        REPEAT_PLAYLIST,
        REPEAT_TRACK
    }

    enum class PlayState {
        PLAYING,
        PAUSED,
        IDLE
    }

    val playState: MutableState<PlayState>
    val playMode: MutableState<PlayMode>
    val queue: MutableState<LinkedList<T>>
    val playlist: MutableState<MutableList<T>>
    val currentTrack: MutableState<T?>
    val volume: MutableState<Float>
    val position: Long get() = -1L
    val crossfade: MutableState<Boolean>

    fun create()

    fun load(uris: List<String>)

    fun play(track: T): Boolean

    fun queue(track: T)

    fun freeQueue()

    fun addToPlaylist(track: T)

    fun removeFromPlaylist(track: T)

    fun prevTrack(): T?

    fun nextTrack(): T?

    fun pause()

    fun resume()

    fun stop()

    fun setVolume(volume: Float) {
        this.volume.value = volume
    }

    fun seek(position: Long)

    fun shutdown()
}