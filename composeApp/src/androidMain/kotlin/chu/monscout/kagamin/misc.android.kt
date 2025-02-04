import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import audio.DenpaPlayer
import audio.DenpaTrack

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