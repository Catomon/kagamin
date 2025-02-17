package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    var playMode by player.playMode
    var fade by player.fade

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
            var image by remember(currentTrack) {
                mutableStateOf<ImageBitmap?>(null)
            }
            var loadingThumb by remember { mutableStateOf(true) }
            LaunchedEffect(currentTrack) {
                image = if (currentTrack != null) getThumbnail(currentTrack) else null
                loadingThumb = false
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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
                                    val weight = progress; if (weight > 0) it.weight(weight) else it
                                }.background(remember {
                                    Colors.bars.copy(0.5f)
                                })
                            ) { }

                            Box(
                                modifier = Modifier.fillMaxHeight().let {
                                    val weight =
                                        1f - progress; if (weight > 0) it.weight(weight) else it
                                }) { }
                        }
                }

                PlaybackButtons(
                    player = player,
                    Modifier.fillMaxWidth()
                )

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
                                ColorFilter.tint(Colors.noteBackground)
                            else
                                ColorFilter.tint(Colors.background)
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
                                ColorFilter.tint(Colors.noteBackground)
                            else
                                ColorFilter.tint(Colors.background)
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
                                ColorFilter.tint(Colors.noteBackground)
                            else
                                ColorFilter.tint(Colors.background)
                        )
                    }
                }

                var volume by player.volume
                VolumeSlider(
                    volume,
                    { newVolume -> volume = newVolume; player.setVolume(newVolume) },
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp)
                )

                //TrackInfoText(currentTrack, Modifier.padding(horizontal = 16.dp))
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
        color = Colors.noteBackground
    )
}
