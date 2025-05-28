package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class AudioTrack @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val uri: String,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 0,
    val artworkUri: String? = null
)