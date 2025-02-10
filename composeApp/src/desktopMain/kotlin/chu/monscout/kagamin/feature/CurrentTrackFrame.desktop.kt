package chu.monscout.kagamin.feature

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import chu.monscout.kagamin.audio.DenpaTrack
import com.mpatric.mp3agic.Mp3File
import org.jetbrains.skia.Image

actual fun getThumbnail(denpaTrack: DenpaTrack): ImageBitmap? {
    return try {
        val file = Mp3File(denpaTrack.uri)
        file.id3v2Tag.albumImage?.let { albumImage ->
            Image.makeFromEncoded(albumImage)
                .toComposeImageBitmap()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}