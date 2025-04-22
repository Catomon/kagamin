package chu.monscout.kagamin.ui.components

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
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.ui.util.getCropParameters
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun TrackThumbnail(
    currentTrack: AudioTrack?,
    player: AudioPlayer<AudioTrack>,
    updateProgress: () -> Unit,
    progress: Float,
    progressColor: Color = Colors.theme.thumbnailProgressIndicator,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    blur: Boolean = false,
    controlProgress: Boolean = true
) {
    //val progressColor = remember { Colors.bars.copy(0.5f) }

    var image by remember {
        mutableStateOf<ImageBitmap?>(null)
    }
    var loadingThumb by remember { mutableStateOf(true) }
    var offset by remember { mutableStateOf(IntOffset(0, 0)) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }

    LaunchedEffect(currentTrack) {
        loadingThumb = true
        image = if (currentTrack != null) {
            withContext(Dispatchers.IO) {
                getThumbnail(currentTrack)?.let { thumbnail ->
                    val crop = getCropParameters(thumbnail)
                    size = crop.second
                    offset = crop.first

                    thumbnail
                }
            }
        } else {
            null
        }
        loadingThumb = false
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = Colors.theme.backgroundTransparent,
                topLeft = Offset(0f, 2f),
                size = this.size.copy(),
                cornerRadius = CornerRadius(12f)
            )
        }
//            .border(2.dp, Colors.currentYukiTheme.thinBorder, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)).let {
                if (controlProgress) {
                    it.pointerInput(currentTrack) {
                        if (currentTrack == null) return@pointerInput
                        val width = this.size.width
                        detectTapGestures {
                            player.seek((currentTrack.duration * (it.x / width)).toLong())
                            updateProgress()
                        }
                    }
                } else {
                    it
                }
            }) {
        AnimatedContent(image, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))) {
            if (!loadingThumb && image == null) { //this still executes idc
                Box(
                    Modifier.fillMaxSize()
                ) {
                    Image(
                        painterResource(Res.drawable.def_thumb),
                        "Default track thumbnail",
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }.alpha(0.25f)
                    )
                }
            } else {
                if (image == null) {
                    Box(
                        Modifier.fillMaxSize()
                    ) {}
                } else {
                    Image(
                        remember {
                            BitmapPainter(
                                image!!,
                                filterQuality = DrawScope.DefaultFilterQuality,
                                srcOffset = offset,
                                srcSize = size
                            )
                        },
                        "Track thumbnail",
                        contentScale = contentScale,
                        modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
                    )
                }
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