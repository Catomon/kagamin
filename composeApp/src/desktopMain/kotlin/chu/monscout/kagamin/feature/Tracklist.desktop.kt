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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chu.monscout.kagamin.audio.DenpaTrack
import chu.monscout.kagamin.Colors
import kagamin.composeapp.generated.resources.Res
import kagamin.composeapp.generated.resources.selected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import chu.monscout.kagamin.savePlaylist

@Composable
actual fun TrackItem(
    index: Int,
    tracksManager: TracksManager,
    track: DenpaTrack,
    clipboard: ClipboardManager,
    coroutineScope: CoroutineScope,
    state: KagaminViewModel
) {
    val backColor =
        if (index % 2 == 0) Colors.dividers.copy(alpha = 0.50f) else Colors.background.copy(alpha = 0.50f)
    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Select") {
                tracksManager.select(index, track)
            },
            if (tracksManager.isAnySelected) {
                ContextMenuItem("Deselect All") {
                    tracksManager.deselectAll()
                }
            } else {
                ContextMenuItem("Copy URI") {
                    clipboard.setText(AnnotatedString(track.uri))
                }
            },
            ContextMenuItem(if (tracksManager.isAnySelected) "Remove selected" else "Remove") {
                coroutineScope.launch {
                    if (tracksManager.isAnySelected) {
                        tracksManager.selected.values.forEach { track ->
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

                        tracksManager.deselectAll()
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
                    if (tracksManager.isAnySelected) {
                        tracksManager.select(index, track)
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
                if (tracksManager.selected.contains(index))
                    Icon(painterResource(Res.drawable.selected), null)
            }
        }
    }
}