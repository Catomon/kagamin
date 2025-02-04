package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import audio.DenpaPlayer
import audio.DenpaTrack
import com.github.catomon.yukinotes.feature.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.prev
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaybackButtons(player: DenpaPlayer<DenpaTrack>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(66.dp),//.background(Colors.noteBackground.copy(alpha = 0.75f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            modifier = Modifier.size(50.dp).padding(2.dp).clip(shape = RoundedCornerShape(12.dp))
                .background(color = Colors.noteBackground).padding(0.dp),
            onClick = {
                player.prevTrack()
            }
        ) {
            Image(
                painterResource(Res.drawable.prev),
                "Previous",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(Colors.background)
            )
        }

        TextButton(
            modifier = Modifier.size(66.dp).padding(2.dp).clip(shape = RoundedCornerShape(12.dp))
                .background(color = Colors.noteBackground).padding(0.dp),
            onClick = {
                when (player.playState.value) {
                    DenpaPlayer.PlayState.PLAYING -> player.pause()
                    DenpaPlayer.PlayState.PAUSED -> player.resume()
                    DenpaPlayer.PlayState.IDLE -> player.resume()
                }
            }
        ) {
            AnimatedContent(player.playState) {
                if (it.value != DenpaPlayer.PlayState.PLAYING) {
                    Image(
                        painterResource(Res.drawable.play),
                        "Play",
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(Colors.background)
                    )
                } else {
                    Image(
                        painterResource(Res.drawable.pause),
                        "Pause",
                        modifier = Modifier.size(64.dp),
                        colorFilter = ColorFilter.tint(Colors.background)
                    )
                }
            }
        }

        TextButton(
            modifier = Modifier.size(50.dp).padding(2.dp).clip(shape = RoundedCornerShape(12.dp))
                .background(color = Colors.noteBackground).padding(0.dp),
            onClick = {
                player.nextTrack()
            }
        ) {
            Image(
                painterResource(Res.drawable.next),
                "Next",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(Colors.background)
            )
        }
    }
}