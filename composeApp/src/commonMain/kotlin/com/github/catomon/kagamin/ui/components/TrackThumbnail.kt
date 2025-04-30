package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.getCropParameters
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun TrackThumbnail(
    image: ImageBitmap?,
    onSetProgress: (Float) -> Unit,
    progress: Float,
    progressColor: Color = KagaminTheme.theme.thumbnailProgressIndicator,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    controlProgress: Boolean = true
) {
    var offset by remember { mutableStateOf(IntOffset(0, 0)) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }

    var isUpdatingOffsets by remember { mutableStateOf(false) }

    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    //crop black bars
    LaunchedEffect(image) {
        isUpdatingOffsets = true
        if (image != null) {
            val crop = withContext(Dispatchers.IO) {
                getCropParameters(image)
            }
            size = crop.second
            offset = crop.first
        }
        croppedImage = image
        isUpdatingOffsets = false
    }

    val density = LocalDensity.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = KagaminTheme.theme.backgroundTransparent,
                topLeft = Offset(0f, with(density) { 2.dp.toPx() }),
                size = this.size.copy(),
                cornerRadius = CornerRadius(12f)
            )
        }
            .clip(RoundedCornerShape(12.dp))
            .let {
                if (controlProgress) {
                    it.pointerInput(croppedImage) {
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
        AnimatedContent(croppedImage, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))) { croppedImage ->
            if (croppedImage == null) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    Image(
                        painterResource(Res.drawable.def_thumb),
                        "Default track thumbnail",
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }.clip(RoundedCornerShape(14.dp))
                    )
                }
            } else {
                Image(
                    remember(croppedImage) {
                        BitmapPainter(
                            croppedImage,
                            filterQuality = DrawScope.DefaultFilterQuality,
                            srcOffset = offset,
                            srcSize = size
                        )
                    },
                    "Track thumbnail",
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }.clip(RoundedCornerShape(14.dp))
                )
            }
        }

        if (progress >= 0) Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxHeight().let {
                    if (progress > 0) it.weight(progress) else it
                }.background(
                    progressColor
                )
            ) { }

            Box(modifier = Modifier.fillMaxHeight().let {
                val weight = 1f - progress; if (weight > 0) it.weight(weight) else it
            }) { }
        }
    }
}