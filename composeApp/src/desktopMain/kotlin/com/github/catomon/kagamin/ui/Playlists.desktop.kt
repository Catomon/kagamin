package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.ui.components.OutlinedText
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.util.echoTrace
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Playlists(viewModel: KagaminViewModel, modifier: Modifier = Modifier) {
    echoTrace { "Playlists" }
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val index = remember(playlists) { playlists.mapIndexed { i, pl -> (pl to i) }.toMap() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showSearchIcon by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchTextValue by remember { mutableStateOf("") }

    var filterName by remember { mutableStateOf("") }
    var filteredPlaylists by remember { mutableStateOf<List<Playlist>?>(null) }

    LaunchedEffect(playlists, filterName) {
        withContext(Dispatchers.Default) {
            filteredPlaylists = if (filterName.isNotBlank()) {
                playlists.filter { it.name.lowercase().contains(filterName.lowercase()) }
            } else {
                null
            }
        }
    }

    LaunchedEffect(Unit) {
        listState.scrollToItem(index[currentPlaylist] ?: 0)
    }

    if (playlists.isEmpty()) {
        Box(
            modifier.background(KagaminTheme.backgroundTransparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No playlists.",
                textAlign = TextAlign.Center,
                color = KagaminTheme.textSecondary
            )
        }
    } else {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Column(modifier) {
            Box(
                modifier = Modifier.background(KagaminTheme.backgroundTransparent).height(32.dp)
                    .fillMaxWidth().padding(4.dp).clip(RoundedCornerShape(8.dp)).clickable {
                        val curTrackIndex = index[currentPlaylist] ?: return@clickable
                        coroutineScope.launch {
                            listState.animateScrollToItem(curTrackIndex)
                        }
                    }.onPointerEvent(PointerEventType.Exit) {
                        if (searchTextValue.isBlank()) {
                            showSearchBar = false
                            showSearchIcon = false
                        }
                    }.onPointerEvent(PointerEventType.Enter) {
                        showSearchIcon = true
                    }, contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedVisibility(showSearchIcon) {
                        IconButton(onClick = {
                            if (searchTextValue.isNotBlank()) {
                                showSearchBar = false
                                showSearchIcon = false
                            } else {
                                //focus text field?
                            }
                        }, modifier = Modifier.size(30.dp).padding(top = 4.dp)) {
                            Icon(
                                painterResource(Res.drawable.search),
                                "Search",
                                modifier = Modifier.size(20.dp)
                                    .onPointerEvent(PointerEventType.Enter) {
                                        showSearchBar = true
                                    },
                                tint = KagaminTheme.colors.buttonIcon
                            )
                        }
                    }

                    AnimatedContent(showSearchBar) {
                        if (showSearchBar) {
                            LaunchedEffect(searchTextValue) {
                                delay(300)
                                filterName = searchTextValue
                            }

                            DisposableEffect(Unit) {
                                onDispose {
                                    searchTextValue = ""
                                    filterName = ""
                                }
                            }

                            BasicTextField(
                                searchTextValue,
                                onValueChange = {
                                    searchTextValue = it
                                },
                                modifier = Modifier.weight(1f).fillMaxWidth().height(20.dp)
                                    .padding(end = 6.dp)
                                    .offset(y = 2.dp).drawBehind {
                                        drawLine(
                                            KagaminTheme.colors.buttonIcon,
                                            start = Offset(0f, size.height),
                                            end = Offset(size.width, size.height)
                                        )
                                    },
                                textStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                                cursorBrush = SolidColor(KagaminTheme.colors.buttonIcon),
                                maxLines = 1
                            )
                        } else {
                            Text(
                                currentPlaylist.name,
                                fontSize = 10.sp,
                                color = KagaminTheme.colors.buttonIcon,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Box(Modifier.fillMaxSize().hoverable(interactionSource)) {
                Column {
                    val displayedPlaylists = remember(filteredPlaylists, playlists) {
                        (filteredPlaylists ?: playlists).sortedBy { it.name.lowercase() }
                    }

                    LazyColumn(
                        state = listState, horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.background(KagaminTheme.backgroundTransparent),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        items(displayedPlaylists.size, key = {
                            displayedPlaylists.elementAt(it)
                        }) { i ->
                            val playlist = displayedPlaylists.elementAt(i)
                            PlaylistItem(playlist, viewModel, displayedPlaylists, currentPlaylist == playlist, i, remove = {
                                viewModel.removePlaylist(playlist)
                            }, clear = {
                                viewModel.clearPlaylist(playlist)
                            }, shuffle = {
                                viewModel.shufflePlaylist(playlist)
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
fun PlaylistItem(
    playlist: Playlist,
    viewModel: KagaminViewModel,
    playlists: List<Playlist>,
    isCurrent: Boolean,
    i: Int,
    remove: () -> Unit,
    clear: () -> Unit,
    shuffle: () -> Unit
) {
    val backColor = KagaminTheme.colors.listItem
    val height = 64.dp
    val randomTrackUri = remember { playlist.tracks.randomOrNull()?.uri }

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
            AnimatedVisibility(isCurrent) {
                PlaybackStateButton(height, backColor, viewModel)
            }

            Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))) {
                TrackThumbnail(
                    randomTrackUri,
                    modifier = Modifier.fillMaxWidth().height(height),
                    shape = RectangleShape,
                    height = ThumbnailCacheManager.SIZE.H250
                )

                PlaylistItemContent(viewModel, playlist, backColor)
            }
        }
    }
}

@Composable
private fun PlaylistItemContent(
    viewModel: KagaminViewModel, playlist: Playlist, backColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxHeight()
                //.background(color = backColor)
                .clickable {
                    viewModel.updateCurrentPlaylist(playlist)
                    viewModel.currentTab = Tabs.TRACKLIST
                }.padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedText(
                    playlist.name, fontSize = 10.sp, fillColor = KagaminTheme.text, maxLines = 1,
                    outlineColor = Color.Black.copy(alpha = 0.25f),
                    outlineDrawStyle = Stroke(
                        width = 3f,
                        join = StrokeJoin.Round
                    )
                )
            }

            Row(Modifier.fillMaxWidth()) {
                OutlinedText(
                    "Tracks: ${playlist.tracks.size}",
                    fontSize = 10.sp,
                    fillColor = KagaminTheme.text,
                    outlineColor = Color.Black.copy(alpha = 0.25f),
                    outlineDrawStyle = Stroke(
                        width = 3f,
                        join = StrokeJoin.Round
                    )
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