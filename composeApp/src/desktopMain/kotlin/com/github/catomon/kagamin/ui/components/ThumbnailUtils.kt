package com.github.catomon.kagamin.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.cacheFolder
import com.mpatric.mp3agic.Mp3File
import kotlinx.io.IOException
import org.jetbrains.skia.Image

actual fun getThumbnail(trackUri: String): ImageBitmap? = try {
//    println("Loading thumbnail: ")

    cacheFolder.mkdirs()

    val uriHash = trackUri.hashCode()
    val cachedFile = cacheFolder.resolve("$uriHash")
    if (cachedFile.exists()) {
        val bufferedImage = javax.imageio.ImageIO.read(cachedFile)
        if (bufferedImage != null) {
            //            println("success.")
            bufferedImage.toComposeImageBitmap()
        } else {
            //        println("fail.")
            null
        }
    } else {
        val file = Mp3File(trackUri)
        file.id3v2Tag.albumImage?.let { albumImage ->

            cachedFile.outputStream().use { output ->
                output.write(albumImage)
            }

            Image.makeFromEncoded(albumImage)
                .toComposeImageBitmap()
        }.also {
//            println("success.")
        }
    }
} catch (e: IOException) {
//        println("fail.")
    e.printStackTrace()
    null
}