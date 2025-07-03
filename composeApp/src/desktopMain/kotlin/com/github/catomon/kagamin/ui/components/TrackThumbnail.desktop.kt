package com.github.catomon.kagamin.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.imageio.ImageIO

suspend fun loadImageBitmapFromUrl(url: String, context: PlatformContext): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            result.image.toBitmap().asComposeImageBitmap()
        } else {
            null
        }
    }
}

@Composable
internal actual fun rememberTrackThumbnail(track: AudioTrack?, size: Int): ImageBitmap? {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalPlatformContext.current

    LaunchedEffect(track) {
        bitmap = if (track != null) {
            if (track.artworkUri?.startsWith("https") == true) {
                loadImageBitmapFromUrl(track.artworkUri, context)
            } else {
                withContext(Dispatchers.Default) {
                    if (track.uri.startsWith("https"))
                        null
                    else
                        ThumbnailCacheManager.cacheThumbnail(track.uri, size = size)
                            ?.let { file ->
                                try {
                                    ImageIO.read(file).toComposeImageBitmap()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    null
                                }
                            }
                }
            }
        } else return@LaunchedEffect
    }

    return bitmap
}