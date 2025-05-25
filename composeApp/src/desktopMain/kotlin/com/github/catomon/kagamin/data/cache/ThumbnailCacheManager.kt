package com.github.catomon.kagamin.data.cache

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.data.cacheFolder
import com.github.catomon.kagamin.ui.util.removeBlackBars
import com.github.catomon.kagamin.util.logDbg
import com.github.catomon.kagamin.util.logErr
import com.github.catomon.kagamin.util.logTrace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var timeoutJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun startTimeoutJob() {
        timeoutJob?.cancel()
        timeoutJob = coroutineScope.launch {
            delay(3000)
            try {
                ongoingCacheJobs.forEach { it.value.cancel() }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            coroutineScope2.cancel()

            logErr { "ongoingCacheJobs wa canceled by timeoutJob after 3 sec." }
        }
    }

    private val coroutineScope2 = CoroutineScope(Dispatchers.IO)

    suspend fun cacheThumbnail(
        trackUri: String,
        size: Int = SIZE.ORIGINAL,
        retrieveImage: (() -> BufferedImage?)? = null,
    ): File? {
        return mutex.withLock {
            ongoingCacheJobs[trackUri]?.let { existingJob ->
                logTrace { "existing job retrieved for track uri: $trackUri" }
                return@withLock existingJob
            }

            logTrace { "job created for track uri: $trackUri" }
            val newJob = coroutineScope2.async {
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

            newJob.start()

            ongoingCacheJobs[trackUri] = newJob
            newJob.invokeOnCompletion { error ->
                logTrace { "job completed for track uri: $trackUri" }

                error?.printStackTrace()

                runBlocking {
                    mutex.withLock {
                        ongoingCacheJobs.remove(trackUri)
                    }
                }
            }

            startTimeoutJob()

            newJob
        }.await().let { file ->
            logTrace { "job result is ${file?.path} for size $size for track uri: $trackUri" }

            try {
                if (ongoingCacheJobs.isEmpty())
                    timeoutJob?.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }

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