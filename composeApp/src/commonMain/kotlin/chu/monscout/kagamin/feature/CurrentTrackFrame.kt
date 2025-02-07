package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import audio.DenpaPlayer
import audio.DenpaTrack
import com.github.catomon.yukinotes.feature.Colors
import com.mpatric.mp3agic.Mp3File
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin1000
import kagamin.composeapp.generated.resources.playlist
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_all
import kagamin.composeapp.generated.resources.repeat_single
import kagamin.composeapp.generated.resources.single
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image

@Composable
fun CurrentTrackFrame(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    modifier: Modifier = Modifier
) {

    val playMode by player.playMode

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
                    modifier = Modifier.height(184.dp).padding(8.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        ),
                    contentScale = ContentScale.Crop,
                )

                PlaybackButtons(
                    player = player,
                    Modifier.fillMaxWidth()
                )

                TextButton(
                    {
                        when (playMode) {
                            DenpaPlayer.PlayMode.ONCE -> {
                                player.playMode.value = DenpaPlayer.PlayMode.REPEAT_TRACK

                            }

                            DenpaPlayer.PlayMode.REPEAT_TRACK -> {
                                player.playMode.value = DenpaPlayer.PlayMode.PLAYLIST

                            }

                            DenpaPlayer.PlayMode.PLAYLIST -> {
                                player.playMode.value = DenpaPlayer.PlayMode.REPEAT_PLAYLIST

                            }

                            DenpaPlayer.PlayMode.REPEAT_PLAYLIST -> {
                                player.playMode.value = DenpaPlayer.PlayMode.RANDOM
                            }

                            DenpaPlayer.PlayMode.RANDOM -> {
                                player.playMode.value = DenpaPlayer.PlayMode.ONCE
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp).padding(2.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(color = Colors.noteBackground).padding(0.dp)
                ) {
                    AnimatedContent(playMode, modifier = Modifier.size(32.dp)) {
                        when (it) {
                            DenpaPlayer.PlayMode.ONCE -> {
                                Image(
                                    painterResource(Res.drawable.single),
                                    null,
                                    modifier = Modifier.size(32.dp),
                                    colorFilter = ColorFilter.tint(Colors.background)
                                )
                            }

                            DenpaPlayer.PlayMode.REPEAT_TRACK -> {
                                Image(
                                    painterResource(Res.drawable.repeat_single),
                                    null,
                                    modifier = Modifier.size(32.dp),
                                    colorFilter = ColorFilter.tint(Colors.background)
                                )
                            }

                            DenpaPlayer.PlayMode.PLAYLIST -> {
                                Image(
                                    painterResource(Res.drawable.playlist),
                                    null,
                                    modifier = Modifier.size(32.dp),
                                    colorFilter = ColorFilter.tint(Colors.background)
                                )
                            }

                            DenpaPlayer.PlayMode.REPEAT_PLAYLIST -> {
                                Image(
                                    painterResource(Res.drawable.repeat_all),
                                    null,
                                    modifier = Modifier.size(32.dp),
                                    colorFilter = ColorFilter.tint(Colors.background)
                                )
                            }

                            DenpaPlayer.PlayMode.RANDOM -> {
                                Image(
                                    painterResource(Res.drawable.random),
                                    null,
                                    modifier = Modifier.size(32.dp),
                                    colorFilter = ColorFilter.tint(Colors.background)
                                )
                            }
                        }
                    }
                }

                VolumeSlider(
                    { volume -> player.setVolume(volume) },
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