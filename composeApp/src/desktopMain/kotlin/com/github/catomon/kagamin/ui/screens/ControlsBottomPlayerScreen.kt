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
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntSize
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.github.catomon.kagamin.audio.AudioPlayerManager
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.SortType
import com.github.catomon.kagamin.ui.MediaFolder
import com.github.catomon.kagamin.ui.Menu
import com.github.catomon.kagamin.ui.Playlists
import com.github.catomon.kagamin.ui.Tracklist
import com.github.catomon.kagamin.ui.TracksDropTarget
import com.github.catomon.kagamin.ui.components.AppLogo
import com.github.catomon.kagamin.ui.components.CurrentTrackFrameHorizontal
import com.github.catomon.kagamin.ui.components.PlayPauseButton
import com.github.catomon.kagamin.ui.components.PlaybackModeToggleButton
import com.github.catomon.kagamin.ui.components.PrevNextTrackButtons
import com.github.catomon.kagamin.ui.components.VolumeOptions
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.trackDropTargetBorder
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import com.github.catomon.kagamin.util.echoTraceFiltered
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.media_folder
import kagamin.composeapp.generated.resources.sorting_artist
import kagamin.composeapp.generated.resources.sorting_default
import kagamin.composeapp.generated.resources.sorting_duration
import kagamin.composeapp.generated.resources.sorting_title
import kagamin.composeapp.generated.resources.star_angled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()
    val playMode by viewModel.playMode.collectAsState()

    var isMenuOpen by remember { mutableStateOf(false) }

    var isMediaFolderPaneVisible by remember(viewModel.settings) { mutableStateOf(viewModel.settings.showMediaFolderPane) }

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

//                val window = LocalWindow.current
//                IconButton({
//                    window.isMinimized = true
//                }, modifier = Modifier.size(24.dp).align(Alignment.TopEnd)) {
//                    Box(
//                        Modifier.size(9.dp)
//                            .background(color = KagaminTheme.colors.thinBorder, shape = CircleShape)
//                    )
//                }

                AnimatedPlayPauseButton(
                    coroutineScope,
                    viewModel,
                    Modifier.align(Alignment.CenterEnd).padding(end = 24.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                val plWeight = if (viewModel.settings.showMediaFolderPane) 0.30f else 40f

                val tracklistWeight = if (viewModel.settings.showMediaFolderPane) 0.40f else 60f

                if (isMediaFolderPaneVisible)
                    MediaFolder(viewModel, Modifier.fillMaxHeight().weight(plWeight))

                Playlists(
                    viewModel,
                    Modifier.weight(plWeight).fillMaxHeight()
                )

                val tracksDropTarget = remember {
                    TracksDropTarget { tracksUris ->
                        viewModel.viewModelScope.launch {
                            val tracks = viewModel.loadTracks(tracksUris)
                            val uris = tracks.map { it.uri }
                            viewModel.updatePlaylist(currentPlaylist.copy(tracks = currentPlaylist.tracks.filter { it.uri !in uris } + tracks))
                        }
                    }
                }

                if (currentPlaylist.tracks.isEmpty()) {
                    Box(
                        Modifier
                            .weight(tracklistWeight)
                            .fillMaxHeight()
                            .background(KagaminTheme.backgroundTransparent)
                            .dragAndDropTarget(
                                { tracksDropTarget.shouldStartDaD(it) },
                                tracksDropTarget
                            )
                            .trackDropTargetBorder(tracksDropTarget.isTarget),
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
                        Modifier.weight(tracklistWeight)
                            .dragAndDropTarget(
                                { tracksDropTarget.shouldStartDaD(it) },
                                tracksDropTarget
                            )
                            .trackDropTargetBorder(tracksDropTarget.isTarget)
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
                            isMenuOpen = !isMenuOpen
                        }
                        .align(Alignment.CenterStart)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    IconButton({
                        isMediaFolderPaneVisible = !isMediaFolderPaneVisible
                    }) {
                        Icon(
                            painterResource(Res.drawable.media_folder),
                            if (isMediaFolderPaneVisible) "Hide Media Folder Pane" else "Show Media Folder Pane",
                            tint = if (isMediaFolderPaneVisible) KagaminTheme.colors.buttonIcon else KagaminTheme.colors.disabled
                        )
                    }

                    SortingToggleButton(currentPlaylist, viewModel)

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

        AnimatedVisibility(
            isMenuOpen,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Box(Modifier.fillMaxSize().clickable(null, null) {
                isMenuOpen = false
            }) {
                Menu(
                    currentTrack,
                    viewModel, navigateToSettings = {
                        if (navController.currentDestination?.route != SettingsDestination.toString())
                            navController.navigate(SettingsDestination.toString())
                    },
                    onClose = {
                        isMenuOpen = false
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 40.dp)
                )
            }
        }

        AnimatedVisibility(
            viewModel.isLoading,
            modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp)
        ) {
            CircularProgressIndicator(Modifier.size(32.dp))
        }
    }
}

@Composable
fun SortingToggleButton(
    currentPlaylist: Playlist,
    viewModel: KagaminViewModel
) {
    IconButton({
        val sortEntries = SortType.entries
        val next = sortEntries.indexOf(currentPlaylist.sortType) + 1
        viewModel.updatePlaylist(currentPlaylist.copy(sortType = sortEntries[if (next < sortEntries.size) next else 0]))
    }) {
        Icon(
            painterResource(
                when (currentPlaylist.sortType) {
                    SortType.ORDER -> Res.drawable.sorting_default
                    SortType.TITLE -> Res.drawable.sorting_title
                    SortType.ARTIST -> Res.drawable.sorting_artist
                    SortType.DURATION -> Res.drawable.sorting_duration
                }
            ),
            contentDescription = null,
            tint = KagaminTheme.colors.buttonIcon,
        )
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
                    dstSize = Size(scaledWidth, scaledHeight)
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
