package chu.monscout.kagamin.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.audio.DenpaPlayer
import chu.monscout.kagamin.audio.DenpaTrack
import chu.monscout.kagamin.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.prev
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaybackButtons(player: DenpaPlayer<DenpaTrack>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(48.dp),//.background(Colors.noteBackground.copy(alpha = 0.75f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                player.prevTrack()
            }
        ) {
            ImageWithShadow(
                painterResource(Res.drawable.prev),
                "Previous",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
            )
        }

        IconButton(
            modifier = Modifier.size(40.dp),
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
                    ImageWithShadow(
                        painterResource(Res.drawable.play),
                        "Play",
                        modifier = Modifier.size(40.dp),
                        colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
                    )
                } else {
                    ImageWithShadow(
                        painterResource(Res.drawable.pause),
                        "Pause",
                        modifier = Modifier.size(40.dp),
                        colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
                    )
                }
            }
        }

        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = {
                player.nextTrack()
            }
        ) {
            ImageWithShadow(
                painterResource(Res.drawable.next),
                "Next",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Colors.currentYukiTheme.playerButtonIcon)
            )
        }
    }
}
