package chu.monscout.kagamin.audio

import android.app.Activity
import android.net.Uri
import androidx.media3.common.MediaItem
import chu.monscout.kagamin.playerContext
import java.util.UUID

actual fun <T : AudioTrack> createAudioTrack(uri: String, name: String): T {
    val uri = Uri.parse(uri)
    val inputStream = (playerContext?.invoke() as Activity).contentResolver.let { contentResolver ->
        val input = contentResolver.openInputStream(uri)

        input
    }

    return (AudioTrackAndy(
        MediaItem.Builder().setUri(uri).setMediaId(UUID.randomUUID().toString()).build()
    ).also { it.name = name } as T).also { inputStream?.close() }
}