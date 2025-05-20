package com.github.catomon.kagamin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.AddTrackOrPlaylistButton
import com.github.catomon.kagamin.ui.components.AppLogo
import com.github.catomon.kagamin.ui.components.PlayPauseButton
import com.github.catomon.kagamin.ui.components.PrevNextTrackButtons
import com.github.catomon.kagamin.ui.components.RandomPlaybackButton
import com.github.catomon.kagamin.ui.components.RepeatPlaylistPlaybackButton
import com.github.catomon.kagamin.ui.components.RepeatTrackPlaybackButton
import com.github.catomon.kagamin.ui.components.VolumeOptions
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.minimize_window
import org.jetbrains.compose.resources.painterResource

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

    Box(
        modifier.background(
            color = KagaminTheme.background
        )
    ) {
        Background(currentTrack)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                Modifier.fillMaxWidth().height(8.dp)
                    .background(color = KagaminTheme.backgroundTransparent)
            ) {
                val window = LocalWindow.current
                IconButton({
                    window.isMinimized = true
                }, modifier = Modifier.align(Alignment.Center)) {
                    Icon(
                        painterResource(Res.drawable.minimize_window),
                        contentDescription = null,
                        tint = KagaminTheme.colors.buttonIcon
                    )
                }

//                AppName(modifier = Modifier.align(Alignment.CenterStart).padding(horizontal = 8.dp), height = 26.dp)
//
//                IconButton({
//                    val player = viewModel.audioPlayer
//                    saveSettings(viewModel.settings.copy(
//                        repeat = player.playMode.value == AudioPlayerService.PlayMode.REPEAT_TRACK,
//                        volume = player.volume.value,
//                        random = player.playMode.value == AudioPlayerService.PlayMode.RANDOM,
//                    ))
//                    exitProcess(0)
//                }, modifier = Modifier.align(Alignment.CenterEnd)) {
//                    Icon(Icons.Default.Close, contentDescription = null, tint = KagaminTheme.colors.buttonIcon)
//                }
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
                       .background(color = KagaminTheme.backgroundTransparent)
//                    .graphicsLayer {
//                        compositingStrategy = CompositingStrategy.Offscreen
//                    }
//                    .drawWithContent {
//                        drawRect(
//                            color = KagaminTheme.backgroundTransparent,
//                            size = size,
//
//                            )
//                        drawCircle(
//                            color = KagaminTheme.colors.backgroundTransparent,
//                            //blendMode = BlendMode.SrcOut,
//                            center = Offset(size.width, size.height),
//                            radius = 64.dp.toPx()
//                        )
//                        drawContent()
//                    },
                        ,
                contentAlignment = Alignment.Center
            ) {
                AppLogo(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (navController.currentDestination?.route != SettingsDestination.toString())
                                navController.navigate(SettingsDestination.toString())
                        }
                        .align(Alignment.CenterStart)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    AddTrackOrPlaylistButton(viewModel, Modifier.padding(end = 6.dp))

                    VolumeOptions(
                        volume = volume,
                        onVolumeChange = { newVolume ->
                            viewModel.setVolume(newVolume)
                        },
                        modifier = Modifier.width(133.dp)
                    )

                    RepeatPlaylistPlaybackButton(viewModel)

                    RepeatTrackPlaybackButton(viewModel)

                    RandomPlaybackButton(viewModel)

                    PrevNextTrackButtons(viewModel)

//                    Spacer(Modifier.width(64.dp))

                    PlayPauseButton(viewModel, modifier = Modifier.padding(end = 6.dp))
                }
            }
        }

        //PlayPauseButton(viewModel)//, modifier = Modifier.size(64.dp).align(Alignment.BottomEnd).background(KagaminTheme.backgroundTransparent, shape = CircleShape).offset())
    }
}
