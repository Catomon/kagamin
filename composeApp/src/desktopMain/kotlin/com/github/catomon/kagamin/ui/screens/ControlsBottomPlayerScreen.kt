package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.audio.AudioPlayerManager
import com.github.catomon.kagamin.ui.Menu
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.AddTrackOrPlaylistButton
import com.github.catomon.kagamin.ui.components.AppLogo
import com.github.catomon.kagamin.ui.components.CurrentTrackFrameHorizontal
import com.github.catomon.kagamin.ui.components.PlayPauseButton
import com.github.catomon.kagamin.ui.components.PlaybackModeToggleButton
import com.github.catomon.kagamin.ui.components.PrevNextTrackButtons
import com.github.catomon.kagamin.ui.components.VolumeOptions
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import com.github.catomon.kagamin.util.echoTraceFiltered
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.minimize_window
import kagamin.composeapp.generated.resources.star_angled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun ControlsBottomPlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    echoTrace { "ControlsBottomPlayerScreen" }

    val currentTrack by viewModel.currentTrack.collectAsState()
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val window = LocalWindow.current
    val coroutineScope = rememberCoroutineScope()
    val playMode by viewModel.playMode.collectAsState()

    var openMenu by remember { mutableStateOf(false) }

    Box(
        modifier.background(
            color = KagaminTheme.background
        )
    ) {
        Background(currentTrack, Modifier.fillMaxSize())

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier.background(KagaminTheme.backgroundTransparent).fillMaxWidth()
            ) {
                CurrentTrackFrameHorizontal(currentTrack, Modifier.padding(4.dp))

                IconButton({
                    window.isMinimized = true
                }, modifier = Modifier.height(16.dp).align(Alignment.TopEnd)) {
                    Icon(
                        painterResource(Res.drawable.minimize_window),
                        contentDescription = null,
                        tint = KagaminTheme.colors.buttonIcon
                    )
                }

                AnimatedPlayPauseButton(
                    coroutineScope,
                    viewModel,
                    Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Playlists(
                    viewModel,
                    Modifier.weight(0.35f).fillMaxHeight()
                )

                if (currentPlaylist.tracks.isEmpty()) {
                    Box(
                        Modifier
                            .weight(0.65f)
                            .fillMaxHeight()
                            .background(KagaminTheme.backgroundTransparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Drop files or folders here",
                            textAlign = TextAlign.Center,
                            color = KagaminTheme.textSecondary
                        )
                    }
                } else {
                    Tracklist(
                        viewModel,
                        Modifier.weight(0.65f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(color = KagaminTheme.backgroundTransparent),
                contentAlignment = Alignment.Center
            ) {
                AppLogo(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            openMenu = !openMenu
                        }
                        .align(Alignment.CenterStart)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    VolumeOptions(
                        volume = volume,
                        onVolumeChange = { newVolume ->
                            viewModel.setVolume(newVolume)
                        }
                    )

                    PlaybackModeToggleButton(playMode, {
                        viewModel.togglePlayMode()
                    })

                    PrevNextTrackButtons(viewModel)
                }
            }
        }

        AnimatedVisibility(openMenu, enter = fadeIn() + slideInHorizontally(), exit = fadeOut() + slideOutHorizontally()) {
            Box(Modifier.fillMaxSize().clickable(null, null) {
                openMenu = false
            }) {
                Menu(
                    currentTrack,
                    viewModel, navigateToSettings = {
                        if (navController.currentDestination?.route != SettingsDestination.toString())
                            navController.navigate(SettingsDestination.toString())
                    },
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 40.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedPlayPauseButton(
    coroutineScope: CoroutineScope,
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier
) {
    echoTraceFiltered { "AnimatedPlayPauseButton" }

    val flow by AudioPlayerManager.amplitudeChannel.receiveAsFlow().collectAsState(1f)
    val targetScaleAnimated by animateFloatAsState(
        1f + flow, animationSpec = tween(
            durationMillis = 100,
            easing = LinearEasing
        )
    )

    val starImage = imageResource(Res.drawable.star_angled)

    Box(
        modifier = modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()

                val targetScaleAnimated = targetScaleAnimated / 2
                val scaledWidth = starImage.width * targetScaleAnimated
                val scaledHeight = starImage.height * targetScaleAnimated

                val offsetX = (size.width - scaledWidth) / 2f
                val offsetY = (size.height - scaledHeight) / 2f

                drawImage(
                    image = starImage,
                    dstOffset = IntOffset(offsetX.roundToInt(), offsetY.roundToInt()),
                    dstSize = androidx.compose.ui.geometry.Size(scaledWidth, scaledHeight)
                        .toIntSize(),
                    blendMode = BlendMode.DstOut
                )
            }
    ) {
        PlayPauseButton(
            viewModel,
            buttonsSize = 48.dp,
        )
    }
}
