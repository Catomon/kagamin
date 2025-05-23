package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.yt_ic
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlaylistItem(
    playlist: Playlist,
    viewModel: KagaminViewModel,
    playlists: List<Playlist>,
    isCurrent: Boolean,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit,
    shuffle: () -> Unit,
    modifier: Modifier
) {
    val backColor = KagaminTheme.colors.listItem
    val height = 64.dp
    val randomTrack = remember { playlist.tracks.randomOrNull() }

    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Shuffle") {
                shuffle()
            },
            ContextMenuItem("Clear") {
                clear()
            },
            ContextMenuItem("Remove") {
                remove()
            },
        )
    }) {
        Row(modifier.height(height)) {
            AnimatedVisibility(isCurrent) {
                PlaybackStateButton(height, viewModel)
            }

            Row(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = backColor)
                    .clickable {
                        viewModel.updateCurrentPlaylist(playlist)
                        viewModel.currentTab = Tabs.TRACKLIST
                    }) {
                TrackThumbnail(
                    randomTrack,
                    modifier = Modifier.size(height),
                    shape = RoundedCornerShape(8.dp),
                    height = ThumbnailCacheManager.SIZE.H64
                )

                PlaylistItemContent(viewModel, playlist, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PlaylistItemContent(
    viewModel: KagaminViewModel, playlist: Playlist,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxHeight()
            .padding(4.dp)
    ) {
        Row {
            if (playlist.isOnline)
                Icon(
                    painterResource(Res.drawable.yt_ic),
                    null,
                    tint = KagaminTheme.colors.backgroundTransparent,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp)
                )

            Text(
                playlist.name, fontSize = 10.sp, color = KagaminTheme.text, maxLines = 1,
            )
        }


        Text(
            "Tracks: ${playlist.tracks.size}",
            fontSize = 10.sp,
            color = KagaminTheme.text,
            maxLines = 1,
        )
    }
}