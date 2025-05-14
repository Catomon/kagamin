package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class TrackData(
    val uri: String,
    val title: String = "",
    val artist: String = "",
    val duration: Long = -1,
    val isOnline: Boolean = false
)