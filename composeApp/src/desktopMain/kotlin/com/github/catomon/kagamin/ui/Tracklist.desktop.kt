package com.github.catomon.kagamin.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.kagamin.LocalWindow
import com.github.catomon.kagamin.audio.AudioPlayerService
import com.github.catomon.kagamin.audio.PlaylistsManager
import com.github.catomon.kagamin.data.AudioTrack
import com.github.catomon.kagamin.ui.components.TrackProgressIndicator
import com.github.catomon.kagamin.ui.theme.KagaminTheme
import com.github.catomon.kagamin.ui.util.formatTime
import com.github.catomon.kagamin.ui.viewmodel.KagaminViewModel
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource

@Composable
fun Tracklist(
    viewModel: KagaminViewModel, modifier: Modifier = Modifier
) {
    val currentPlaylist by viewModel.currentPlaylist.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val tracklistManager = remember { TracklistManager(coroutineScope) }
    val index = remember(currentPlaylist) {
        currentPlaylist.tracks.mapIndexed { i, track -> (track.uri to i) }.toMap()
    }
    val currentTrack by viewModel.currentTrack.collectAsState()
    val listState = rememberLazyListState()
    var allowAutoScroll by remember { mutableStateOf(true) }
    val window = LocalWindow.current
    var filterName by remember { mutableStateOf("") }
    var filteredTracks by remember { mutableStateOf<List<AudioTrack>?>(null) }

    LaunchedEffect(currentPlaylist, filterName) {
        withContext(Dispatchers.Default) {
            filteredTracks = if (filterName.isNotBlank()) {
                currentPlaylist.tracks.filter {
                    it.title.lowercase().contains(filterName.lowercase())
                }
            } else {
                null
            }
        }
    }

    LaunchedEffect(allowAutoScroll) {
        delay(3000)
        allowAutoScroll = true
    }

    LaunchedEffect(currentPlaylist.id) {
        listState.scrollToItem(index[currentTrack?.uri] ?: 0)
    }

    LaunchedEffect(currentTrack) {
        if (!window.isMinimized && viewModel.settings.autoScrollNextTrack && allowAutoScroll) {
            val nextIndex =
                index[currentTrack?.uri ?: return@LaunchedEffect] ?: return@LaunchedEffect
            if (viewModel.playMode.value == PlaylistsManager.PlayMode.RANDOM) listState.scrollToItem(
                nextIndex
            )
            else listState.animateScrollToItem(nextIndex)
        }
    }

    Column(modifier) {
        if (currentTrack != null) {
            TracklistHeader(currentTrack, viewModel = viewModel, onClick = onClick@{
                val curTrackIndex = currentTrack?.let { index[it.uri] } ?: return@onClick
                coroutineScope.launch {
                    listState.animateScrollToItem(curTrackIndex)
                }
            }, filterTracks = {
                filterName = it
            })
        } else {
            Box(
                modifier = Modifier.background(KagaminTheme.backgroundTransparent).height(32.dp)
                    .fillMaxWidth()
            )
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().weight(2f).hoverable(interactionSource)
        ) {
            Column(Modifier.fillMaxSize()) {
                val tracks by remember {
                    derivedStateOf {
                        filteredTracks ?: currentPlaylist.tracks //todo sorting
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = KagaminTheme.backgroundTransparent,
                                size = size,
                                blendMode = BlendMode.SrcOut
                            )
                            drawContent()
                        }
//                        .background(KagaminTheme.backgroundTransparent)
                        .pointerInput(allowAutoScroll) {
                            allowAutoScroll = false
                        }, state = listState, contentPadding = PaddingValues(2.dp)
                ) {
                    items(tracks.size, key = {
                        tracks[it].id
                    }) { index ->
                        val track = tracks[index]

                        ThumbnailTrackItem(
                            index,
                            track,
                            tracklistManager,
                            isCurrentTrack = currentTrack == track,
                            viewModel = viewModel,
                            onClick = onClick@{
                                if (tracklistManager.isAnySelected) {
                                    if (tracklistManager.isSelected(
                                            index, track
                                        )
                                    ) tracklistManager.deselect(index, track)
                                    else tracklistManager.select(index, track)
                                    return@onClick
                                }
                                if (viewModel.isLoadingSong != null) return@onClick

                                viewModel.viewModelScope.launch {
                                    viewModel.play(track)
                                }
                            },
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

                Spacer(
                    Modifier.fillMaxSize().weight(2f).background(KagaminTheme.backgroundTransparent)
                )
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

private enum class Content {
    SearchBar, TrackName, Indicator,
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TracklistHeader(
    currentTrack: AudioTrack?,
    viewModel: KagaminViewModel,
    onClick: () -> Unit,
    filterTracks: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = KagaminTheme.backgroundTransparent
    var shownContent by remember { mutableStateOf(Content.TrackName) }
    var showSearchIcon by remember { mutableStateOf(false) }

    var searchTextValue by remember { mutableStateOf("") }

    val interactionSource = remember { MutableInteractionSource() }
    val isIndicatorHovered by interactionSource.collectIsHoveredAsState()
    val position by viewModel.position.collectAsState()

    val progress by remember(currentTrack) {
        derivedStateOf {
            if (!isIndicatorHovered) {
                when (currentTrack) {
                    null -> 0f
                    else -> if (currentTrack.duration > 0 && currentTrack.duration < Long.MAX_VALUE) position.toFloat() / currentTrack.duration else -1f
                }
            } else {
                0f
            }
        }
    }

    val progressAnimated by animateFloatAsState(progress)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(32.dp).fillMaxWidth().onPointerEvent(PointerEventType.Exit) {
            if (searchTextValue.isBlank()) {
                shownContent = Content.TrackName
                showSearchIcon = false
            }
        }.onPointerEvent(PointerEventType.Enter) {
            showSearchIcon = true
        },
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = modifier.fillMaxWidth().height(32.dp).background(color = backgroundColor)
                .padding(4.dp).clip(RoundedCornerShape(8.dp)).clickable {
                    onClick()
                }, contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(showSearchIcon) {
                    IconButton(onClick = {
                        if (searchTextValue.isNotBlank()) {
                            shownContent = Content.TrackName
                            showSearchIcon = false
                        } else {
                            //focus text field?
                        }
                    }, modifier = Modifier.size(30.dp).padding(top = 4.dp)) {
                        Icon(
                            painterResource(Res.drawable.search),
                            "Search",
                            modifier = Modifier.size(20.dp).onPointerEvent(PointerEventType.Enter) {
                                shownContent = Content.SearchBar
                            },
                            tint = KagaminTheme.colors.buttonIcon
                        )
                    }
                }

                AnimatedContent(shownContent, modifier.weight(1f)) { shownContent ->
                    when (shownContent) {
                        Content.Indicator -> {
                            var progressOnHover by remember { mutableStateOf(0f) }

                            TrackProgressIndicator(
                                currentTrack = currentTrack,
                                seek = {
                                    viewModel.seek(it)
                                },
                                progress = if (isIndicatorHovered) progressOnHover else progressAnimated,
                                modifier = Modifier.weight(1f).padding(end = 6.dp, start = 3.dp)
                                    .height(10.dp).onPointerEvent(
                                        PointerEventType.Move
                                    ) {
                                        progressOnHover = it.changes.first().position.x / size.width
                                    }.hoverable(interactionSource)
                            )
                        }

                        Content.SearchBar -> {
                            LaunchedEffect(searchTextValue) {
                                delay(300)
                                filterTracks(searchTextValue)
                            }

                            DisposableEffect(Unit) {
                                onDispose {
                                    searchTextValue = ""
                                    filterTracks("")
                                }
                            }

                            BasicTextField(
                                searchTextValue,
                                onValueChange = {
                                    searchTextValue = it
                                },
                                modifier = Modifier.weight(1f).height(20.dp).padding(end = 6.dp)
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
                        }

                        Content.TrackName -> {
                            @Suppress("NAME_SHADOWING") val interactionSource =
                                remember { MutableInteractionSource() }
                            val isHovered by interactionSource.collectIsHoveredAsState()

                            Text(
                                currentTrack?.title ?: "",
                                fontSize = 10.sp,
                                color = KagaminTheme.colors.buttonIcon,
                                maxLines = 1,
                                modifier = Modifier.weight(1f).hoverable(interactionSource)
                                    .let { if (isHovered) it.basicMarquee(iterations = Int.MAX_VALUE) else it }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.onPointerEvent(PointerEventType.Enter) {
                        shownContent = Content.Indicator
                    }) {
                    val trackDurationText by remember(currentTrack) {
                        derivedStateOf {
                            if (currentTrack == null) "-:-/-:-"
                            else
                                "${
                                    if (isIndicatorHovered) formatTime((currentTrack.duration * progress).toLong())
                                    else formatTime(currentTrack.let { position })
                                }/${formatTime(currentTrack.duration)}"
                        }
                    }

                    Text(
                        trackDurationText,
                        fontSize = 10.sp,
                        color = KagaminTheme.colors.buttonIcon
                    )
                }
            }
        }
    }
}
