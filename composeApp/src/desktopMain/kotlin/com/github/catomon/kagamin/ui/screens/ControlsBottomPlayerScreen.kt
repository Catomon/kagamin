package com.github.catomon.kagamin.ui.screens

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.saveSettings
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.components.AddButton
import com.github.catomon.kagamin.ui.components.LuckyStarLogo
import com.github.catomon.kagamin.ui.components.PlaybackButtons
import com.github.catomon.kagamin.ui.components.RandomPlaybackButton
import com.github.catomon.kagamin.ui.components.RepeatPlaylistPlaybackButton
import com.github.catomon.kagamin.ui.components.RepeatTrackPlaybackButton
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.components.VolumeOptions
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.minimize_window
import org.jetbrains.compose.resources.painterResource

@Composable
fun ControlsBottomPlayerScreen(
    viewModel: KagaminViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val audioPlayer = viewModel.audioPlayer
    val playlist = viewModel.playlist
    val currentTrack = viewModel.currentTrack
    val playState = viewModel.playState
    val playMode = viewModel.playMode
    val currentPlaylistName = viewModel.currentPlaylistName

    val tabTransition: (Tabs) -> ContentTransform = { tab ->
        when (tab) {
            Tabs.ADD_TRACKS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            Tabs.CREATE_PLAYLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.TRACKLIST -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            Tabs.PLAYLISTS -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }

            else -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
        }
    }

    LaunchedEffect(currentPlaylistName) {
        viewModel.reloadPlaylist()

        saveSettings(viewModel.settings.copy(lastPlaylistName = currentPlaylistName))
    }

    LaunchedEffect(currentTrack) {
        viewModel.updateThumbnail()
    }

    Box(
        modifier.background(
            color = KagaminTheme.background
        )
    ) {
        TrackThumbnail(
            currentTrack?.uri,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop,
            blur = true,
        )

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
//                        repeat = player.playMode.value == AudioPlayer.PlayMode.REPEAT_TRACK,
//                        volume = player.volume.value,
//                        random = player.playMode.value == AudioPlayer.PlayMode.RANDOM,
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

                if (viewModel.playlist.isEmpty()) {
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
                        viewModel.playlist,
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
//                AppName(
//                    Modifier
//                        .padding(horizontal = 12.dp)
//                        .height(25.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .clickable {
//                            if (navController.currentDestination?.route != SettingsDestination.toString())
//                                navController.navigate(SettingsDestination.toString())
//                        }
//                        .align(Alignment.CenterStart)
//                )
                LuckyStarLogo(
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
                    RepeatPlaylistPlaybackButton(audioPlayer)

                    RepeatTrackPlaybackButton(audioPlayer)

                    PlaybackButtons(audioPlayer)

                    RandomPlaybackButton(audioPlayer)

                    VolumeOptions(
                        volume = audioPlayer.volume.value,
                        onVolumeChange = { newVolume ->
                            audioPlayer.volume.value = newVolume; audioPlayer.setVolume(newVolume)
                        },
                        modifier = Modifier.width(133.dp)
                    )

                    AddTrackOrPlaylistButton(viewModel, Modifier.padding(end = 6.dp))
                }
            }
        }
    }
}

@Composable
fun AddTrackOrPlaylistButton(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    AddButton(
        onClick = {
            viewModel.currentTab = Tabs.CREATE_PLAYLIST
            viewModel.createPlaylistWindow = !viewModel.createPlaylistWindow
        },
        modifier = modifier,
        color = if (viewModel.currentTab == Tabs.ADD_TRACKS || viewModel.currentTab == Tabs.CREATE_PLAYLIST) KagaminTheme.colors.buttonIconSmallSelected else KagaminTheme.colors.buttonIconSmall,
        size = 24.dp
    )
}
