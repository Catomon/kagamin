package chu.monscout.kagamin.feature

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaTrack
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.def_thumb
import kagamin.composeapp.generated.resources.fade
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_single
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

expect fun getThumbnail(denpaTrack: DenpaTrack): ImageBitmap?

@Composable
fun CurrentTrackFrame(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    modifier: Modifier = Modifier
) {
    val playMode by player.playMode
    val fade by player.fade

    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE)
                player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == DenpaPlayer.PlayState.PLAYING)
                updateProgress()
            delay(1000)
        }
    }

    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TrackThumbnail(currentTrack, player, updateProgress, progress)

            PlaybackButtons(
                player = player,
                Modifier.fillMaxWidth()
            )

            PlaybackOptionsButtons(player)

            var volume by player.volume
            VolumeSlider(
                volume,
                { newVolume -> volume = newVolume; player.setVolume(newVolume) },
                Modifier.fillMaxWidth().padding(horizontal = 18.dp)
            )
        }
    }
}

@Composable
fun CompactCurrentTrackFrame(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    modifier: Modifier = Modifier
) {
    val playMode by player.playMode
    val fade by player.fade

    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE)
                player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == DenpaPlayer.PlayState.PLAYING)
                updateProgress()
            delay(1000)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(modifier.hoverable(interactionSource), contentAlignment = Alignment.Center) {

        val targetValue = remember(
            currentTrack,
            isHovered,
            progress
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
            progressColor = aniColor.value
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

                PlaybackOptionsButtons(player)

                PlaybackButtons(
                    player = player,
                    Modifier.fillMaxWidth()
                )

                var volume by player.volume
                VolumeSlider(
                    volume,
                    { newVolume -> volume = newVolume; player.setVolume(newVolume) },
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                )
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

    for (y in 0 until height) {
        for (x in 0 until width) {
            if (pixels[y * width + x] != Color.Black.toArgb()) {
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
private fun TrackThumbnail(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    updateProgress: () -> Unit,
    progress: Float,
    progressColor: Color = Colors.currentYukiTheme.progressOverThumbnail
) {
    //val progressColor = remember { Colors.bars.copy(0.5f) }

    var image by remember(currentTrack) {
        mutableStateOf<ImageBitmap?>(null)
    }
    var loadingThumb by remember(currentTrack) { mutableStateOf(true) }
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(160.dp).padding(8.dp)
            .clip(
                RoundedCornerShape(12.dp)
            ).pointerInput(currentTrack) {
                if (currentTrack == null) return@pointerInput
                val width = this.size.width
                detectTapGestures {
                    player.seek((currentTrack.duration * (it.x / width)).toLong())
                    updateProgress()
                }
            }
    ) {
        AnimatedContent(image, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))) {
            if (!loadingThumb && image == null) { //this still executes idc
                Box(
                    Modifier.fillMaxSize()
                ) {
                    Image(
                        painterResource(Res.drawable.def_thumb),
                        "Track thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            } else {
                if (loadingThumb || image == null) {
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
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        if (progress >= 0)
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxHeight().let {
                        if (progress > 0) it.weight(progress) else it
                    }.background(
                        progressColor
                    )
                ) { }

                Box(
                    modifier = Modifier.fillMaxHeight().let {
                        val weight =
                            1f - progress; if (weight > 0) it.weight(weight) else it
                    }) { }
            }
    }
}

@Composable
private fun PlaybackOptionsButtons(
    player: DenpaPlayer<DenpaTrack>
) {
    var fade by player.fade
    var playMode by player.playMode
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton({
            fade = !fade
        }, modifier = Modifier.size(32.dp)) {
            Image(
                painterResource(Res.drawable.fade),
                "crossfade",
                colorFilter =
                if (fade)
                    ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
                else
                    ColorFilter.tint(Colors.currentYukiTheme.playerButtonIconTransparent)
            )
        }

        IconButton({
            playMode =
                if (playMode != DenpaPlayer.PlayMode.REPEAT_TRACK)
                    DenpaPlayer.PlayMode.REPEAT_TRACK
                else
                    DenpaPlayer.PlayMode.REPEAT_PLAYLIST
        }, modifier = Modifier.size(32.dp)) {
            Image(
                painterResource(Res.drawable.repeat_single),
                "repeat track",
                colorFilter =
                if (playMode == DenpaPlayer.PlayMode.REPEAT_TRACK)
                    ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
                else
                    ColorFilter.tint(Colors.currentYukiTheme.playerButtonIconTransparent)
            )
        }

        IconButton({
            player.playMode.value =
                if (playMode != DenpaPlayer.PlayMode.RANDOM)
                    DenpaPlayer.PlayMode.RANDOM
                else
                    DenpaPlayer.PlayMode.REPEAT_PLAYLIST
        }, modifier = Modifier.size(32.dp)) {
            Image(
                painterResource(Res.drawable.random),
                "random mode",
                colorFilter =
                if (playMode == DenpaPlayer.PlayMode.RANDOM)
                    ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
                else
                    ColorFilter.tint(Colors.currentYukiTheme.playerButtonIconTransparent)
            )
        }
    }
}

@Composable
private fun TrackInfoText(track: DenpaTrack?, modifier: Modifier = Modifier) {
    val text = remember(track) {
        """
        |${track?.name}
        """.trimMargin()
    }

    Text(
        text, fontSize = 10.sp,
        modifier = modifier.verticalScroll(rememberScrollState()),
        color = Colors.currentYukiTheme.playerButtonIcon
    )
}
