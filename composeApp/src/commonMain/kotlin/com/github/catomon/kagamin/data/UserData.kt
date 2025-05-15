package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val playliststs: Array<Playlist>
)