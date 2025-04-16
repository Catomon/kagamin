package chu.monscout.kagamin.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.ui.util.formatTime

@Composable
fun TrackProgressIndicator(
    currentTrack: AudioTrack?,
    player: AudioPlayer<AudioTrack>,
    updateProgress: () -> Unit,
    progress: Float,
    color: Color = Colors.theme.buttonIcon,
    textColor: Color = Colors.theme.buttonIconTransparent,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 10.sp
) {

    val timeTextPad = with(LocalDensity.current) { (6 * (10f / fontSize.value)).dp.toPx() }
    val timePastText = formatTime(currentTrack?.let { player.position })
    val trackDurationText = formatTime(currentTrack?.duration)

    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.pointerHoverIcon(
            PointerIcon.Hand
        ).pointerInput(currentTrack) {
            if (currentTrack == null) return@pointerInput
            val width = this.size.width
            detectTapGestures {
                player.seek((currentTrack.duration * (it.x / width)).toLong())
                updateProgress()
            }
        }) {

        Box {
            LinearProgressIndicator(
                progress = progress,
                color = Colors.theme.thinBorder,
                strokeCap = StrokeCap.Round,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    .graphicsLayer(translationY = 2f)
            )

            LinearProgressIndicator(
                progress = progress,
                Modifier.fillMaxWidth().padding(top = 16.dp),
                color = color,
                strokeCap = StrokeCap.Round
            )
        }

        Row(
            Modifier.fillMaxWidth().graphicsLayer(translationY = -timeTextPad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                timePastText, fontSize = fontSize, color = Colors.theme.buttonIcon
            )

            Text(
                trackDurationText, fontSize = fontSize, color = Colors.theme.buttonIcon
            )
        }
    }
}