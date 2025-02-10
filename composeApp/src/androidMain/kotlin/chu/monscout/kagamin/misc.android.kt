package chu.monscout.kagamin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaTrack

actual fun <T : DenpaTrack> createDenpaTrack(uri: String, name: String): T {
    TODO("Not yet implemented")
}

@Composable
actual fun DenpaFilePicker(
    show: MutableState<Boolean>,
    denpaPlayer: DenpaPlayer<DenpaTrack>,
    currentPlaylistName: String
) {
}