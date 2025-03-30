package chu.monscout.kagamin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.audio.AudioTrackAndy
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

actual fun <T : AudioTrack> createDenpaTrack(uri: String, name: String): T {
    val uri = File(uri).toUri()
    println(uri)
    return AudioTrackAndy(
        MediaItem.Builder().setUri(uri).setMediaId(UUID.randomUUID().toString(),).build()
    ) as T
}

@Composable
actual fun DenpaFilePicker(
    show: MutableState<Boolean>,
    audioPlayer: AudioPlayer<AudioTrack>,
    currentPlaylistName: String
) {
    val a = LocalSnackbarHostState.current
    val fileType = listOf("mp3", "wav")
    MultipleFilePicker(show = show.value, fileExtensions = fileType) { files ->
        show.value = false
        if (files != null) {
            //it.platformFile desk - File, android - Uri
            audioPlayer.load(files.map { it.platformFile.toString() })

            savePlaylist(currentPlaylistName, audioPlayer.playlist.value.toTypedArray())
        }

        //fixme
        GlobalScope.launch {
            a.showSnackbar("${files?.size ?: 0} tracks were added.")
        }
    }
}