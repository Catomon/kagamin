package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val tracks: List<AudioTrack>,
    val sortType: SortType = SortType.ORDER,
    val isOnline: Boolean = false,
    val url: String = ""
)

enum class SortType {
    ORDER,
    TITLE,
    ARTIST,
    DURATION,
    DATE_TIME,
}
