package chu.monscout.kagamin.feature

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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

//class TrackThumbnail (
//    val imageBitmap: ImageBitmap,
//    val offsetToSize: Pair<IntOffset, IntSize>
//)


//fun getThumbnail(denpaTrack: DenpaTrack): TrackThumbnail? {
//    return try {
//        val file = Mp3File(denpaTrack.uri)
//        val imageBitmap = file.id3v2Tag.albumImage?.let { albumImage ->
//            Image.makeFromEncoded(albumImage)
//                .toComposeImageBitmap()
//        }
//        imageBitmap.readPixels()
//
//        null
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}