package com.github.catomon.kagamin.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.ui.theme.Colors
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kotlinx.coroutines.delay

@Composable
fun CurrentTrackFrame(
    viewModel: KagaminViewModel,
    thumbnail: ImageBitmap?,
    currentTrack: AudioTrack?, player: AudioPlayer<AudioTrack>, modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == AudioPlayer.PlayState.PLAYING) updateProgress()
            delay(250)
        }
    }

    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TrackThumbnail(
                thumbnail,
                onSetProgress = {
                    if (currentTrack != null) {
                        player.seek((currentTrack.duration * it).toLong())
                        updateProgress()
                    }
                },
                progress,
                modifier = Modifier.padding(8.dp).size(145.dp)
            )

            PlaybackButtons(
                player = player, Modifier.width(133.dp)
            )

            PlaybackOptionsButtons(player, Modifier.width(133.dp))

//            TrackProgressIndicator(
//                currentTrack,
//                player,
//                updateProgress,
//                progress,
//                modifier = Modifier.padding(horizontal = 20.dp)
//            )

            SongOptionsButtons(
                modifier = Modifier.width(133.dp),
                viewModel = viewModel,
            )
        }
    }
}

@Composable
fun CompactCurrentTrackFrame(
    thumbnail: ImageBitmap?,
    currentTrack: AudioTrack?,
    player: AudioPlayer<AudioTrack>,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == AudioPlayer.PlayState.PLAYING) updateProgress()
            delay(1000)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(modifier.hoverable(interactionSource), contentAlignment = Alignment.Center) {

        val targetValue = remember(
            currentTrack, isHovered, progress
        ) { if (isHovered) 1f else progress }
        val floatAnimation by animateFloatAsState(targetValue)

        val targetProgressColor: Color =
            remember(isHovered) { if (isHovered) Colors.backgroundTransparent else Colors.theme.thumbnailProgressIndicator }
        val aniColor = animateColorAsState(targetProgressColor)

        TrackThumbnail(
            thumbnail,
          onSetProgress = {
              if (currentTrack != null) {
                  player.seek((currentTrack.duration * it).toLong())
                  updateProgress()
              }
          },
            floatAnimation,
            progressColor = aniColor.value,
            modifier = Modifier.padding(8.dp).size(145.dp)
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

                PlaybackOptionsButtons(player, Modifier.width(133.dp))

                PlaybackButtons(
                    player = player, Modifier.width(133.dp)
                )

                TrackProgressIndicator(
                    currentTrack,
                    player,
                    updateProgress,
                    progress,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}
