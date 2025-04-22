package chu.monscout.kagamin.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.audio.AudioTrackAndy
import chu.monscout.kagamin.ui.theme.Colors
import kotlinx.coroutines.delay

actual fun getThumbnail(audioTrack: AudioTrack): ImageBitmap? {
    val artworkData = (audioTrack as AudioTrackAndy).mediaItem?.mediaMetadata?.artworkData ?: return null
    if (artworkData != null) {
        val bitmap = BitmapFactory.decodeByteArray(artworkData, 0, artworkData.size)
        return bitmap?.asImageBitmap()
    }

    return null
}

@Composable
fun CurrentTrackFrame2(
    currentTrack: AudioTrack?, player: AudioPlayer<AudioTrack>, modifier: Modifier = Modifier
) {
    val playMode by player.playMode
    val crossfade by player.crossfade

    var progress by remember { mutableStateOf(-1f) }
    val updateProgress = {
        progress = when (currentTrack) {
            null -> 0f
            else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) player.position.toFloat() / currentTrack.duration else -1f
        }
    }

    val pad = with(LocalDensity.current) { 2.dp.toPx() }

    LaunchedEffect(currentTrack) {
        while (true) {
            if (player.playState.value == AudioPlayer.PlayState.PLAYING) updateProgress()
            delay(1000)
        }
    }

    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TrackThumbnail(
                currentTrack,
                player,
                updateProgress,
                progress,
                modifier = Modifier
                    .padding(8.dp)
                    .size(200.dp)
            )

            PlaybackButtons(
                player = player, Modifier.fillMaxWidth(), buttonsSize = 64.dp
            )

            PlaybackOptionsButtons(player, Modifier.fillMaxWidth(), buttonsSize = 48.dp)

            Spacer(Modifier.weight(0.50f))

            TrackProgressIndicator(
                currentTrack, player, updateProgress, progress,
                fontSize = 20.sp, modifier = Modifier.padding(horizontal = 15.dp)
            )
        }
    }
}