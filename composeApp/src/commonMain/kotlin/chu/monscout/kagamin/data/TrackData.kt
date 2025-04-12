package chu.monscout.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class TrackData(
    val uri: String,
    val name: String,
    val isOnline: Boolean = false
)