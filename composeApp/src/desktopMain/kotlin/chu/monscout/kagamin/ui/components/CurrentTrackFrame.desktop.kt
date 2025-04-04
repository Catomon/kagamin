package chu.monscout.kagamin.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import chu.monscout.kagamin.audio.AudioTrack
import com.mpatric.mp3agic.Mp3File
import org.jetbrains.skia.Image

actual fun getThumbnail(audioTrack: AudioTrack): ImageBitmap? {
    return try {
        val file = Mp3File(audioTrack.uri)
        file.id3v2Tag.albumImage?.let { albumImage ->
            Image.makeFromEncoded(albumImage)
                .toComposeImageBitmap()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}