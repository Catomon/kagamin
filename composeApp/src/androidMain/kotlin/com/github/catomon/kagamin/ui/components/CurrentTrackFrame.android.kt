package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import kotlinx.coroutines.delay

@Composable
fun CurrentTrackFrame2(
    thumbnail: ImageBitmap?,
    currentTrack: AudioTrack?,
    player: AudioPlayer<AudioTrack>,
    modifier: Modifier = Modifier
) {
    val playMode by player.playMode
    val crossfade by player.crossfade

    var progress by remember { mutableFloatStateOf(-1f) }
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

    Box(modifier, contentAlignment = Alignment.Center) {
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
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(300.dp)
            )

            Spacer(Modifier.weight(0.99f))

            TrackProgressIndicator(
                currentTrack, player, updateProgress, progress,
                fontSize = 20.sp, modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .width(300.dp)
            )

            PlaybackButtons(
                player = player, Modifier
                    .width(300.dp).padding(vertical = 12.dp), buttonsSize = 48.dp
            )

//            PlaybackOptionsButtons(
//                player,
//                Modifier
//                    .width(300.dp),
//                buttonsSize = 48.dp
//            )
        }
    }
}