package com.github.catomon.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.audio.AudioTrack
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kagamin.composeapp.generated.resources.selected
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
    var indexed = remember(tracks) { emptyMap<String, Int>() }
    val currentTrack = viewModel.currentTrack

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(indexed[currentTrack?.uri] ?: 0)
        indexed = tracks.mapIndexed { i, track -> (track.uri to i) }.toMap()
    }

    Column(modifier) {
        if (currentTrack != null) {
            TracklistHeader(
                viewModel.currentTrack!!,
                viewModel = viewModel,
                onClick = onClick@{
                    val curTrackIndex = indexed[currentTrack.uri] ?: return@onClick
                    coroutineScope.launch {
                        listState.animateScrollToItem(curTrackIndex)
                    }
                },
            )
        } else {
            Box(
                modifier = Modifier
                    .background(KagaminTheme.backgroundTransparent)
                    .height(32.dp)
                    .fillMaxWidth()
            )
        }

        LazyColumn(Modifier.fillMaxWidth(), state = listState) {
            items(tracks.size, key = {
                tracks[it].uri
            }) { index ->
                val track = tracks[index]
                TracklistHeader(
                    track,
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
                    modifier = Modifier,
                )
            }
        }

        Box(Modifier
            .fillMaxSize()
            .background(KagaminTheme.colors.listItem))
    }
}

@Composable
actual fun TracklistHeader(
    currentTrack: AudioTrack,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    filterTracks: (String) -> Unit,
    modifier: Modifier
) {
    val clipboard = LocalClipboardManager.current
    val isHeader = index == -1
    val backColor = if (isHeader) KagaminTheme.backgroundTransparent else
        if (index % 2 == 0) KagaminTheme.colors.forDisabledMostlyIdk else KagaminTheme.colors.listItem

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        if (index > -1 && viewModel.currentTrack == currentTrack) {
            Box(
                Modifier
                    .height(32.dp)
                    .clip(
                        RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                    )
                    .background(KagaminTheme.backgroundTransparent)
                    .clickable {
                        viewModel.onPlayPause()
                    }, contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(if (viewModel.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                    "track playback state icon",
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
                )
            }
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(32.dp)
                .background(color = backColor)
                .clickable {
                    onClick()
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                currentTrack.name,
                fontSize = 12.sp,
                color = KagaminTheme.text,
                maxLines = 1,
                modifier = Modifier.align(Alignment.CenterStart),
                overflow = TextOverflow.Ellipsis,
            )



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