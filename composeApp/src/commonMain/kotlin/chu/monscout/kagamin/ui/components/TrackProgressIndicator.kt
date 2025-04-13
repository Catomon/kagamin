package chu.monscout.kagamin.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack

@Composable
fun TrackProgressIndicator(
    currentTrack: AudioTrack?,
    player: AudioPlayer<AudioTrack>,
    updateProgress: () -> Unit,
    progress: Float,
    color: Color = Colors.theme.playerButtonIcon,
    textColor: Color = Colors.theme.playerButtonIconTransparent,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 10.sp
) {

    val pad = with(LocalDensity.current) { (8 * (10f / fontSize.value)).dp.toPx() }

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .pointerHoverIcon(
                PointerIcon.Hand
            ).pointerInput(currentTrack) {
                if (currentTrack == null) return@pointerInput
                val width = this.size.width
                detectTapGestures {
                    player.seek((currentTrack.duration * (it.x / width)).toLong())
                    updateProgress()
                }
            }) {
        LinearProgressIndicator(
            progress = progress,
            Modifier.fillMaxWidth().padding(top = 16.dp).height(6.dp),
            color = color,
            strokeCap = StrokeCap.Round
        )

        Row(
            Modifier.fillMaxWidth().graphicsLayer(translationY = -pad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                (player.position / 1000L / 60L).toString() + ":" + (player.position / 1000L % 60),
                fontSize = fontSize,
                color = textColor
            )
//                    Text(" - ", fontSize = 10.sp, color = Colors.currentYukiTheme.playerButtonIconTransparent)
            Text(
                ((currentTrack?.duration
                    ?: 0L) / 1000L / 60L).toString() + ":" + ((currentTrack?.duration
                    ?: 0L) / 1000L % 60),
                fontSize = fontSize,
                color = textColor
            )
        }
    }
}