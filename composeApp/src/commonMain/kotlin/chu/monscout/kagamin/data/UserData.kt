package chu.monscout.kagamin.data

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val playliststs: Array<PlaylistData>
)