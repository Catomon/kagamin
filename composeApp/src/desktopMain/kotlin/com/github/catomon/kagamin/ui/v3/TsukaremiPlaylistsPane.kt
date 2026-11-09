package com.github.catomon.kagamin.ui.v3

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.TracksDropTarget
import com.github.catomon.kagamin.ui.components.TrackThumbnail
import com.github.catomon.kagamin.ui.util.Tabs
import com.github.catomon.kagamin.ui.v3.components.V3Icon
import com.github.catomon.kagamin.ui.v3.components.V3ItemDropBorder
import com.github.catomon.kagamin.ui.v3.components.V3OutlinedText
import com.github.catomon.kagamin.ui.v3.components.V3PaneHeader
import com.github.catomon.kagamin.ui.v3.components.v3ItemBackground
import com.github.catomon.kagamin.ui.v3.components.v3Panel
import com.github.catomon.kagamin.ui.v3.theme.V3Theme
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import com.github.catomon.kagamin.ui.windows.LocalToolWindow
import com.github.catomon.kagamin.ui.windows.ToolScreenState
import com.github.catomon.kagamin.ui.windows.ToolWindowState
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.heart16
import kagamin.composeapp.generated.resources.online
import kagamin.composeapp.generated.resources.pause16
import kagamin.composeapp.generated.resources.play16
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun V3PlaylistsPane(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    onPlaylistOpened: () -> Unit = {},
) {
    val colors = V3Theme.colors
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val playlists by viewModel.playlists.collectAsState()

    val listState = rememberLazyListState()
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val displayed = remember(playlists, searchText) {
        playlists
            .filter { searchText.isBlank() || it.name.contains(searchText, ignoreCase = true) }
            .sortedBy { it.name.lowercase() }
    }

    LaunchedEffect(displayed, currentPlaylist) {
        val index = displayed.indexOfFirst { it.id == currentPlaylist.id }
        if (index >= 0) listState.animateScrollToItem(index + 1)
    }

    Column(
        modifier = modifier
            .v3Panel()
            .padding(8.dp),
    ) {
        V3PaneHeader(
            title = "Playlists",
            count = playlists.size,
            searching = searching,
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onToggleSearch = {
                if (searching) {
                    searching = false
                    searchText = ""
                } else {
                    searching = true
                }
            },
            actionIcon = painterResource(Res.drawable.add),
            actionDescription = "New playlist",
            onAction = {
                viewModel.currentTab = Tabs.CREATE_PLAYLIST
                viewModel.createPlaylistWindow = true
            },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .hoverable(interactionSource),
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                item(key = "new-playlist") {
                    V3NewPlaylistRow {
                        viewModel.currentTab = Tabs.CREATE_PLAYLIST
                        viewModel.createPlaylistWindow = true
                    }
                }

                items(displayed, key = { it.id }) { playlist ->
                    V3PlaylistItem(
                        playlist = playlist,
                        viewModel = viewModel,
                        isCurrent = playlist.id == currentPlaylist.id,
                        onSelect = {
                            viewModel.changeCurrentPlaylist(playlist)
                            viewModel.currentTab = Tabs.TRACKLIST
                            onPlaylistOpened()
                        },
                    )
                }
            }

            if (isHovered) {
                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(listState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(start = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun V3NewPlaylistRow(onClick: () -> Unit) {
    val colors = V3Theme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClickLabel = "New playlist", onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        V3Icon(
            painter = painterResource(Res.drawable.add),
            tint = colors.panelContentSecondary,
            size = 16.dp,
        )
        Spacer(Modifier.size(8.dp))
        V3OutlinedText(
            text = "New playlist",
            fillColor = colors.panelContentSecondary,
            outlineColor = colors.headerOutline,
            fontSize = 12.sp,
            outlineSize = 3f,
            maxLines = 1,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun V3PlaylistItem(
    playlist: Playlist,
    viewModel: KagaminViewModel,
    isCurrent: Boolean,
    onSelect: () -> Unit,
) {
    val colors = V3Theme.colors
    val toolWindow = LocalToolWindow.current
    val showThumbnails = viewModel.settings.showThumbnails
    val height = if (showThumbnails) 58.dp else 46.dp

    val randomTrack = remember(playlist.id, playlist.tracks) { playlist.tracks.randomOrNull() }
    val playState by viewModel.playState.collectAsState()

    val contentColor = if (isCurrent) colors.itemSelectedContent else colors.itemContent
    val secondaryColor =
        if (isCurrent) colors.itemSelectedContentSecondary else colors.itemContentSecondary
    val textOutline = if (isCurrent) colors.itemSelectedOutline else colors.headerOutline
    val iconColor = if (isCurrent) colors.itemSelectedIcon else colors.itemContent

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val dropTarget = remember(playlist) {
        TracksDropTarget { tracksUris ->
            viewModel.viewModelScope.launch {
                val loaded = viewModel.loadTracks(tracksUris)
                val loadedUris = loaded.map { it.uri }
                viewModel.updatePlaylist(
                    playlist.copy(
                        tracks = loaded + playlist.tracks.filter { it.uri !in loadedUris }
                    )
                )
            }
        }
    }

    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("Edit") {
                    toolWindow.value = ToolWindowState(
                        currentScreenState = ToolScreenState.EditPlaylist(
                            playlist = playlist,
                            onSort = { viewModel.sortPlaylist(playlist, it) },
                            onRename = { viewModel.renamePlaylist(playlist, it) },
                            onClose = {
                                toolWindow.value = ToolWindowState(isVisible = false)
                            },
                        ),
                        isVisible = true,
                        onClose = {
                            toolWindow.value = ToolWindowState(isVisible = false)
                        },
                    )
                },
                ContextMenuItem("Shuffle") { viewModel.shufflePlaylist(playlist) },
                ContextMenuItem("Clear") { viewModel.clearPlaylist(playlist) },
                ContextMenuItem("Remove") { viewModel.removePlaylist(playlist) },
            )
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .height(height)
                .v3ItemBackground(isCurrent)
                .then(V3ItemDropBorder(dropTarget.isTarget))
                .hoverable(interactionSource)
                .dragAndDropTarget(
                    { dropTarget.shouldStartDaD(it) },
                    dropTarget,
                )
                .clickable(onClickLabel = "Open playlist", onClick = onSelect)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isCurrent) {
                V3PlaybackStateIcon(
                    playState = playState,
                    color = iconColor,
                    onClick = viewModel::onPlayPause,
                )
                Spacer(Modifier.size(6.dp))
            }

            if (showThumbnails) {
                TrackThumbnail(
                    currentTrack = randomTrack,
                    modifier = Modifier
                        .size(height - 14.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    size = ThumbnailCacheManager.SIZE.H64,
                )
                Spacer(Modifier.size(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when {
                        playlist.isOnline -> {
                            V3Icon(
                                painter = painterResource(Res.drawable.online),
                                tint = iconColor,
                                size = 12.dp,
                            )
                            Spacer(Modifier.size(4.dp))
                        }

                        playlist.name == "loved" -> {
                            V3Icon(
                                painter = painterResource(Res.drawable.heart16),
                                tint = iconColor,
                                size = 12.dp,
                            )
                            Spacer(Modifier.size(4.dp))
                        }
                    }

                    V3OutlinedText(
                        text = playlist.name,
                        fillColor = contentColor,
                        outlineColor = textOutline,
                        fontSize = 13.sp,
                        outlineSize = 3f,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.weight(1f),
                    )
                }

                V3OutlinedText(
                    text = "${playlist.tracks.size} tracks",
                    fillColor = secondaryColor,
                    outlineColor = textOutline,
                    fontSize = 10.sp,
                    outlineSize = 2.5f,
                    maxLines = 1,
                    modifier = Modifier.offset(y = if (showThumbnails) 0.dp else (-2).dp),
                )
            }

            if (isHovered && !isCurrent) {
                V3OutlinedText(
                    text = ">",
                    fillColor = colors.panelContentSecondary,
                    outlineColor = colors.headerOutline,
                    fontSize = 14.sp,
                    outlineSize = 3f,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun V3PlaybackStateIcon(
    playState: AudioPlayerService.PlayState,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable(onClickLabel = "Play or pause", onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(playState) { state ->
            V3Icon(
                painter = painterResource(
                    if (state == AudioPlayerService.PlayState.PLAYING) Res.drawable.pause16
                    else Res.drawable.play16
                ),
                tint = color,
                size = 14.dp,
            )
        }
    }
}
