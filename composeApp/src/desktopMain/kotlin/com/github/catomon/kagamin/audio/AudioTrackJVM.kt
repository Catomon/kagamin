package com.github.catomon.kagamin.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.UUID

class AudioTrackJVM(
    override val uri: String,
    override val id: String = UUID.randomUUID().toString(),
    override var author: String = "",
    override var name: String = "",
    override var duration: Long = Long.MAX_VALUE
) : com.github.catomon.kagamin.audio.AudioTrack {

    var audioTrack: AudioTrack? = null
        set(value) {
            field = value
            author = audioTrack?.info?.author ?: ""
            name = audioTrack?.trackName ?: ""
            duration = audioTrack?.duration ?: Long.MAX_VALUE
        }

    constructor(audioTrack: AudioTrack) : this(
        audioTrack.info.uri,
        audioTrack.info.identifier,
        audioTrack.info.author,
        audioTrack.trackName
    )

    override fun equals(other: Any?): Boolean {
        return other is com.github.catomon.kagamin.audio.AudioTrack && uri == other.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}