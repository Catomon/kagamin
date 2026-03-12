package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.LocalLayoutManager
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.LayoutManager
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.heart16
import kagamin.composeapp.generated.resources.online
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
    edit: () -> Unit,
    modifier: Modifier
) {
    val backColor = KagaminTheme.colors.listItem
    val randomTrack = remember { playlist.tracks.randomOrNull() }
    val layout by LocalLayoutManager.current.currentLayout
    val height by derivedStateOf {
        when (layout) {
            LayoutManager.Layout.Spacy -> 40.dp
            else -> 64.dp
        }
    }

    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Edit") {
                edit()
            },
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
                        viewModel.changeCurrentPlaylist(playlist)
                        viewModel.currentTab = Tabs.TRACKLIST
                    }) {

                if (layout != LayoutManager.Layout.Spacy)
                    TrackThumbnail(
                        randomTrack,
                        modifier = Modifier.size(height),
                        shape = RoundedCornerShape(8.dp),
                        size = ThumbnailCacheManager.SIZE.H64
                    )

                if (layout != LayoutManager.Layout.Spacy)
                    PlaylistItemContent(viewModel, playlist, Modifier.weight(1f))
                else
                    CompactPlaylistItemContent(viewModel, playlist, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CompactPlaylistItemContent(
    viewModel: KagaminViewModel, playlist: Playlist,
    modifier: Modifier = Modifier
) {
    val currentLayout by LocalLayoutManager.current.currentLayout
    val fontScale by derivedStateOf {
        when (currentLayout) {
            LayoutManager.Layout.Spacy -> 1.25f
            else -> 1f
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            modifier.fillMaxHeight()
                .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            when {
                playlist.isOnline -> {
                    Icon(
                        painterResource(Res.drawable.online),
                        null,
                        tint = KagaminTheme.colors.backgroundTransparent,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp)
                    )
                }

                playlist.name == "loved" -> {
                    Icon(
                        painterResource(Res.drawable.heart16),
                        null,
                        tint = KagaminTheme.colors.backgroundTransparent,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp)
                    )
                }
            }

            Text(
                playlist.name, fontSize = 10.sp * fontScale, color = KagaminTheme.text, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        Text(
            "${playlist.tracks.size}",
            fontSize = 8.sp * fontScale,
            color = KagaminTheme.textSecondary,
            maxLines = 1,
            //lineHeight = 16.sp
        )
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
            when {
                playlist.isOnline -> {
                    Icon(
                        painterResource(Res.drawable.online),
                        null,
                        tint = KagaminTheme.colors.backgroundTransparent,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp)
                    )
                }

                playlist.name == "loved" -> {
                    Icon(
                        painterResource(Res.drawable.heart16),
                        null,
                        tint = KagaminTheme.colors.backgroundTransparent,
                        modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp)
                    )
                }
            }

            Text(
                playlist.name, fontSize = 10.sp, color = KagaminTheme.text, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        Text(
            "Tracks: ${playlist.tracks.size}",
            fontSize = 8.sp,
            color = KagaminTheme.textSecondary,
            maxLines = 1,
            lineHeight = 16.sp
        )
    }
}