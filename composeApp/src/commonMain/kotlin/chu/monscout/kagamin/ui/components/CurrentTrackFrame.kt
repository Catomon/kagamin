package chu.monscout.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

expect fun getThumbnail(audioTrack: AudioTrack): ImageBitmap?

@Composable
fun CurrentTrackFrame(
    currentTrack: AudioTrack?, player: AudioPlayer<AudioTrack>, modifier: Modifier = Modifier
) {
    val playMode by player.playMode
    val crossfade by player.crossfade

    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == AudioPlayer.PlayState.PLAYING) updateProgress()
            delay(1000)
        }
    }

    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TrackThumbnail(
                currentTrack,
                player,
                updateProgress,
                progress,
                modifier = Modifier.padding(8.dp).size(145.dp)
            )

            PlaybackButtons(
                player = player, Modifier.width(133.dp)
            )

            PlaybackOptionsButtons(player, Modifier.width(133.dp))

            Box {
                TrackProgressIndicator(
                    currentTrack,
                    player,
                    updateProgress,
                    progress,
                    color = Colors.currentYukiTheme.thinBorder,
                    textColor = Colors.currentYukiTheme.thinBorder,
                    Modifier.graphicsLayer(translationY = 2f)
                )
                TrackProgressIndicator(currentTrack, player, updateProgress, progress)
            }
        }
    }
}

@Composable
fun CompactCurrentTrackFrame(
    currentTrack: AudioTrack?, player: AudioPlayer<AudioTrack>, modifier: Modifier = Modifier
) {
    val playMode by player.playMode
    val crossfade by player.crossfade

    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == AudioPlayer.PlayState.PLAYING) updateProgress()
            delay(1000)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(modifier.hoverable(interactionSource), contentAlignment = Alignment.Center) {

        val targetValue = remember(
            currentTrack, isHovered, progress
        ) { if (isHovered) 1f else progress }
        val floatAnimation by animateFloatAsState(targetValue)

        val targetProgressColor: Color =
            remember(isHovered) { if (isHovered) Colors.barsTransparent else Colors.currentYukiTheme.progressOverThumbnail }
        val aniColor = animateColorAsState(targetProgressColor)

        TrackThumbnail(
            currentTrack,
            player,
            updateProgress,
            floatAnimation,
            progressColor = aniColor.value,
            modifier = Modifier.padding(8.dp).size(145.dp)
        )

        AnimatedVisibility(
            isHovered,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.height(144.dp).width(153.dp).clip(
                RoundedCornerShape(12.dp)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                //modifier = Modifier.let { if (currentTrack != null) it.background(Colors.barsTransparent) else it },
            ) {
                Spacer(Modifier.height(16.dp))

                PlaybackOptionsButtons(player, Modifier.width(133.dp))

                PlaybackButtons(
                    player = player, Modifier.width(133.dp)
                )

                Box {
                    TrackProgressIndicator(
                        currentTrack,
                        player,
                        updateProgress,
                        progress,
                        color = Colors.currentYukiTheme.thinBorder,
                        textColor = Colors.currentYukiTheme.thinBorder,
                        Modifier.graphicsLayer(translationY = 2f)
                    )
                    TrackProgressIndicator(currentTrack, player, updateProgress, progress)
                }
            }
        }
    }
}

fun getCropParameters(original: ImageBitmap): Pair<IntOffset, IntSize> {
    val width = original.width
    val height = original.height

    var left = width
    var right = 0
    var top = height
    var bottom = 0

    val pixels = IntArray(width * height)
    original.readPixels(pixels)

//    val topColor = Color(pixels[width / 2 ])
    val leftColor = Color(pixels[(height / 2) * width])

    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = pixels[y * width + x]
            val color = Color(pixel)
            val isHorizontalBarsColor = false //abs((color.red * 255 + color.green * 255 + color.blue * 255) - (leftColor.red * 255 + leftColor.green * 255 + leftColor.blue * 255)) < 25
            val isVerticalBarsColor = color.red * 255 + color.green * 255 + color.blue * 255 > 120

            if (isHorizontalBarsColor || isVerticalBarsColor) { //0.47f
                if (x < left) left = x
                if (x > right) right = x
                if (y < top) top = y
                if (y > bottom) bottom = y
            }
        }
    }

    val offset = IntOffset(left, top)
    val size = IntSize(right - left + 1, bottom - top + 1)

    return Pair(offset, size)
}


@Composable
fun TrackThumbnail(
    currentTrack: AudioTrack?,
    player: AudioPlayer<AudioTrack>,
    updateProgress: () -> Unit,
    progress: Float,
    progressColor: Color = Colors.currentYukiTheme.progressOverThumbnail,
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
            getThumbnail(currentTrack)?.let { thumbnail ->
                val crop = getCropParameters(thumbnail)
                size = crop.second
                offset = crop.first

                thumbnail
            }
        } else {
            null
        }
        loadingThumb = false
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier.drawBehind {
            drawRoundRect(
                color = Colors.currentYukiTheme.thinBorder,
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
                        modifier = Modifier.fillMaxSize().let { if (blur) it.blur(5.dp) else it }
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
                                filterQuality = DefaultFilterQuality,
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
