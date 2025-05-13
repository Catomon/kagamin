package com.github.catomon.kagamin.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.github.catomon.kagamin.cacheFolder
import com.mpatric.mp3agic.Mp3File
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.IRect
import org.jetbrains.skia.Image
import java.io.File

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

object ThumbnailCacheManager {
    private val ongoingCacheJobs = mutableMapOf<String, Deferred<File?>>()

    private val mutex = Mutex()

    suspend fun cacheThumbnailOnce(trackUri: String): File? {
        return mutex.withLock {
            ongoingCacheJobs[trackUri]?.let { existingJob ->
                return@withLock existingJob
            }

            val newJob = CoroutineScope(Dispatchers.IO).async {
                cacheThumbnail(trackUri)
            }

            ongoingCacheJobs[trackUri] = newJob
            newJob.invokeOnCompletion {
                runBlocking {
                    mutex.withLock {
                        ongoingCacheJobs.remove(trackUri)
                    }
                }
            }
            newJob
        }.await()
    }

    private suspend fun cacheThumbnail(trackUri: String): File? {
        try {
            cacheFolder.mkdirs()

            val uriHash = trackUri.hashCode()
            val cachedFile = cacheFolder.resolve("thumbnails/$uriHash")
            if (cachedFile.exists()) {
                return cachedFile
            } else {
                cachedFile.parentFile.mkdirs()

                val file = Mp3File(trackUri)
                file.id3v2Tag.albumImage?.let { albumImage ->
                    val image = Image.makeFromEncoded(albumImage)

                    val target = removeBlackBars(image.toComposeImageBitmap())

                    cachedFile.outputStream().use { output ->
                        output.write(target.encodeToByteArray())
                    }
                }

                return cachedFile
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}

suspend fun cacheThumbnail(trackUri: String): File? = withContext(Dispatchers.IO) {
    try {
        cacheFolder.mkdirs()

        val uriHash = trackUri.hashCode()
        val cachedFile = cacheFolder.resolve("thumbnails/$uriHash")
        if (cachedFile.exists()) {
            cachedFile
        } else {
            cachedFile.parentFile.mkdirs()

            val file = Mp3File(trackUri)
            file.id3v2Tag.albumImage?.let { albumImage ->
                val image = Image.makeFromEncoded(albumImage)

                val target = removeBlackBars(image.toComposeImageBitmap())

                cachedFile.outputStream().use { output ->
                    output.write(target.encodeToByteArray())
                }
            }

            cachedFile
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun removeBlackBars(original: ImageBitmap): ImageBitmap {
    val width = original.width
    val height = original.height

    var left = width
    var right = 0
    var top = height
    var bottom = 0

    val pixels = IntArray(width * height)
    original.readPixels(pixels)

//    val topColor = Color(pixels[width / 2 ])
    val leftColor = Color(pixels[(height / 2) * width])

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = pixels[y * width + x]
            val color = Color(pixel)
            val isHorizontalBarsColor =
                false //abs((color.red * 255 + color.green * 255 + color.blue * 255) - (leftColor.red * 255 + leftColor.green * 255 + leftColor.blue * 255)) < 25
            val isVerticalBarsColor = color.red * 255 + color.green * 255 + color.blue * 255 > 120

            if (isHorizontalBarsColor || isVerticalBarsColor) { //0.47f
                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y
            }
        }
    }

    val offset = IntOffset(left, top)
    val size = IntSize(right - left + 1, bottom - top + 1)

    return original.cropped(offset, size)
}

fun ImageBitmap.cropped(offset: IntOffset, size: IntSize): ImageBitmap {
    val srcBitmap = this.asSkiaBitmap()

    val dstBitmap = Bitmap()
    dstBitmap.allocN32Pixels(size.width, size.height)

    val subset = IRect.makeXYWH(offset.x, offset.y, size.width, size.height)

    val success = srcBitmap.extractSubset(dstBitmap, subset)

    require(success) { "Failed to extract subset from bitmap" }

    return dstBitmap.asComposeImageBitmap()
}

//actual fun getThumbnail(trackUri: String, maxSize: Int): ImageBitmap? = try {
////    println("Loading thumbnail: ")
//
//    val uriHash = trackUri.hashCode()
//    val cachedFile = cacheFolder.resolve((if (maxSize < 1024) "$maxSize/" else "") + "$uriHash")
//    if (cachedFile.exists()) {
//        val bufferedImage = javax.imageio.ImageIO.read(cachedFile)
//        if (bufferedImage != null) {
//            //            println("success.")
//            bufferedImage.toComposeImageBitmap()
//        } else {
//            //        println("fail.")
//            null
//        }
//    } else {
//        cachedFile.parentFile.mkdirs()
//
//        val file = Mp3File(trackUri)
//        file.id3v2Tag.albumImage?.let { albumImage ->
//
//            val image = Image.makeFromEncoded(albumImage)
//
//            val srcHeight = image.height
//            val srcWidth = image.width
//
//            val diff = if (srcHeight > maxSize) maxSize / srcHeight.toFloat() else 1f
//
//            val width =  (srcWidth * diff).toInt()
//            val height = (srcHeight * diff).toInt()
//
////            val info = ImageInfo(width, height, image.colorType, image.colorInfo.alphaType)
////
////            val bytesPerPixel = image.bytesPerPixel
////            val rowBytes = width * bytesPerPixel
////
////            val buffer = Data.makeUninitialized(rowBytes * height)
////
////            val dstPixmap = Pixmap.make(info, buffer, rowBytes)
//
//            val dstPixmap = Bitmap()
//            dstPixmap.allocN32Pixels(width, height)
//
//            val success = image.scalePixels(dstPixmap.peekPixels()!!, SamplingMode.LINEAR, cache = true)
//
//            if (!success) {
//                echoWarn("[getThumbnail] Failed to scale image pixels")
//            }
//
//            val scaledImage = Image.makeFromBitmap(dstPixmap)
//
//            cachedFile.outputStream().use { output ->
//                val encodedData = scaledImage.encodeToData(EncodedImageFormat.PNG) ?: run {
//                    echoWarn("[getThumbnail] encodeToData == null")
//                    return@use
//                }
//                output.write(encodedData.bytes)//bytearray
//            }
//
//            scaledImage.toComposeImageBitmap()
//        }.also {
////            println("success.")
//        }
//    }
//} catch (e: IOException) {
////        println("fail.")
//    e.printStackTrace()
//    null
//}