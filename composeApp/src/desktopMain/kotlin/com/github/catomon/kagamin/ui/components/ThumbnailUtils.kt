package com.github.catomon.kagamin.ui.components

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.audio.AudioTrack
import com.mpatric.mp3agic.Mp3File
import org.jetbrains.skia.Image

actual fun getThumbnail(audioTrack: AudioTrack): ImageBitmap? {
    print("Loading thumbnail: ")

    //TODO
//    val jvmTrack = audioTrack as AudioTrackJVM
//    val artUrl = jvmTrack.audioTrack?.info?.artworkUrl
//    if (artUrl?.isNotBlank() == true) {
//        print("is an url image; ")
//        return try {
//            val connection = URL(artUrl).openStream()
//            val bufferedImage: BufferedImage = ImageIO.read(connection)
//            bufferedImage?.toComposeImageBitmap().also {
//                println("success.")
//            }
//        } catch (e: Exception) {
//            println("fail.")
//            e.printStackTrace()
//            null
//        }
//    }

    return try {
        print("try load a file;")
        val file = Mp3File(audioTrack.uri)
        file.id3v2Tag.albumImage?.let { albumImage ->
            Image.makeFromEncoded(albumImage)
                .toComposeImageBitmap()
        }.also {
            println("success.")
        }
    } catch (e: Exception) {
        println("fail.")
        e.printStackTrace()
        null
    }
}