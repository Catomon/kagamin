package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val tracks: List<AudioTrack>,
    val isOnline: Boolean = false,
    val url: String = ""
)