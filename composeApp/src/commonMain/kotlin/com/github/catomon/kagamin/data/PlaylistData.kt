package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistData(
    val tracks: Array<TrackData>,
    val isOnline: Boolean = false
)