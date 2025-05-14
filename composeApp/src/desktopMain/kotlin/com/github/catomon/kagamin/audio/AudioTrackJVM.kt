package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.TrackData
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import java.util.UUID

class AudioTrackJVM(
    override val uri: String,
    var overrideTitle: String = "",
    override val id: String = UUID.randomUUID().toString(),
    override val trackData: TrackData
) : com.github.catomon.kagamin.audio.AudioTrack {

    var audioTrack: AudioTrack? = null

    override val author: String
        get() = trackData.artist.ifBlank {
            audioTrack?.info?.author?.let { if (it == "Unknown artist") "" else it } ?: ""
        }
    override val title: String
        get() = trackData.title.ifBlank {
            overrideTitle.ifBlank {
                audioTrack?.trackName?.let { if (it == "Unknown title") "" else it } ?: ""
            }
        }
    override val duration: Long get() = trackData.duration.takeIf { it > 0 } ?: audioTrack?.duration ?: Long.MAX_VALUE

    constructor(audioTrack: AudioTrack) : this(
        audioTrack.info.uri,
        audioTrack.info.title,
        trackData = TrackData(audioTrack.info.uri, audioTrack.info.title, audioTrack.info.author)
    ) {
        this.audioTrack = audioTrack
    }

    constructor(trackData: TrackData) : this(
        uri = trackData.uri,
        overrideTitle = trackData.title,
        trackData = trackData
    )

    override fun equals(other: Any?): Boolean {
        return other is com.github.catomon.kagamin.audio.AudioTrack && uri == other.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }
}