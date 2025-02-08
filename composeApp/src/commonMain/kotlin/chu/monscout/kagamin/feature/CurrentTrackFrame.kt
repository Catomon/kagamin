package chu.monscout.kagamin.feature

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
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
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.audio.DenpaTrack
import chu.monscout.kagamin.audio.DenpaPlayer
import com.github.catomon.yukinotes.feature.Colors
import com.mpatric.mp3agic.Mp3File
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.fade
import kagamin.composeapp.generated.resources.kagamin1000
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_single
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image

@Composable
fun CurrentTrackFrame(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    modifier: Modifier = Modifier
) {

    var playMode by player.playMode
    var fade by player.fade

    var progress by remember { mutableStateOf(0f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE)
                player.position.toFloat() / currentTrack.duration else 1f
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
        if (currentTrack == null) {
            Image(
                painterResource(Res.drawable.kagamin1000),
                "Track thumbnail",
                modifier = modifier,
                contentScale = ContentScale.Crop,
            )
        } else {
            val image = remember(currentTrack) {
                try {
                    val file = Mp3File(currentTrack.uri)
                    file.id3v2Tag.albumImage?.let { albumImage ->
                        Image.makeFromEncoded(albumImage)
                            .toComposeImageBitmap()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

//                Image(
//                    image,
//                    "Background track thumbnail",
//                    modifier = Modifier.fillMaxSize().align(Alignment.Center).blur(16.dp).graphicsLayer(1.35f, 1.35f).alpha(0.75f),
//                    contentScale = ContentScale.Crop,
//                )


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(184.dp).padding(8.dp)
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
                    Image(
                        if (image != null)
                            remember(image) {
                                BitmapPainter(
                                    image,
                                    filterQuality = DefaultFilterQuality
                                )
                            }
                        else
                            painterResource(Res.drawable.kagamin1000),
                        "Track thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxHeight().let { val weight = progress; if (weight > 0) it.weight(weight) else it }.background(remember {
                            Colors.bars.copy(0.5f)
                        })) { }

                        Box(modifier = Modifier.fillMaxHeight().let { val weight = 1f - progress; if (weight > 0) it.weight(weight) else it }) { }
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
                            "fade in/out",
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
//
//                IconButton(
//                    {
//                        when (playMode) {
//                            DenpaPlayer.PlayMode.ONCE -> {
//                                player.playMode.value = DenpaPlayer.PlayMode.REPEAT_TRACK
//
//                            }
//
//                            DenpaPlayer.PlayMode.REPEAT_TRACK -> {
//                                player.playMode.value = DenpaPlayer.PlayMode.PLAYLIST
//
//                            }
//
//                            DenpaPlayer.PlayMode.PLAYLIST -> {
//                                player.playMode.value = DenpaPlayer.PlayMode.REPEAT_PLAYLIST
//
//                            }
//
//                            DenpaPlayer.PlayMode.REPEAT_PLAYLIST -> {
//                                player.playMode.value = DenpaPlayer.PlayMode.RANDOM
//                            }
//
//                            DenpaPlayer.PlayMode.RANDOM -> {
//                                player.playMode.value = DenpaPlayer.PlayMode.ONCE
//                            }
//                        }
//                    },
//                    modifier = Modifier.size(32.dp)
//                ) {
//                    AnimatedContent(playMode, modifier = Modifier.size(32.dp)) {
//                        when (it) {
//                            DenpaPlayer.PlayMode.ONCE -> {
//                                Image(
//                                    painterResource(Res.drawable.single),
//                                    null,
//                                    modifier = Modifier.size(32.dp),
//                                    colorFilter = ColorFilter.tint(Colors.noteBackground)
//                                )
//                            }
//
//                            DenpaPlayer.PlayMode.REPEAT_TRACK -> {
//                                Image(
//                                    painterResource(Res.drawable.repeat_single),
//                                    null,
//                                    modifier = Modifier.size(32.dp),
//                                    colorFilter = ColorFilter.tint(Colors.noteBackground)
//                                )
//                            }
//
//                            DenpaPlayer.PlayMode.PLAYLIST -> {
//                                Image(
//                                    painterResource(Res.drawable.playlist),
//                                    null,
//                                    modifier = Modifier.size(32.dp),
//                                    colorFilter = ColorFilter.tint(Colors.noteBackground)
//                                )
//                            }
//
//                            DenpaPlayer.PlayMode.REPEAT_PLAYLIST -> {
//                                Image(
//                                    painterResource(Res.drawable.repeat_all),
//                                    null,
//                                    modifier = Modifier.size(32.dp),
//                                    colorFilter = ColorFilter.tint(Colors.noteBackground)
//                                )
//                            }
//
//                            DenpaPlayer.PlayMode.RANDOM -> {
//                                Image(
//                                    painterResource(Res.drawable.random),
//                                    null,
//                                    modifier = Modifier.size(32.dp),
//                                    colorFilter = ColorFilter.tint(Colors.noteBackground)
//                                )
//                            }
//                        }
//                    }
//                }

                var volume by player.volume
                VolumeSlider(
                    volume,
                    { newVolume -> volume = newVolume; player.setVolume(newVolume) },
                    Modifier.fillMaxWidth().padding(8.dp)
                )
            }
//                    Image(
//                        image,
//                        "Track thumbnail",
//                        modifier = Modifier.height(184.dp).padding(8.dp).align(Alignment.TopCenter).clip(
//                            RoundedCornerShape(12.dp)
//                        ),
//                        contentScale = ContentScale.Crop,
//                    )


        }

//        Image(
//            painterResource(Res.drawable.kagamin1000),
//            "Track thumbnail",
//            modifier = Modifier.fillMaxSize().align(Alignment.Center),
//            contentScale = ContentScale.Crop,
//        )

//        PlaybackButtons(player = player, Modifier.fillMaxWidth().align(Alignment.BottomCenter))


    }
}