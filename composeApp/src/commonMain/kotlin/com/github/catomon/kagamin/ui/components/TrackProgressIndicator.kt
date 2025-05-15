package com.github.catomon.kagamin.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.theme.KagaminTheme

//@Composable
//fun TrackProgressIndicator(
//    currentTrack: AudioTrack?,
//    updateProgress: () -> Unit,
//    progress: Float,
//    color: Color = KagaminTheme.colors.buttonIcon,
//    textColor: Color = KagaminTheme.colors.buttonIconTransparent,
//    modifier: Modifier = Modifier,
//    fontSize: TextUnit = 10.sp,
//) {
//
//    val timeTextPad = -(with(LocalDensity.current) { (3 * (10f / fontSize.value)).dp.toPx() })
//    val timePastText = formatTime(currentTrack?.let { player.position })
//    val trackDurationText = formatTime(currentTrack?.duration)
//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier.pointerHoverIcon(
//            PointerIcon.Hand
//        ).pointerInput(currentTrack) {
//            if (currentTrack == null) return@pointerInput
//            val width = this.size.width
//            detectTapGestures {
//                player.seek((currentTrack.duration * (it.x / width)).toLong())
//                updateProgress()
//            }
//        }) {
//
//        Box {
//            LinearProgressIndicator(
//                progress = progress,
//                color = KagaminTheme.colors.thinBorder,
//                trackColor = KagaminTheme.colors.buttonIcon,
//                strokeCap = StrokeCap.Round,
//                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
//                    .graphicsLayer(translationY = 2f)
//            )
//
//            LinearProgressIndicator(
//                progress = progress,
//                Modifier.fillMaxWidth().padding(top = 8.dp),
//                color = color,
//                trackColor = KagaminTheme.colors.buttonIcon,
//                strokeCap = StrokeCap.Round
//            )
//        }
//
//        Row(
//            Modifier.fillMaxWidth().graphicsLayer(translationY = timeTextPad),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                timePastText, fontSize = fontSize, color = KagaminTheme.colors.buttonIcon
//            )
//
//            Text(
//                trackDurationText, fontSize = fontSize, color = KagaminTheme.colors.buttonIcon
//            )
//        }
//    }
//}

@Composable
fun TrackProgressIndicator(
    currentTrack: AudioTrack?,
    seek: (Long) -> Unit,
    progress: Float,
    color: Color = KagaminTheme.colors.buttonIcon,
    modifier: Modifier = Modifier,
) {
        LinearProgressIndicator(
            progress = progress,
            modifier.fillMaxWidth().padding().pointerHoverIcon(
                PointerIcon.Hand
            ).pointerInput(currentTrack) {
                if (currentTrack == null) return@pointerInput
                val width = this.size.width
                detectTapGestures {
                    seek((currentTrack.duration * (it.x / width)).toLong())
                }
            },
            color = color,
            trackColor = KagaminTheme.colors.thinBorder,
            strokeCap = StrokeCap.Round
        )
}