package com.github.catomon.kagamin.data.cache

import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.kagamin.data.cacheFolder
import com.github.catomon.kagamin.ui.util.removeBlackBars
import com.mpatric.mp3agic.Mp3File
import io.github.vinceglb.filekit.dialogs.compose.util.encodeToByteArray
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
import org.jetbrains.skia.Image
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

        const val DEFAULT_WIDTH = 1024
    }

    suspend fun cacheThumbnail(
        trackUri: String,
        size: Int = SIZE.ORIGINAL,
        mp3File: Mp3File? = null
    ): File? {
        return mutex.withLock {
            ongoingCacheJobs[trackUri]?.let { existingJob ->
                return@withLock existingJob
            }

            val newJob = CoroutineScope(Dispatchers.IO).async {
                try {
                    if (mp3File != null)
                        cacheThumbnail(trackUri, mp3File)
                    else
                        cacheThumbnail(trackUri)
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
                file?.parentFile?.resolve("$size/${file.name}")
            } else {
                file
            }
        }
    }

    private suspend fun cacheThumbnail(
        trackUri: String, mp3File: Mp3File = Mp3File(trackUri)
    ): File? {
        try {
            val uriHash = trackUri.hashCode()
            val cachedSrcFile = cacheFolder.resolve("thumbnails/$uriHash")

            if (cachedSrcFile.exists()) {
                return cachedSrcFile
            } else {
                cachedSrcFile.parentFile?.mkdirs()

                mp3File.id3v2Tag.albumImage?.let { albumImage ->
                    val image = Image.makeFromEncoded(albumImage)

                    val target = image.toComposeImageBitmap().removeBlackBars()

                    cachedSrcFile.outputStream().use { output ->
                        output.write(target.encodeToByteArray())
                    }
                }
            }

            if (cachedSrcFile.exists()) {
                cacheScaledThumbnailFile(cachedSrcFile, uriHash, SIZE.H64)
                cacheScaledThumbnailFile(cachedSrcFile, uriHash, SIZE.H150)
                cacheScaledThumbnailFile(cachedSrcFile, uriHash, SIZE.H250)

                return cachedSrcFile
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun cacheScaledThumbnailFile(
        cachedSrcFile: File,
        uriHash: Int,
        height: Int = SIZE.ORIGINAL,
        width: Int = SIZE.DEFAULT_WIDTH
    ) {
        val cachedScaledFile = cacheFolder.resolve("thumbnails/$height/$uriHash")
        if (cachedScaledFile.exists())
            return
        else
            cachedScaledFile.parentFile?.mkdirs()

        Thumbnails.of(cachedSrcFile).size(width, height).outputFormat("png")
            .outputQuality(0.75f).antialiasing(Antialiasing.ON).rendering(Rendering.QUALITY)
            .toFile(cachedScaledFile)

        Files.move(
            (cachedScaledFile.parentFile?.resolve("$uriHash.png")
                ?: File("$uriHash")).toPath(),
            (cachedScaledFile.parentFile?.resolve("$uriHash")
                ?: File("$uriHash")).toPath()
        )

        return
    }
}