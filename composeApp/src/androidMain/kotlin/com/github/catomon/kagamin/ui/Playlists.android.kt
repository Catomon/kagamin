package com.github.catomon.kagamin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.removePlaylist
import com.github.catomon.kagamin.savePlaylist
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.util.Tabs
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun Playlists(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    val playlists by viewModel.playlists.collectAsState()
    val index =
        remember(playlists) { playlists.mapIndexed { i, pl -> (pl.first to i) }.toMap() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[viewModel.currentPlaylistName] ?: 0)
    }

    if (playlists.isEmpty()) {
        Box(
            modifier
                .background(KagaminTheme.colors.listItem),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No playlists.",
                textAlign = TextAlign.Center,
                color = KagaminTheme.textSecondary
            )
        }
    } else {
        Column(modifier) {
            Box(
                modifier = Modifier.background(KagaminTheme.backgroundTransparent).height(32.dp)
                    .fillMaxWidth()
                    .clickable {
                        val curTrackIndex =
                            index[viewModel.currentPlaylistName] ?: return@clickable
                        coroutineScope.launch {
                            listState.animateScrollToItem(curTrackIndex)
                        }
                    }.padding(horizontal = 4.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    viewModel.currentPlaylistName,
                    fontSize = 10.sp,
                    color = KagaminTheme.colors.buttonIcon
                )
            }

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(playlists.size, key = {
                    playlists[it]
                }) { i ->
                    val playlist = playlists[i]
                    PlaylistItem(
                        playlist,
                        viewModel,
                        playlists,
                        i,
                        remove = {
                            removePlaylist(playlist.first)
                            if (viewModel.currentPlaylistName == playlist.first)
                                viewModel.currentPlaylistName = "default"
                            viewModel.reloadPlaylists()
                        },
                        clear = {
                            savePlaylist(
                                playlist.first,
                                arrayOf()
                            )

                            if (viewModel.currentPlaylistName == playlist.first)
                                viewModel.audioPlayer.playlist.value = mutableListOf()

                            viewModel.reloadPlaylists()
                        },
                        shuffle = {
                            savePlaylist(playlist.first, playlist.second.tracks.toList().shuffled())

                            viewModel.reloadPlaylists()

                            viewModel.reloadPlaylist()
                        }
                    )
                }
            }

            Box(Modifier.fillMaxSize().background(KagaminTheme.colors.listItem))
        }
    }
}

@Composable
actual fun PlaylistItem(
    playlist: Pair<String, Playlist>,
    viewModel: KagaminViewModel,
    playlists: List<Pair<String, Playlist>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit,
    shuffle: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(56.dp)) {
        if (viewModel.currentPlaylistName == playlist.first) {
            Box(Modifier
                .fillMaxHeight()
                .clip(
                    RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                )
                .background(KagaminTheme.backgroundTransparent)
                .clickable {
                    viewModel.onPlayPause()
                }, contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(if (viewModel.playState == AudioPlayerService.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                    "current playlist playback state icon",
                    modifier = Modifier
                        .size(16.dp)
                        .fillMaxHeight(),
                    colorFilter = ColorFilter.tint(KagaminTheme.colors.buttonIcon)
                )
            }
        }

        Column(
            Modifier
                .fillMaxHeight()
                .background(color = if (i % 2 == 0) KagaminTheme.colors.disabled else KagaminTheme.colors.listItem)
                .clickable {
                    viewModel.currentPlaylistName = playlist.first
                    viewModel.currentTab = Tabs.TRACKLIST
                }
                .padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    playlist.first, fontSize = 12.sp, color = KagaminTheme.text,
                    maxLines = 1
                )
            }

            Row(Modifier.fillMaxWidth()) {
                Text(
                    "Tracks: ${playlist.second.tracks.size}",
                    modifier = Modifier.weight(0.5f),
                    fontSize = 10.sp,
                    color = KagaminTheme.textSecondary
                )
//                    Text(
//                        "Duration: ???",
//                        modifier = Modifier.weight(0.5f),
//                        fontSize = 10.sp,
//                        color = Colors.text2
//                    )
            }
        }
    }
}
