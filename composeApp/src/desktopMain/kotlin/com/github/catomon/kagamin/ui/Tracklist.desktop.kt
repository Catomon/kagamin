package com.github.catomon.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.formatTime
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun Tracklist(
    viewModel: KagaminViewModel,
    tracks: List<AudioTrack>,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }
    val index =
        remember(tracks) { tracks.mapIndexed { i, track -> (track.uri to i) }.toMap() }
    val currentTrack = viewModel.currentTrack
    val listState = rememberLazyListState()
    var allowAutoScroll by remember { mutableStateOf(true) }
    val window = LocalWindow.current

    LaunchedEffect(allowAutoScroll) {
        delay(3000)
        allowAutoScroll = true
    }

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[currentTrack?.uri] ?: 0)
    }

    LaunchedEffect(currentTrack) {
        if (!window.isMinimized && viewModel.settings.autoScrollNextTrack && allowAutoScroll) {
            val nextIndex =
                index[currentTrack?.uri ?: return@LaunchedEffect] ?: return@LaunchedEffect
            if (viewModel.playMode == AudioPlayer.PlayMode.RANDOM)
                listState.scrollToItem(nextIndex)
            else
                listState.animateScrollToItem(nextIndex)
        }
    }

    Column(modifier) {
        if (currentTrack != null) {
            TrackItemHeader(
                -1,
                viewModel.currentTrack!!,
                tracklistManager,
                viewModel = viewModel,
                onClick = onClick@{
                    val curTrackIndex = index[currentTrack.uri] ?: return@onClick
                    coroutineScope.launch {
                        listState.animateScrollToItem(curTrackIndex)
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier.background(KagaminTheme.backgroundTransparent).height(32.dp)
                    .fillMaxWidth()
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().weight(2f).hoverable(interactionSource)
        ) {
            Column(Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .background(KagaminTheme.backgroundTransparent)
                        .pointerInput(allowAutoScroll) {
                            allowAutoScroll = false
                        },
                    state = listState
                ) {
                    items(tracks.size, key = {
                        tracks[it].uri
                    }) { index ->
                        val track = tracks[index]
                        ThumbnailTrackItem(
                            index,
                            track,
                            tracklistManager,
                            viewModel = viewModel,
                            onClick = onClick@{
                                if (tracklistManager.isAnySelected) {
                                    if (tracklistManager.isSelected(index, track))
                                        tracklistManager.deselect(index, track)
                                    else tracklistManager.select(index, track)
                                    return@onClick
                                }
                                if (viewModel.isLoadingSong != null) return@onClick

                                viewModel.viewModelScope.launch {
                                    viewModel.isLoadingSong = track
                                    viewModel.audioPlayer.play(track)
                                    viewModel.isLoadingSong = null
                                }
                            },
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Spacer(Modifier.fillMaxSize().weight(2f).background(KagaminTheme.theme.listItem))
            }

            androidx.compose.animation.AnimatedVisibility(
                isHovered, modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
            ) {
                VerticalScrollbar(
                    modifier = Modifier
                        .fillMaxHeight().clickable { },
                    adapter = rememberScrollbarAdapter(listState)
                )
            }
        }
    }
}

@Composable
actual fun TrackItemHeader(
    index: Int,
    track: AudioTrack,
    tracklistManager: TracklistManager,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val isHeader = true
    val backColor = KagaminTheme.backgroundTransparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(32.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        if (index > -1 && viewModel.currentTrack == track) {
            Box(
                Modifier
                    .height(32.dp)
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(color = backColor, size = size, blendMode = BlendMode.SrcOut)
                        drawContent()
                    }
                    .clickable { viewModel.onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(32.dp).background(
                        KagaminTheme.backgroundTransparent,
                        RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                    )
                ) {
                    Image(
                        painterResource(if (viewModel.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                        "track playback state icon",
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(KagaminTheme.theme.buttonIcon)
                    )
                }
            }
        }

        Box(
            modifier = modifier.fillMaxWidth().height(32.dp)
                .background(color = backColor)
                .clickable {
                    onClick()
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    track.name,
                    fontSize = 10.sp,
                    color = if (isHeader) KagaminTheme.theme.buttonIcon else KagaminTheme.text,
                    maxLines = 1,
                    // overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(0.99f).let {
                        if (isHeader) it.basicMarquee(iterations = Int.MAX_VALUE)
                        else it
                    }
                )

                // duration text
                if (isHeader)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var timePastText by remember { mutableStateOf("-:-") }
                        var trackDurationText by remember { mutableStateOf("-:-") }

                        LaunchedEffect(track) {
                            trackDurationText = formatTime(track.duration)

                            while (true) {
                                if (viewModel.audioPlayer.playState.value == AudioPlayer.PlayState.PLAYING) {
                                    timePastText =
                                        formatTime(track.let { viewModel.audioPlayer.position })

                                    if (viewModel.audioPlayer.position < 1000)
                                        trackDurationText = formatTime(track.duration)
                                }
                                delay(250)
                            }
                        }

                        Text(
                            "$timePastText/$trackDurationText",
                            fontSize = 10.sp,
                            color = KagaminTheme.theme.buttonIcon
                        )
                    }
            }

            //selected icon
            Row(
                Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (tracklistManager.selected.contains(index))
                    Icon(painterResource(Res.drawable.selected), null)
            }
        }
    }
}
