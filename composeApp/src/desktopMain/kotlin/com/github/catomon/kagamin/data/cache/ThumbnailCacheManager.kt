package com.github.catomon.kagamin.data.cache

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.AudioTagsReader
import com.github.catomon.kagamin.data.cacheFolder
import com.github.catomon.kagamin.ui.util.removeBlackBars
import com.github.catomon.kagamin.util.logErr
import com.github.catomon.kagamin.util.logTrace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.IOException
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.resizers.configurations.Antialiasing
import net.coobird.thumbnailator.resizers.configurations.Rendering
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import kotlin.time.Duration.Companion.seconds

object ThumbnailCacheManager {
    private val ongoingCacheJobs = mutableMapOf<String, File?>()
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

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun cacheThumbnail(
        trackUri: String,
        size: Int = SIZE.ORIGINAL,
        retrieveImage: (() -> BufferedImage?)? = null,
    ): File? {
        // check if we already have the result cached
        mutex.withLock {
            ongoingCacheJobs[trackUri]?.let { cached ->
                logTrace { "cache hit for $trackUri: ${cached?.path}" }
                return getSizedFile(cached, size)
            }
        }

        // try to get from disk first
        val diskFile = thumbnailCacheFolder.resolve("512/${trackUri.hashCode()}").takeIf { it.exists() }
        if (diskFile != null) {
            logTrace { "disk cache hit for $trackUri" }
            mutex.withLock { ongoingCacheJobs[trackUri] = diskFile }
            return getSizedFile(diskFile, size)
        }

        // meed to compute - coordinate with others
        return mutex.withLock {
            // double-check inside lock
            ongoingCacheJobs[trackUri]?.let { cached ->
                logTrace { "concurrent cache hit for $trackUri" }
                return@withLock getSizedFile(cached, size)
            }

            // create computation job
            val result = withTimeoutOrNull(10.seconds) {
                scope.async {
                    computeThumbnailFile(trackUri, retrieveImage)
                }.await()
            }

            if (result != null) {
                ongoingCacheJobs[trackUri] = result
                logTrace { "computation completed for $trackUri: ${result.path}" }
                return@withLock getSizedFile(result, size)
            }

            // start background computation for others
            ongoingCacheJobs[trackUri] = null // mark as "in progress"

            scope.launch {
                try {
                    val backgroundResult = computeThumbnailFile(trackUri, retrieveImage)
                    mutex.withLock {
                        ongoingCacheJobs[trackUri] = backgroundResult
                    }
                    logTrace { "background computation done for $trackUri: ${backgroundResult?.path}" }
                } catch (e: Exception) {
                    logErr { "background thumbnail failed for $trackUri: ${e.message}" }
                    mutex.withLock {
                        ongoingCacheJobs.remove(trackUri)
                    }
                }
            }

            null
        }
    }

    private suspend fun computeThumbnailFile(
        trackUri: String,
        retrieveImage: (() -> BufferedImage?)?,
    ): File? {
        val image = retrieveImage?.invoke()
            ?: AudioTagsReader.read(File(trackUri))?.tag?.firstArtwork?.image

        return image?.let { img ->
            cacheThumbnailFromImage(trackUri, img as BufferedImage)
        }
    }

    private suspend fun cacheThumbnailFromImage(sourcePath: String, image: BufferedImage): File? {
        return try {
            val uriHash = sourcePath.hashCode()
            val h512 = thumbnailCacheFolder.resolve("512/$uriHash")

            if (h512.exists()) {
                h512
            } else {
                thumbnailCacheFolder.parentFile?.mkdirs()
                h512.parentFile?.mkdirs()

                val processedImage = image.toComposeImageBitmap().removeBlackBars().toAwtImage()

                cacheScaledThumbnailFile(processedImage, uriHash, SIZE.H64)
                cacheScaledThumbnailFile(processedImage, uriHash, SIZE.H150)
                cacheScaledThumbnailFile(processedImage, uriHash, SIZE.H512)

                h512
            }
        } catch (e: Exception) {
            logErr { "failed to cache thumbnail: ${e.message}" }
            null
        }
    }

    private fun getSizedFile(baseFile: File?, size: Int): File? {
        return if (size > 0 && baseFile != null) {
            thumbnailCacheFolder.resolve("$size/${baseFile.name}")
        } else {
            baseFile
        }
    }

    private fun cacheScaledThumbnailFile(
        srcImage: BufferedImage,
        targetFileName: Int,
        height: Int = SIZE.ORIGINAL,
        width: Int = SIZE.DEFAULT_WIDTH
    ): File {
        val cachedScaledFile = thumbnailCacheFolder.resolve("$height/$targetFileName")
        if (cachedScaledFile.exists()) return cachedScaledFile

        cachedScaledFile.parentFile?.mkdirs()

        Thumbnails.of(srcImage).let { creator ->
            if (height != SIZE.ORIGINAL && srcImage.height > height) {
                creator.size(width, height)
            } else {
                creator.size(srcImage.width, srcImage.height)
            }
        }.outputFormat("JPEG")
            .outputQuality(0.90f)
            .antialiasing(Antialiasing.ON)
            .rendering(Rendering.QUALITY)
            .toFile(cachedScaledFile.parentFile!!.resolve("$targetFileName.JPEG"))

        // remove .JPEG from the name
        Files.move(
            cachedScaledFile.parentFile!!.resolve("$targetFileName.JPEG").toPath(),
            cachedScaledFile.toPath()
        )

        return cachedScaledFile
    }
}
