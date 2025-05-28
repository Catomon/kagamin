package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.LocalToolWindow
import com.github.catomon.kagamin.ui.windows.ToolScreenState
import com.github.catomon.kagamin.ui.windows.ToolWindowState
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

    val toolWindow = LocalToolWindow.current

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
                        modifier = Modifier.graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    color = KagaminTheme.backgroundTransparent,
                                    size = size,
                                    blendMode = BlendMode.SrcOut
                                )
                                drawContent()
                            },
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        items(displayedPlaylists.size, key = {
                            displayedPlaylists.elementAt(it)
                        }) { i ->
                            val playlist = displayedPlaylists.elementAt(i)
                            PlaylistItem(
                                playlist,
                                viewModel,
                                displayedPlaylists,
                                currentPlaylist == playlist,
                                i,
                                remove = {
                                    viewModel.removePlaylist(playlist)
                                },
                                clear = {
                                    viewModel.clearPlaylist(playlist)
                                },
                                shuffle = {
                                    viewModel.shufflePlaylist(playlist)
                                },
                                edit = {
                                    toolWindow.value = ToolWindowState(
                                        currentScreenState = ToolScreenState.EditPlaylist(
                                            playlist = playlist,
                                            onRename = { viewModel.renamePlaylist(playlist, it) },
                                            onClose = {
                                                toolWindow.value =
                                                    ToolWindowState(isVisible = false)
                                            }),
                                        isVisible = true,
                                        onClose = {
                                            toolWindow.value =
                                                ToolWindowState(isVisible = false)
                                        }
                                    )
                                },
                                modifier = Modifier.padding(2.dp)
                            )
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
