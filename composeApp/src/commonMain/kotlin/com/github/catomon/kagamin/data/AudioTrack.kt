package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class AudioTrack(
    val id: String,
    val uri: String,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 0,
    val artworkUri: String? = null
)