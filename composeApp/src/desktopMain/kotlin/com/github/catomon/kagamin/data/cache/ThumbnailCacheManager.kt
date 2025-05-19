package com.github.catomon.kagamin.data.cache

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.data.cacheFolder
import com.github.catomon.kagamin.ui.util.removeBlackBars
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.IOException
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.resizers.configurations.Antialiasing
import net.coobird.thumbnailator.resizers.configurations.Rendering
import org.jaudiotagger.audio.AudioFileIO
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files

object ThumbnailCacheManager {
    private val ongoingCacheJobs = mutableMapOf<String, Deferred<File?>>()

    private val mutex = Mutex()

    object SIZE {
        const val ORIGINAL = 0
        const val H64 = 64
        const val H150 = 150
        const val H250 = 250
        const val H512 = 512

        const val DEFAULT_WIDTH = 1024
    }

    private val thumbnailCacheFolder = cacheFolder.resolve("thumbnails/")

    suspend fun cacheThumbnail(
        trackUri: String,
        size: Int = SIZE.ORIGINAL,
        retrieveImage: (() -> BufferedImage?)? = null,
    ): File? {
        return mutex.withLock {
            ongoingCacheJobs[trackUri]?.let { existingJob ->
                return@withLock existingJob
            }

            val newJob = CoroutineScope(Dispatchers.IO).async {
                try {
                    thumbnailCacheFolder.resolve("512/${trackUri.hashCode()}").let {
                        if (it.exists()) it else {
                            if (retrieveImage != null)
                                cacheThumbnail(trackUri, retrieveImage() ?: return@async null)
                            else
                                cacheThumbnail(
                                    trackUri,
                                    AudioFileIO.read(File(trackUri))
                                        .let {
                                            it.tag?.firstArtwork?.image as BufferedImage
                                        }
                                )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
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
        }.await().let { file ->
            if (size > 0) {
                file?.let {
                    thumbnailCacheFolder.resolve("$size/${file.name}")
                }
            } else {
                file
            }
        }
    }

    private suspend fun cacheThumbnail(
        sourcePath: String, image: BufferedImage
    ): File? {
        try {
            val uriHash = sourcePath.hashCode()
            val extractedSrcTmpFile = cacheFolder.resolve("temp")
            val h512 = cacheFolder.resolve("thumbnails/512/$uriHash")
            if (!h512.exists()) {
                extractedSrcTmpFile.parentFile?.mkdirs()
                val srcImage = image.toComposeImageBitmap().removeBlackBars()
                val srcHeight = srcImage.toAwtImage()
                cacheScaledThumbnailFile(srcHeight, uriHash, SIZE.H64)
                cacheScaledThumbnailFile(srcHeight, uriHash, SIZE.H150)
//                cacheScaledThumbnailFile(srcHeight, uriHash, SIZE.H250)
                return cacheScaledThumbnailFile(
                    srcHeight,
                    uriHash,
                    SIZE.H512
                ).also { extractedSrcTmpFile.delete() }
            } else {
                return h512
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }


    private fun cacheScaledThumbnailFile(
        srcImage: BufferedImage,
        targetFileName: Int,
        height: Int = SIZE.ORIGINAL,
        width: Int = SIZE.DEFAULT_WIDTH
    ): File {
        val cachedScaledFile = cacheFolder.resolve("thumbnails/$height/$targetFileName")
        if (cachedScaledFile.exists())
            return cachedScaledFile
        else
            cachedScaledFile.parentFile?.mkdirs()

        Thumbnails.of(srcImage).let {
            if (height != SIZE.ORIGINAL && srcImage.height > height)
                it.size(width, height)
            else it.size(srcImage.width, srcImage.height)
        }.outputFormat("JPEG")
            .outputQuality(0.90f).antialiasing(Antialiasing.ON).rendering(Rendering.QUALITY)
            .toFile(cachedScaledFile)

        return Files.move(
            (cachedScaledFile.parentFile?.resolve("$targetFileName.JPEG")
                ?: File("$targetFileName")).toPath(),
            (cachedScaledFile.parentFile?.resolve("$targetFileName")
                ?: File("$targetFileName")).toPath()
        ).toFile()
    }
}