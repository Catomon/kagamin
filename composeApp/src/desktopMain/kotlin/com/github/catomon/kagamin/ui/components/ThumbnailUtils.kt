package com.github.catomon.kagamin.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.io.IOException
//import androidx.compose.ui.graphics.toComposeImageBitmap
//import com.github.catomon.kagamin.data.cacheFolder
//import com.mpatric.mp3agic.Mp3File
//import org.jetbrains.skia.Image

@Deprecated("")
actual fun getThumbnail(trackUri: String): ImageBitmap? = try {
    null
//    cacheFolder.mkdirs()
//
//    val uriHash = trackUri.hashCode()
//    val cachedFile = cacheFolder.resolve("$uriHash")
//    if (cachedFile.exists()) {
//        val bufferedImage = javax.imageio.ImageIO.read(cachedFile)
//        if (bufferedImage != null) {
//            bufferedImage.toComposeImageBitmap()
//        } else {
//            null
//        }
//    } else {
//        val file = Mp3File(trackUri)
//        file.id3v2Tag.albumImage?.let { albumImage ->
//
//            cachedFile.outputStream().use { output ->
//                output.write(albumImage)
//            }
//
//            Image.makeFromEncoded(albumImage)
//                .toComposeImageBitmap()
//        }
//    }
} catch (e: IOException) {
    e.printStackTrace()
    null
}
