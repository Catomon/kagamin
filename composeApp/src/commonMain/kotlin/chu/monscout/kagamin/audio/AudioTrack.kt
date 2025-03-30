package chu.monscout.kagamin.audio

import kotlin.random.Random

interface AudioTrack {
    val uri: String
    val id: String
    val author: String
    val name: String
    val duration: Long get() = Long.MAX_VALUE
}

val emptyAudioTrack
    get() = object : AudioTrack {
        override val uri: String = "uri"
        override val id: String = "id"
        override val author: String = "Author${Random.nextInt(0, 11)}"
        override val name: String = "Song Name"
    }