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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.userDataFolder
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import java.io.File

@Composable
fun TrackThumbnail(
    trackUri: String?,
    onSetProgress: (Float) -> Unit = { },
    progress: Float = 0f,
    progressColor: Color = KagaminTheme.colors.thumbnailProgressIndicator,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    controlProgress: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    height: Int = ThumbnailCacheManager.SIZE.ORIGINAL
) {
    var cachedThumbnailFile by remember { mutableStateOf<File?>(null) }
    val defaultPainter = painterResource(Res.drawable.def_thumb)

    LaunchedEffect(trackUri) {
        cachedThumbnailFile = if (trackUri != null) withContext(Dispatchers.Default) {
            ThumbnailCacheManager.cacheThumbnailSafe(trackUri, size = height)
        } else null
    }

    val density = LocalDensity.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = KagaminTheme.colors.backgroundTransparent,
                    topLeft = Offset(0f, with(density) { 2.dp.toPx() }),
                    size = this.size.copy(height = this.size.height + 2.dp.toPx()),
                    cornerRadius = CornerRadius(if (shape is RoundedCornerShape) 12f else 0f)
                )
            }
//            .clip(shape)
            .let {
                if (controlProgress) {
                    it.pointerInput(Unit) {
                        val width = this.size.width
                        detectTapGestures { offset ->
                            onSetProgress(offset.x / width)
                        }
                    }
                } else {
                    it
                }
            }
    ) {
        AnimatedContent(
            cachedThumbnailFile,
            modifier = Modifier.fillMaxSize().clip(shape)
        ) { cachedThumbnailFile ->
//            if (cachedThumbnailFile == null) {
//                Box(
//                    Modifier.fillMaxSize()
//                ) {
//                    Image(
//                        painterResource(Res.drawable.def_thumb),
//                        "Default track thumbnail",
//                        contentScale = contentScale,
//                        modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
//                            .clip(shape)
//                    )
//                }
//            } else {

            AsyncImage(
                cachedThumbnailFile
                    ?: userDataFolder.resolve("definitely_not_existing_file"),
                "Track thumbnail",
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
                    .clip(shape),
                placeholder = null,
                error = defaultPainter,
                fallback = defaultPainter,
                filterQuality = FilterQuality.High
            )

//            val painter = rememberAsyncImagePainter("https://example.com/image.jpg")
//
//            Canvas(modifier = Modifier.size(200.dp)) {
//                with(painter) {
//                    draw(size)
//                }
//
//                val imageBitmap = (painter.imageLoader.execute(painter.request).drawable as? BitmapDrawable)?.bitmap?.asImageBitmap()
//                if (imageBitmap != null) {
//                    drawImageRect(
//                        image = imageBitmap,
//                        dstOffset = IntOffset(0, 0),
//                        dstSize = IntSize(size.width.toInt(), size.height.toInt())
//                    )
//                }
//            }

//                Image(
//                    remember(cachedThumbnailFile) {
//                        BitmapPainter(
//                            cachedThumbnailFile,
//                            filterQuality = DrawScope.DefaultFilterQuality,
//                            srcOffset = offset,
//                            srcSize = size
//                        )
//                    },
//                    "Track thumbnail",
//                    contentScale = contentScale,
//                    modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
//                        .clip(shape)
//                )
//            }
        }

        if (progress >= 0) Row(modifier = Modifier.fillMaxSize()) {
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