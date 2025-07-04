package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.next
import kagamin.composeapp.generated.resources.pause_star
import kagamin.composeapp.generated.resources.play_star
import kagamin.composeapp.generated.resources.prev
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_single
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(
    trackName: String,
    isPlaying: Boolean,
    isRepeat: Boolean,
    isShuffle: Boolean,
    onPlayPauseClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        modifier = modifier.background(KagaminTheme.backgroundTransparent)
    ) {
        if (trackName.isNotEmpty())
        Text(
            text = trackName,
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis,
            color = KagaminTheme.colors.textSecondary,
            modifier = Modifier.Companion
                .padding(start = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        ) {
            IconButton(onRepeatClick, modifier = Modifier.size(32.dp)) {
                Icon(
                    painterResource(Res.drawable.repeat_single),
                    "Repeat",
                    tint = if (isRepeat)
                        KagaminTheme.colors.buttonIcon
                    else KagaminTheme.colors.disabled
                )
            }

            IconButton(
                onClick = onPrevClick
            ) {
                Icon(
                    painterResource(Res.drawable.prev),
                    "Previous",
                    modifier = Modifier.size(32.dp),
                    tint = KagaminTheme.colors.buttonIcon
                )
            }

            IconButton(
                onClick = onPlayPauseClick
            ) {
                AnimatedContent(isPlaying) { isPlaying ->
                    if (isPlaying) {
                        Icon(
                            painterResource(Res.drawable.pause_star),
                            "Pause",
                            modifier = Modifier.size(48.dp),
                            tint = KagaminTheme.colors.buttonIcon
                        )

                    } else {
                        Icon(
                            painterResource(Res.drawable.play_star),
                            "Play",
                            modifier = Modifier.size(48.dp),
                            tint = KagaminTheme.colors.buttonIcon
                        )
                    }
                }
            }

            IconButton(
                onClick = onNextClick
            ) {
                Icon(
                    painterResource(Res.drawable.next),
                    "Next",
                    modifier = Modifier.size(32.dp),
                    tint = KagaminTheme.colors.buttonIcon
                )
            }

            IconButton(onShuffleClick) {
                Icon(
                    painterResource(Res.drawable.random),
                    "Random",
                    modifier = Modifier.size(32.dp),
                    tint = if (isShuffle)
                        KagaminTheme.colors.buttonIcon
                    else KagaminTheme.colors.disabled
                )
            }
        }
    }
}
