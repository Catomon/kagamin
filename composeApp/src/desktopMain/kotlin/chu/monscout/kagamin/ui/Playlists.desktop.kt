package chu.monscout.kagamin.ui

import chu.monscout.kagamin.data.PlaylistData
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.ui.theme.Colors
import chu.monscout.kagamin.audio.AudioPlayer
import chu.monscout.kagamin.loadPlaylists
import chu.monscout.kagamin.removePlaylist
import chu.monscout.kagamin.savePlaylist
import chu.monscout.kagamin.ui.viewmodel.KagaminViewModel
import chu.monscout.kagamin.ui.util.Tabs
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.pause
import kagamin.composeapp.generated.resources.play
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun Playlists(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    var playlists by remember { mutableStateOf(loadPlaylists()) }
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
                .background(Colors.theme.listItemB),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No playlists.",
                textAlign = TextAlign.Center,
                color = Colors.textSecondary
            )
        }
    } else {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Column(modifier) {
            Box(
                modifier = Modifier.background(Colors.backgroundTransparent).height(32.dp)
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
                    color = Colors.theme.buttonIcon
                )
            }

            Box(Modifier.fillMaxSize().hoverable(interactionSource)) {
                Column {
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
                                    playlists = loadPlaylists()
                                },
                                clear = {
                                    savePlaylist(
                                        playlist.first,
                                        arrayOf()
                                    )

                                    if (viewModel.currentPlaylistName == playlist.first)
                                        viewModel.audioPlayer.playlist.value = mutableListOf()

                                    playlists = loadPlaylists()
                                },
                                shuffle = {
                                    savePlaylist(
                                        playlist.first,
                                        playlist.second.tracks.toList().shuffled()
                                    )

                                    playlists = loadPlaylists()

                                    viewModel.reloadPlaylist()
                                }
                            )
                        }
                    }

                    Box(Modifier.fillMaxSize().background(Colors.theme.listItemB))
                }

                androidx.compose.animation.AnimatedVisibility(
                    isHovered, modifier = Modifier.align(Alignment.CenterEnd)
                        .fillMaxHeight()
                ) {
                    VerticalScrollbar(
                        modifier = Modifier
                            .fillMaxHeight(),
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(56.dp)) {
            if (viewModel.currentPlaylistName == playlist.first) {
                Box(Modifier.fillMaxHeight()//.clip(
                    //RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                //)
                    .background(Colors.backgroundTransparent).clickable {
                    viewModel.onPlayPause()
                }, contentAlignment = Alignment.Center) {
                    Image(
                        painterResource(if (viewModel.playState == AudioPlayer.PlayState.PAUSED) Res.drawable.pause else Res.drawable.play),
                        "current playlist playback state icon",
                        modifier = Modifier.size(16.dp).fillMaxHeight(),
                        colorFilter = ColorFilter.tint(Colors.theme.buttonIcon)
                    )
                }
            }

            Column(
                Modifier.fillMaxHeight().background(color = if (i % 2 == 0) Colors.theme.listItemA else Colors.theme.listItemB)
                    .clickable {
                        viewModel.currentPlaylistName = playlist.first
                        viewModel.currentTab = Tabs.TRACKLIST
                    }.padding(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        playlist.first, fontSize = 10.sp, color = Colors.text,
                        maxLines = 1
                    )
                }

                Row(Modifier.fillMaxWidth()) {
                    Text(
                        "Tracks: ${playlist.second.tracks.size}",
                        modifier = Modifier.weight(0.5f),
                        fontSize = 10.sp,
                        color = Colors.textSecondary
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
}