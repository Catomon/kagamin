package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.prev
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaybackButtons(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp
) {
    val playState by viewModel.playState.collectAsState()

    Row(
        modifier = modifier.height(buttonsSize * 1.5f),//.background(Colors.noteBackground.copy(alpha = 0.75f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(buttonsSize),
            onClick = {
                viewModel.prevTrack()
            }
        ) {
            ImageWithShadow(
                painterResource(Res.drawable.prev),
                "Previous",
                modifier = Modifier.size(buttonsSize),
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
            )
        }

        IconButton(
            modifier = Modifier.size(buttonsSize * 1.25f),
            onClick = {
                viewModel.onPlayPause()
            }
        ) {
            AnimatedContent(playState) { playState ->
                if (playState != AudioPlayerService.PlayState.PLAYING) {
                    ImageWithShadow(
                        painterResource(Res.drawable.play),
                        "Play",
                        modifier = Modifier.size(buttonsSize * 1.25f),
                        colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
                    )
                } else {
                    ImageWithShadow(
                        painterResource(Res.drawable.pause),
                        "Pause",
                        modifier = Modifier.size(buttonsSize * 1.25f),
                        colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
                    )
                }
            }
        }

        IconButton(
            modifier = Modifier.size(buttonsSize),
            onClick = {
                viewModel.nextTrack()
            }
        ) {
            ImageWithShadow(
                painterResource(Res.drawable.next),
                "Next",
                modifier = Modifier.size(buttonsSize),
                colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
            )
        }
    }
}
