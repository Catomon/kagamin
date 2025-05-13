package com.github.catomon.kagamin.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.UUID

class AudioTrackJVM(
    override val uri: String,
    var overrideName: String = "",
    override val id: String = UUID.randomUUID().toString(),
) : com.github.catomon.kagamin.audio.AudioTrack {

    var audioTrack: AudioTrack? = null

    override val author: String get() = audioTrack?.info?.author?.let { if (it == "Unknown artist") "" else it } ?: ""
    override val name: String get() = overrideName.ifBlank { audioTrack?.trackName?.let { if (it == "Unknown title") "" else it } ?: "" }
    override val duration: Long get() = audioTrack?.duration ?: Long.MAX_VALUE

    constructor(audioTrack: AudioTrack) : this(
        audioTrack.info.uri,
        audioTrack.info.title,
    ) {
        this.audioTrack = audioTrack
    }

    override fun equals(other: Any?): Boolean {
        return other is com.github.catomon.kagamin.audio.AudioTrack && uri == other.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}