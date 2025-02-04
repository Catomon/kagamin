package chu.monscout.kagamin.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import audio.DenpaPlayer
import audio.DenpaTrack
import com.mpatric.mp3agic.Mp3File
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.kagamin1000
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.skia.Image

@Composable
fun CurrentTrackFrame(
    currentTrack: DenpaTrack?,
    player: DenpaPlayer<DenpaTrack>,
    modifier: Modifier = Modifier
) {
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