package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kagamin.composeapp.generated.resources.def_thumb_150
import kagamin.composeapp.generated.resources.def_thumb_512
import kagamin.composeapp.generated.resources.def_thumb_64
import org.jetbrains.compose.resources.imageResource

object TrackThumbnailDefaults {
    val shape = RoundedCornerShape(12.dp)
}

@Composable
internal expect fun rememberTrackThumbnail(track: AudioTrack?, size: Int) : ImageBitmap?

private val defaultThumbnail = mapOf(
    0 to Res.drawable.def_thumb,
    64 to Res.drawable.def_thumb_64,
    150 to Res.drawable.def_thumb_150,
    512 to Res.drawable.def_thumb_512,
)

@Composable
fun TrackThumbnail(
    currentTrack: AudioTrack?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    shape: Shape = TrackThumbnailDefaults.shape,
    size: Int = 0,
) {
    echoTrace { "TrackThumbnail" }

    val thumbnail = rememberTrackThumbnail(currentTrack, size)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        AnimatedContent(
            thumbnail,
            modifier = Modifier.fillMaxSize().clip(shape)
        ) { model ->
            Image(
                bitmap = thumbnail ?: imageResource(defaultThumbnail[size] ?: Res.drawable.def_thumb),
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
                    .clip(shape),
                filterQuality = FilterQuality.High,
            )
        }
    }
}

@Composable
fun TrackThumbnailWithProgressOverlay(
    currentTrack: AudioTrack?,
    progress: Float,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    shape: Shape = TrackThumbnailDefaults.shape,
    onSetProgress: (Float) -> Unit = { },
    progressColor: Color = KagaminTheme.colors.thumbnailProgressIndicator,
    controlProgress: Boolean = false,
    shadow: Boolean = true,
    size: Int = 0
) {
    echoTrace { "TrackThumbnailWithProgressOverlay" }

    Box(
        modifier.then(
            if (shadow)
                Modifier.drawBehind {
                    drawRoundRect(
                        color = KagaminTheme.colors.thinBorder,
                        topLeft = Offset(0f, with(density) { 1.dp.toPx() }),
                        size = this.size.copy(height = this.size.height + 2.dp.toPx()),
                        cornerRadius = CornerRadius(if (shape is RoundedCornerShape) 12f else 0f)
                    )
                }
            else Modifier
        )
            .let {
                if (controlProgress) {
                    it.pointerInput(currentTrack) {
                        if (currentTrack == null) return@pointerInput

                        val width = this.size.width
                        detectTapGestures { offset ->
                            onSetProgress(offset.x / width)
                        }
                    }
                } else it
            }) {

        TrackThumbnail(currentTrack, Modifier.fillMaxSize(), contentScale, blur, shape, size)

        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize().let {
                    if (progress > 0) it.weight(progress) else it
                }.background(
                    progressColor,
                    shape as? RoundedCornerShape ?: RoundedCornerShape(0f)
                )
            ) { }

            Box(modifier = Modifier.let {
                val weight = 1f - progress; if (weight > 0) it.weight(weight) else it
            }) { }
        }
    }
}