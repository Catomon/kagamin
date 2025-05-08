package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedVisibility
import com.github.catomon.kagamin.data.PlaylistData
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.audio.AudioPlayer
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.components.getThumbnail
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.util.Tabs
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.jetbrains.compose.resources.painterResource

@Composable
fun Playlists(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    val playlistsMap by viewModel.playlists.collectAsState()
    val playlists = remember(playlistsMap) { playlistsMap.map { it.key to it.value } }
    val index = remember(playlists) { playlists.mapIndexed { i, pl -> (pl.first to i) }.toMap() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[viewModel.currentPlaylistName] ?: 0)
    }

    if (playlists.isEmpty()) {
        Box(
            modifier.background(KagaminTheme.backgroundTransparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No playlists.",
                textAlign = TextAlign.Center,
                color = KagaminTheme.backgroundTransparent
            )
        }
    } else {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Column(modifier) {
            Box(
                modifier = Modifier.background(KagaminTheme.backgroundTransparent).height(32.dp)
                    .fillMaxWidth().clickable {
                        val curTrackIndex = index[viewModel.currentPlaylistName] ?: return@clickable
                        coroutineScope.launch {
                            listState.animateScrollToItem(curTrackIndex)
                        }
                    }.padding(horizontal = 4.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    viewModel.currentPlaylistName,
                    fontSize = 10.sp,
                    color = KagaminTheme.theme.buttonIcon
                )
            }

            Box(Modifier.fillMaxSize().hoverable(interactionSource)) {
                Column {
                    LazyColumn(
                        state = listState, horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.background(KagaminTheme.backgroundTransparent),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        items(playlists.size, key = {
                            playlists[it]
                        }) { i ->
                            val playlist = playlists[i]
                            PlaylistItem(playlist, viewModel, playlists, i, remove = {
                                viewModel.removePlaylist(playlist.first)
                            }, clear = {
                                viewModel.clearPlaylist(playlist.first)
                            }, shuffle = {
                                viewModel.shufflePlaylist(playlist.first)
                            })
                        }
                    }

                    Box(Modifier.fillMaxSize().background(KagaminTheme.backgroundTransparent))
                }

                androidx.compose.animation.AnimatedVisibility(
                    isHovered, modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                ) {
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().clickable { },
                        adapter = rememberScrollbarAdapter(listState)
                    )
                }
            }
        }
    }
}

@Composable
actual fun PlaylistItem(
    playlist: Pair<String, PlaylistData>,
    viewModel: KagaminViewModel,
    playlists: List<Pair<String, PlaylistData>>,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit,
    shuffle: () -> Unit
) {

    val backColor = KagaminTheme.theme.listItem

    var trackThumbnailUpdated by remember { mutableStateOf<ImageBitmap?>(null) }

    val height = 64.dp

    LaunchedEffect(Unit) {
        playlist.second.tracks.randomOrNull()?.uri?.let { uri ->
            trackThumbnailUpdated = try {
                withContext(Dispatchers.IO) {
                    getThumbnail(uri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

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

        Row(Modifier.height(height).padding(2.dp)) {
            AnimatedVisibility(viewModel.currentPlaylistName == playlist.first) {
                PlaybackStateButton(height, backColor, viewModel)
            }

            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))) {
                TrackThumbnail(
                    trackThumbnailUpdated,
                    modifier = Modifier.fillMaxWidth().height(height),
                    shape = RectangleShape
                )

                PlaylistItemContent(viewModel, playlist, backColor)
            }
        }
    }
}

@Composable
private fun PlaylistItemContent(
    viewModel: KagaminViewModel, playlist: Pair<String, PlaylistData>, backColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxHeight()
                //.background(color = backColor)
                .clickable {
                    viewModel.currentPlaylistName = playlist.first
                    viewModel.currentTab = Tabs.TRACKLIST
                }.padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    playlist.first, fontSize = 10.sp, color = KagaminTheme.text, maxLines = 1
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