package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.data.userDataFolder
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kagamin.composeapp.generated.resources.def_thumb_150
import kagamin.composeapp.generated.resources.def_thumb_512
import kagamin.composeapp.generated.resources.def_thumb_64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

object TrackThumbnailDefaults {
    val shape = RoundedCornerShape(12.dp)
}

val defaultThumbnail = mapOf(
    ThumbnailCacheManager.SIZE.ORIGINAL to Res.drawable.def_thumb,
    ThumbnailCacheManager.SIZE.H64 to Res.drawable.def_thumb_64,
    ThumbnailCacheManager.SIZE.H150 to Res.drawable.def_thumb_150,
    ThumbnailCacheManager.SIZE.H512 to Res.drawable.def_thumb_512,
)

@Composable
fun TrackThumbnail(
    currentTrack: AudioTrack?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    shape: Shape = TrackThumbnailDefaults.shape,
    height: Int = ThumbnailCacheManager.SIZE.ORIGINAL
) {
    echoTrace { "TrackThumbnail" }

    var thumbnailModel by remember { mutableStateOf<Any?>(userDataFolder.resolve("definitely_not_existing_file")) }
    val defaultPainter = painterResource(defaultThumbnail[height] ?: Res.drawable.def_thumb)

    LaunchedEffect(currentTrack) {
        thumbnailModel = if (currentTrack != null) {
            if (currentTrack.artworkUri?.startsWith("https") == true) {
                currentTrack.artworkUri
            } else {
                withContext(Dispatchers.Default) {
                    if (currentTrack.uri.startsWith("https"))
                        null
                    else
                        ThumbnailCacheManager.cacheThumbnail(currentTrack.uri, size = height)
                            ?.let { file ->
                                file as Any
                            }
                }
            }
        } else return@LaunchedEffect
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedContent(
            thumbnailModel,
            modifier = Modifier.fillMaxSize().clip(shape)
        ) { cachedThumbnailFile ->
            AsyncImage(
                cachedThumbnailFile,
                "Track thumbnail",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
                    .clip(shape),
                placeholder = null,
                error = defaultPainter,
                fallback = defaultPainter,
                filterQuality = FilterQuality.High
            )
        }
    }
}

@Composable
fun TrackThumbnailProgressOverlay(
    currentTrack: AudioTrack?,
    progress: Float,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    shape: Shape = TrackThumbnailDefaults.shape,
    height: Int = ThumbnailCacheManager.SIZE.ORIGINAL,
    onSetProgress: (Float) -> Unit = { },
    progressColor: Color = KagaminTheme.colors.thumbnailProgressIndicator,
    controlProgress: Boolean = false,
) {
    echoTrace { "TrackThumbnailProgressOverlay" }

    Box(
        modifier
            .drawBehind {
                drawRoundRect(
                    color = KagaminTheme.colors.thinBorder,
                    topLeft = Offset(0f, with(density) { 1.dp.toPx() }),
                    size = this.size.copy(height = this.size.height + 2.dp.toPx()),
                    cornerRadius = CornerRadius(if (shape is RoundedCornerShape) 12f else 0f)
                )
            }.let {
                if (controlProgress) {
                    it.pointerInput(currentTrack) {
                        if (currentTrack == null) return@pointerInput

                        val width = this.size.width
                        detectTapGestures { offset ->
                            onSetProgress(offset.x / width)
                        }
                    }
                } else {
                    it.pointerInput(currentTrack) {
                        if (currentTrack == null) return@pointerInput

                        val width = this.size.width
                        detectTapGestures { offset ->
                            onSetProgress(offset.x / width)
                        }
                    }
                }
            }) {

        TrackThumbnail(currentTrack, Modifier.fillMaxSize(), contentScale, blur, shape, height)

        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxHeight().let {
                    if (progress > 0) it.weight(progress) else it
                }.background(
                    progressColor,
                    RoundedCornerShape(if (shape is RoundedCornerShape) 10f else 0f)
                )
            ) { }

            Box(modifier = Modifier.fillMaxHeight().let {
                val weight = 1f - progress; if (weight > 0) it.weight(weight) else it
            }) { }
        }
    }
}