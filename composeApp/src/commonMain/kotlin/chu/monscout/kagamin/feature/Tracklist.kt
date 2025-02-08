package chu.monscout.kagamin.feature

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.audio.DenpaTrack
import com.github.catomon.yukinotes.feature.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import savePlaylist

@Composable
fun Tracklist(state: KagaminViewModel, tracks: List<DenpaTrack>, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val selected = remember { mutableStateMapOf<Int, DenpaTrack>() }

    LazyColumn(modifier, state = rememberLazyListState()) {
        items(tracks.size) { i ->
            val track = tracks[i]
            val backColor = if (i % 2 == 0) Colors.dividers else Colors.background

            ContextMenuArea(items = {
                listOf(
                    ContextMenuItem("Select") {
                        selected[i] = track
                    },
                    if (selected.isNotEmpty()) {
                        ContextMenuItem("Deselect All") {
                            selected.clear()
                        }
                    } else {
                        ContextMenuItem("Copy URI") {
                            clipboard.setText(AnnotatedString(track.uri))
                        }
                    },
                    ContextMenuItem(if (selected.isNotEmpty()) "Remove selected" else "Remove") {
                        coroutineScope.launch {
                            if (selected.isNotEmpty()) {
                                selected.values.forEach { track ->
                                    state.isLoadingSong = track
                                    state.denpaPlayer.removeFromPlaylist(track)
                                    state.denpaPlayer.playlist.value =
                                        state.denpaPlayer.playlist.value
                                    savePlaylist(
                                        state.currentPlaylistName,
                                        state.denpaPlayer.playlist.value.toTypedArray()
                                    )
                                    //listState.scrollToItem(i, -60)
                                    state.isLoadingSong = null
                                }

                                selected.clear()
                            } else {
                                state.isLoadingSong = track
                                state.denpaPlayer.removeFromPlaylist(track)
                                state.denpaPlayer.playlist.value =
                                    state.denpaPlayer.playlist.value
                                savePlaylist(
                                    state.currentPlaylistName,
                                    state.denpaPlayer.playlist.value.toTypedArray()
                                )
                                //listState.scrollToItem(i, -60)
                                state.isLoadingSong = null
                            }
                        }
                    },
                )
            }) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                        .background(color = backColor)
                        .let {
                            if (state.currentTrack == track) it.border(
                                2.dp,
                                Colors.bars
                            ) else it
                        }.clickable {
                            if (selected.isNotEmpty()) {
                                selected[i] = track
                                return@clickable
                            }
                            if (state.isLoadingSong != null) return@clickable
                            CoroutineScope(Dispatchers.Default).launch {
                                state.isLoadingSong = track
                                state.denpaPlayer.play(track)
                                state.isLoadingSong = null
                            }
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        track.name,
                        fontSize = 12.sp,
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.CenterStart),
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selected.contains(i))
                            Icon(painterResource(Res.drawable.selected), null)
//                    if (Random.nextInt(0, 4) == 1)
//                        Image(
//                            painterResource(Res.drawable.yt_ic),
//                            "",
//                            modifier = Modifier.background(
//                                color = backColor,
//                                shape = RoundedCornerShape(4.dp)
//                            )
//                                .padding(horizontal = 2.dp)
//                        )

//                    Text(
//                        "5:25", //remember { formatMilliseconds(track.duration).take(9) },
//                        fontSize = 12.sp,
//                        color = Colors.noteText,
//                        maxLines = 1,
//                        modifier = Modifier.padding(horizontal = 2.dp).background(
//                            color = backColor,
//                            shape = RoundedCornerShape(4.dp)
//                        )
//                            .padding(horizontal = 2.dp)
//                    )
                    }
                }
            }
        }
    }
}