package com.github.catomon.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistData(
    val tracks: Array<TrackData>,
    val isOnline: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistData

        if (isOnline != other.isOnline) return false
        if (!tracks.contentEquals(other.tracks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isOnline.hashCode()
        result = 31 * result + tracks.contentHashCode()
        return result
    }
}