package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.Colors
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaTrack
import kagamin.composeapp.generated.resources.Res
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
        TrackThumbnail(currentTrack, player, updateProgress, progress)

        AnimatedVisibility(
            isHovered || currentTrack == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.height(144.dp).width(153.dp).clip(
                RoundedCornerShape(12.dp)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.let { if (currentTrack != null) it.background(Colors.barsTransparent) else it },
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

@Composable
private fun TrackThumbnail(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    updateProgress: () -> Unit,
    progress: Float
) {
    val progressColor = remember { Colors.bars.copy(0.5f) }

    var image by remember(currentTrack) {
        mutableStateOf<ImageBitmap?>(null)
    }
    var loadingThumb by remember { mutableStateOf(true) }
    LaunchedEffect(currentTrack) {
        image = if (currentTrack != null) getThumbnail(currentTrack) else null
        loadingThumb = false
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.height(160.dp).padding(8.dp)
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
        AnimatedContent(image, modifier = Modifier.fillMaxSize()) {
            if (!loadingThumb && image == null) { //this still executes idc
                Box(
                    Modifier.fillMaxSize()
                ) {}
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
                                filterQuality = DefaultFilterQuality
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
