package com.github.catomon.kagamin.audio

import com.github.catomon.kagamin.data.TrackData
import kotlin.random.Random

interface AudioTrack {
    val uri: String
    val id: String
    val author: String
    val title: String
    val duration: Long get() = Long.MAX_VALUE
    val trackData: TrackData
}

val emptyAudioTrack
    get() = object : AudioTrack {
        override val uri: String = "uri"
        override val id: String = "id"
        override val author: String = "Author${Random.nextInt(0, 11)}"
        override val title: String = "Song Name"
        override val trackData: TrackData = TrackData(uri, title, author)
    }