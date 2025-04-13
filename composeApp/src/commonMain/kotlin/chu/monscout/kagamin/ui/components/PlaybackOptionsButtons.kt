package chu.monscout.kagamin.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.audio.AudioTrack
import chu.monscout.kagamin.ui.theme.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.random
import kagamin.composeapp.generated.resources.repeat_single
import kagamin.composeapp.generated.resources.volume
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaybackOptionsButtons(
    player: AudioPlayer<AudioTrack>,
    modifier: Modifier = Modifier,
    buttonsSize: Dp = 32.dp
) {
    var playMode by player.playMode
    var volume by player.volume
    var showVolumeSlider by remember { mutableStateOf(false) }
    var timer by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(showVolumeSlider, volume) {
        timer = 0f

        while (true) {
            timer += 0.1f
            if (timer >= 3f) {
                showVolumeSlider = false
            }

            delay(100)
        }
    }

    AnimatedContent(showVolumeSlider) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.height(buttonsSize)
        ) {
            IconButton({
                showVolumeSlider = !showVolumeSlider
            }, modifier = Modifier.size(buttonsSize).onFocusChanged { focusState ->
                showVolumeSlider = focusState.isFocused
            }.focusable().pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> {
                                showVolumeSlider = false
                                showVolumeSlider = true
                            }

                            PointerEventType.Exit -> {
//                            showVolumeSlider = false
                            }
                        }
                    }
                }
            }) {
                ImageWithShadow(
                    painterResource(Res.drawable.volume),
                    "volume",
                    colorFilter = if (showVolumeSlider) ColorFilter.tint(Colors.theme.playerButtonIcon)
                    else ColorFilter.tint(Colors.theme.playerButtonIconTransparent)
                )
            }

            if (it) {
                VolumeSlider(
                    volume,
                    { newVolume -> volume = newVolume; player.setVolume(newVolume) },
                    Modifier.fillMaxWidth()
                )
            }

            if (!it) {
                IconButton({
                    playMode =
                        if (playMode != AudioPlayer.PlayMode.REPEAT_TRACK) AudioPlayer.PlayMode.REPEAT_TRACK
                        else AudioPlayer.PlayMode.REPEAT_PLAYLIST
                }, modifier = Modifier.size(buttonsSize)) {
                    ImageWithShadow(
                        painterResource(Res.drawable.repeat_single),
                        "repeat track",
                        colorFilter = if (playMode == AudioPlayer.PlayMode.REPEAT_TRACK) ColorFilter.tint(
                            Colors.theme.playerButtonIcon
                        )
                        else ColorFilter.tint(Colors.theme.playerButtonIconTransparent)
                    )
                }

            }

            if (!it) {
                IconButton({
                    player.playMode.value =
                        if (playMode != AudioPlayer.PlayMode.RANDOM) AudioPlayer.PlayMode.RANDOM
                        else AudioPlayer.PlayMode.REPEAT_PLAYLIST
                }, modifier = Modifier.size(buttonsSize)) {
                    ImageWithShadow(
                        painterResource(Res.drawable.random),
                        "random mode",
                        colorFilter = if (playMode == AudioPlayer.PlayMode.RANDOM) ColorFilter.tint(
                            Colors.theme.playerButtonIcon
                        )
                        else ColorFilter.tint(Colors.theme.playerButtonIconTransparent)
                    )
                }
            }
        }
    }
}