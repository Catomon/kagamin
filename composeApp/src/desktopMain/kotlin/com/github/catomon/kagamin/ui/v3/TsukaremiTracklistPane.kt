package com.github.catomon.kagamin.ui.v3

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalSnackbarHostState
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.data.Playlist
import com.github.catomon.kagamin.data.cache.ThumbnailCacheManager
import com.github.catomon.kagamin.ui.ThumbnailTrackItemDefaults
import com.github.catomon.kagamin.ui.TracklistManager
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
import com.github.catomon.kagamin.ui.windows.LocalConfirmWindow
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.add
import kagamin.composeapp.generated.resources.arrow_left
import kagamin.composeapp.generated.resources.like_song
import kagamin.composeapp.generated.resources.online
import kagamin.composeapp.generated.resources.pause16
import kagamin.composeapp.generated.resources.play16
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun V3TracklistPane(
    viewModel: KagaminViewModel,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    val colors = V3Theme.colors
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val window = LocalWindow.current
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }
    val listState = rememberLazyListState()

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    var searching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val displayed = remember(currentPlaylist.tracks, searchText) {
        currentPlaylist.tracks.filter {
            searchText.isBlank() ||
                it.title.contains(searchText, ignoreCase = true) ||
                it.artist.contains(searchText, ignoreCase = true)
        }
    }

    val dropTarget = remember(currentPlaylist) {
        TracksDropTarget { tracksUris ->
            viewModel.viewModelScope.launch {
                val loaded = viewModel.loadTracks(tracksUris)
                val loadedUris = loaded.map { it.uri }
                viewModel.updatePlaylist(
                    currentPlaylist.copy(
                        tracks = loaded + currentPlaylist.tracks.filter { it.uri !in loadedUris }
                    )
                )
            }
        }
    }

    LaunchedEffect(displayed) {
        val index = displayed.indexOfFirst { it.id == currentTrack?.id }
        if (index >= 0) listState.animateScrollToItem(index)
    }

    LaunchedEffect(currentTrack) {
        if (!window.isMinimized && viewModel.settings.autoScrollNextTrack) {
            val index = displayed.indexOfFirst { it.id == currentTrack?.id }
            if (index >= 0) listState.animateScrollToItem(index)
        }
    }

    Column(
        modifier = modifier
            .v3Panel()
            .padding(8.dp),
    ) {
        V3PaneHeader(
            title = "Tracklist",
            count = currentPlaylist.tracks.size,
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
            actionDescription = "Add tracks",
            onAction = {
                viewModel.currentTab = Tabs.ADD_TRACKS
                viewModel.createPlaylistWindow = true
            },
            leadingIcon = if (onBack != null) painterResource(Res.drawable.arrow_left) else null,
            leadingDescription = "Back to playlists",
            onTitleClick = onBack,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .hoverable(interactionSource)
                .dragAndDropTarget(
                    { dropTarget.shouldStartDaD(it) },
                    dropTarget,
                )
                .then(V3ItemDropBorder(dropTarget.isTarget)),
        ) {
            if (displayed.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    V3OutlinedText(
                        text = "Drop files or folders here",
                        fillColor = colors.panelContentSecondary,
                        outlineColor = colors.headerOutline,
                        fontSize = 13.sp,
                        outlineSize = 3.5f,
                        maxLines = 2,
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = displayed,
                        key = { it.id },
                    ) { track ->
                        val index = displayed.indexOf(track)
                        V3TrackItem(
                            index = index,
                            track = track,
                            viewModel = viewModel,
                            tracklistManager = tracklistManager,
                            isCurrent = track.id == currentTrack?.id,
                            onClick = onClick@{
                                if (tracklistManager.isAnySelected) {
                                    if (tracklistManager.isSelected(index, track))
                                        tracklistManager.deselect(index, track)
                                    else
                                        tracklistManager.select(index, track)
                                    return@onClick
                                }

                                if (viewModel.isLoadingSong != null) return@onClick

                                if (currentTrack?.id != track.id)
                                    viewModel.viewModelScope.launch { viewModel.play(track) }
                                else
                                    viewModel.onPlayPause()
                            },
                        )
                    }
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
private fun V3TrackItem(
    index: Int,
    track: AudioTrack,
    viewModel: KagaminViewModel,
    tracklistManager: TracklistManager,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    val colors = V3Theme.colors
    val showThumbnails = viewModel.settings.showThumbnails
    val height = if (showThumbnails) 58.dp else 46.dp

    val clipboard = LocalClipboardManager.current
    val confirmationWindow = LocalConfirmWindow.current
    val snackbar = LocalSnackbarHostState.current

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isSelected = tracklistManager.selected.contains(index)

    val contentColor = if (isCurrent) colors.itemSelectedContent else colors.itemContent
    val secondaryColor =
        if (isCurrent) colors.itemSelectedContentSecondary else colors.itemContentSecondary
    val textOutline = if (isCurrent) colors.itemSelectedOutline else colors.headerOutline
    val iconColor = if (isCurrent) colors.itemSelectedIcon else colors.itemContent

    var isLoved by remember(track, viewModel.playlists.value) {
        mutableStateOf(
            viewModel.playlists.value
                .firstOrNull { it.id == "loved" }
                ?.tracks
                ?.any { it.id == track.id } == true
        )
    }

    val playState by viewModel.playState.collectAsState()

    ContextMenuArea(
        items = {
            ThumbnailTrackItemDefaults.contextMenuItems(
                tracklistManager = tracklistManager,
                index = index,
                track = track,
                clipboard = clipboard,
                viewModel = viewModel,
                confirmationWindow = confirmationWindow,
                snackbar = snackbar,
                isCurrentTrack = isCurrent,
            )
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .height(height)
                .v3ItemBackground(isCurrent)
                .hoverable(interactionSource)
                .clickable(onClickLabel = "Play track", onClick = onClick)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .clickable(onClickLabel = "Play or pause", onClick = viewModel::onPlayPause),
                    contentAlignment = Alignment.Center,
                ) {
                    AnimatedContent(playState) { state ->
                        V3Icon(
                            painter = painterResource(
                                if (state == AudioPlayerService.PlayState.PLAYING) Res.drawable.pause16
                                else Res.drawable.play16
                            ),
                            tint = iconColor,
                            size = 14.dp,
                        )
                    }
                }
                Spacer(Modifier.size(6.dp))
            }

            if (showThumbnails) {
                TrackThumbnail(
                    currentTrack = track,
                    modifier = Modifier
                        .size(height - 14.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    size = ThumbnailCacheManager.SIZE.H150,
                )
                Spacer(Modifier.size(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                V3OutlinedText(
                    text = track.title,
                    fillColor = contentColor,
                    outlineColor = textOutline,
                    fontSize = 12.sp,
                    outlineSize = 3f,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.let {
                        if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it
                    },
                )

                if (track.artist.isNotBlank()) {
                    V3OutlinedText(
                        text = track.artist,
                        fillColor = secondaryColor,
                        outlineColor = textOutline,
                        fontSize = 10.sp,
                        outlineSize = 2.5f,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.offset(y = if (showThumbnails) 0.dp else (-2).dp),
                    )
                }
            }

            Row(
                modifier = Modifier.padding(start = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedVisibility(isHovered || isLoved) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .clickable(onClickLabel = "Love track") {
                                viewModel.viewModelScope.launch {
                                    val loved = viewModel.playlists.value
                                        .firstOrNull { it.id == "loved" }

                                    if (loved == null) {
                                        viewModel.createPlaylist(Playlist("loved", "loved", listOf(track)))
                                        isLoved = true
                                    } else if (loved.tracks.any { it.id == track.id }) {
                                        viewModel.updatePlaylist(
                                            loved.copy(tracks = loved.tracks - track)
                                        )
                                        isLoved = false
                                    } else {
                                        viewModel.updatePlaylist(
                                            loved.copy(tracks = loved.tracks + track)
                                        )
                                        isLoved = true
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        V3Icon(
                            painter = painterResource(Res.drawable.like_song),
                            tint = iconColor,
                            size = 15.dp,
                        )
                    }
                }

                if (!viewModel.currentPlaylist.value.isOnline && track.uri.startsWith("https")) {
                    V3Icon(
                        painter = painterResource(Res.drawable.online),
                        tint = iconColor,
                        size = 12.dp,
                        modifier = Modifier.padding(start = 2.dp),
                    )
                }

                if (isSelected) {
                    V3Icon(
                        painter = painterResource(Res.drawable.selected),
                        tint = iconColor,
                        size = 16.dp,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}
